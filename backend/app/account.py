# Caregiver register & login endpoints

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from sqlalchemy import text

from app.auth import check_password, hash_password, make_token
from app.db import engine

router = APIRouter()


class RegisterIn(BaseModel):
    name: str
    email: str
    password: str


class LoginIn(BaseModel):
    email: str
    password: str


@router.post("/register", status_code=201)
def register(data: RegisterIn):
    name = data.name.strip()
    email = data.email.strip().lower()

    if not name or "@" not in email or len(data.password) < 6:
        raise HTTPException(400, "name, a valid email and a password of 6+ characters are required")

    with engine.connect() as conn:
        taken = conn.execute(
            text("select 1 from caregivers where email = :email"),
            {"email": email},
        ).first()

        if taken:
            raise HTTPException(400, "email already registered")

        # only the hash goes in, never the password
        row = conn.execute(
            text(
                "insert into caregivers (name, email, password_hash) "
                "values (:name, :email, :password_hash) returning id"
            ),
            {
                "name": name,
                "email": email,
                "password_hash": hash_password(data.password),
            },
        ).first()
        conn.commit()

    return {"id": str(row[0]), "name": name, "email": email}


@router.post("/login")
def login(data: LoginIn):
    email = data.email.strip().lower()

    with engine.connect() as conn:
        row = conn.execute(
            text("select id, password_hash from caregivers where email = :email"),
            {"email": email},
        ).first()

    # one same error for wrong email and wrong password
    if not row or not check_password(data.password, row[1]):
        raise HTTPException(401, "wrong email or password")

    return {"access_token": make_token(str(row[0])), "token_type": "bearer"}
