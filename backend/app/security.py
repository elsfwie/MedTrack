# Security helpers (Supabase JWT verification, patient device check)

import os
import uuid

import requests
from dotenv import load_dotenv
from fastapi import HTTPException, Request
from jose import jwk, jwt
from sqlalchemy import text

from app.db import engine

load_dotenv()

SUPABASE_URL = os.getenv("supabase_url")

_jwks = None


# supabase signs tokens with ES256, public keys live at their jwks url
def _fetch_jwks() -> dict:
    r = requests.get(f"{SUPABASE_URL}/auth/v1/.well-known/jwks.json", timeout=10)
    r.raise_for_status()
    return {k["kid"]: jwk.construct(k) for k in r.json()["keys"]}


def _key_for(kid):
    global _jwks
    if _jwks is None:
        _jwks = _fetch_jwks()
    if kid not in _jwks:
        _jwks = _fetch_jwks()  # key rotated, grab fresh set once
    return _jwks.get(kid)


# read the bearer token, who is the caregiver calling
def current_user(request: Request) -> uuid.UUID:
    header = request.headers.get("Authorization", "")
    token = header.replace("Bearer ", "", 1).strip()

    if not token:
        raise HTTPException(401, "missing token")

    try:
        kid = jwt.get_unverified_header(token).get("kid")
        key = _key_for(kid)
        if not key:
            raise ValueError("unknown signing key")
        payload = jwt.decode(
            token,
            key,
            algorithms=["ES256"],
            audience="authenticated",
            options={"verify_exp": False},  # no expiry for now, drop this line when sessions should expire
        )
        return uuid.UUID(payload["sub"])
    except Exception:
        raise HTTPException(401, "invalid or expired token")


# patient has no token, device match does the login
def current_patient(request: Request) -> uuid.UUID:
    patient_id = request.headers.get("X-Patient-Id", "")
    device_id = request.headers.get("X-Device-Id", "")

    if not patient_id or not device_id:
        raise HTTPException(401, "missing patient headers")

    try:
        patient_id = uuid.UUID(patient_id)
    except ValueError:
        raise HTTPException(401, "invalid patient headers")

    with engine.connect() as conn:
        row = conn.execute(
            text("select 1 from patients where id = :id and device_id = :device"),
            {"id": patient_id, "device": device_id},
        ).first()

    if not row:
        raise HTTPException(401, "device not recognized")

    return patient_id
