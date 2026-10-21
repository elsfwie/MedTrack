# Patient claim code endpoint (binds device)

import hashlib
from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import text

from app.auth import current_patient
from app.db import engine

router = APIRouter()


class ClaimIn(BaseModel):
    code: str
    device_id: str
    age: int


@router.post("/claim")
def claim_patient(data: ClaimIn):
    code = data.code.strip().upper()
    device_id = data.device_id.strip()

    if not code or not device_id:
        raise HTTPException(400, "code and device_id are required")

    token_hash = hashlib.sha256(code.encode()).hexdigest()

    with engine.connect() as conn:
        token = conn.execute(
            text(
                "select patient_id from tokens "
                "where token_hash = :token_hash "
                "and used_at is null and revoked_at is null"
            ),
            {"token_hash": token_hash},
        ).first()

        if not token:
            raise HTTPException(400, "invalid or already used code")

        patient_id = token[0]
        age = conn.execute(
            text("select age from patients where id = :patient_id"),
            {"patient_id": patient_id},
        ).scalar()

        # wrong age just bounces back, code survives
        if data.age != age:
            raise HTTPException(400, "age does not match")

        used = conn.execute(
            text(
                "update tokens set used_at = now() "
                "where token_hash = :token_hash and used_at is null "
                "returning id"
            ),
            {"token_hash": token_hash},
        ).first()
        if not used:
            raise HTTPException(400, "invalid or already used code")

        # claim done, this device now owns the patient
        patient = conn.execute(
            text(
                "update patients set device_id = :device_id, updated_at = now() "
                "where id = :patient_id "
                "returning id, name, age, emergency_contact"
            ),
            {"device_id": device_id, "patient_id": patient_id},
        ).first()
        conn.commit()

    return {
        "patient_id": str(patient[0]),
        "name": patient[1],
        "age": patient[2],
        "emergency_contact": patient[3],
        "device_id": device_id,
    }


# profile screen on the patient side
@router.get("/patient/me")
def patient_me(patient_id: UUID = Depends(current_patient)):
    with engine.connect() as conn:
        row = conn.execute(
            text("select id, name, age, emergency_contact from patients where id = :id"),
            {"id": patient_id},
        ).first()

    if not row:
        raise HTTPException(404, "patient not found")

    return {
        "patient_id": str(row[0]),
        "name": row[1],
        "age": row[2],
        "emergency_contact": row[3],
    }
