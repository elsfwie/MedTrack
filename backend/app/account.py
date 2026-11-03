# Caregiver register & login — proxied to Supabase Auth

import os

import requests
from dotenv import load_dotenv
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from sqlalchemy import text

from app.db import engine

load_dotenv()

router = APIRouter()

SUPABASE_URL = os.getenv("supabase_url")
SERVICE_ROLE = os.getenv("supabase_service_role")

# service key never leaves this backend
HEADERS = {"apikey": SERVICE_ROLE, "Authorization": f"Bearer {SERVICE_ROLE}"}


class RegisterIn(BaseModel):
    name: str
    email: str
    password: str


class LoginIn(BaseModel):
    email: str
    password: str


class RefreshIn(BaseModel):
    refresh_token: str


@router.post("/register", status_code=201)
def register(data: RegisterIn):
    name = data.name.strip()
    email = data.email.strip().lower()

    if not name or "@" not in email or len(data.password) < 6:
        raise HTTPException(400, "name, a valid email and a password of 6+ characters are required")

    # old rows own their email too, block before supabase leaves an orphan
    with engine.connect() as conn:
        taken = conn.execute(
            text("select 1 from caregivers where email = :email"),
            {"email": email},
        ).first()

    if taken:
        raise HTTPException(400, "email already registered")

    r = requests.post(
        f"{SUPABASE_URL}/auth/v1/signup",
        headers=HEADERS,
        json={"email": email, "password": data.password, "data": {"name": name}},
        timeout=15,
    )
    if r.status_code >= 400:
        body = r.json() if r.text else {}
        if "already" in str(body).lower():
            raise HTTPException(400, "email already registered")
        raise HTTPException(400, body.get("msg", "signup failed") if isinstance(body, dict) else "signup failed")

    # profile row keys off the supabase user id
    user = r.json()["user"]
    with engine.connect() as conn:
        row = conn.execute(
            text(
                "insert into caregivers (id, name, email) "
                "values (:id, :name, :email) returning id"
            ),
            {"id": user["id"], "name": name, "email": email},
        ).first()
        conn.commit()

    return {"id": str(row[0]), "name": name, "email": email}


@router.post("/login")
def login(data: LoginIn):
    r = requests.post(
        f"{SUPABASE_URL}/auth/v1/token?grant_type=password",
        headers=HEADERS,
        json={"email": data.email.strip().lower(), "password": data.password},
        timeout=15,
    )

    # one same error for wrong email and wrong password
    if r.status_code >= 400:
        raise HTTPException(401, "wrong email or password")

    body = r.json()
    return {
        "access_token": body["access_token"],
        "refresh_token": body["refresh_token"],
        "token_type": "bearer",
    }


# android hits this when its supabase token runs out (~1h)
@router.post("/refresh")
def refresh(data: RefreshIn):
    r = requests.post(
        f"{SUPABASE_URL}/auth/v1/token?grant_type=refresh_token",
        headers=HEADERS,
        json={"refresh_token": data.refresh_token},
        timeout=15,
    )
    if r.status_code >= 400:
        raise HTTPException(401, "invalid or expired token")

    body = r.json()
    return {
        "access_token": body["access_token"],
        "refresh_token": body["refresh_token"],
        "token_type": "bearer",
    }
