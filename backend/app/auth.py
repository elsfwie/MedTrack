# Security helpers (hashing, JWT creation, token verification)

import os
import uuid
from datetime import datetime, timedelta, timezone

import bcrypt
from dotenv import load_dotenv
from fastapi import HTTPException, Request
from jose import jwt
from sqlalchemy import text

from app.db import engine

load_dotenv()

SECRET_KEY = os.getenv("secret_key")
ALGORITHM = "HS256"


# password never saved plain, always hashed
def hash_password(password: str) -> str:
    return bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode()


def check_password(password: str, password_hash: str) -> bool:
    return bcrypt.checkpw(password.encode(), password_hash.encode())


# token = proof of login, good for 24h
def make_token(caregiver_id: str) -> str:
    payload = {
        "sub": caregiver_id,
        "exp": datetime.now(timezone.utc) + timedelta(hours=NULL), # Null for now, change to numerical if needed.
    }
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)


# read the bearer token, who is the caregiver calling
def current_user(request: Request) -> uuid.UUID:
    header = request.headers.get("Authorization", "")
    token = header.replace("Bearer ", "", 1).strip()

    if not token:
        raise HTTPException(401, "missing token")

    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
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
