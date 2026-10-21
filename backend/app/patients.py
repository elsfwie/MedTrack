# Caregiver tools for patients, sharing, and generating codes

import hashlib
import secrets
from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import text

from app.auth import current_user
from app.db import engine

router = APIRouter()

# Validation
class PatientIn(BaseModel):
    name: str
    age: int
    emergency_contact: str | None = None


class ShareIn(BaseModel):
    caregiver_id: UUID

# Lists patients (logged in). Fetches all patients can or have access.
@router.get("/patients")
def list_patients(me: UUID = Depends(current_user)):
    with engine.connect() as conn:
        rows = conn.execute(
            text(
                "select p.id, p.name, p.age, p.emergency_contact, c.name "
                "from patients p "
                "left join shared_patients s "
                "on s.patient_id = p.id and s.caregiver_id = :me and s.revoked_at is null "
                "left join caregivers c on c.id = s.shared_by "
                "where p.created_by = :me or s.caregiver_id = :me "
                "order by p.created_at"
            ),
            {"me": me},
        ).fetchall()

    return [
        {
            "id": str(r[0]),
            "name": r[1],
            "age": r[2],
            "emergency_contact": r[3],
            "shared_by": r[4],
        }
        for r in rows
    ]

# Add patients (logged in). Creates new patient in the database.
@router.post("/patients", status_code=201)
def add_patient(data: PatientIn, me: UUID = Depends(current_user)):
    name = data.name.strip()
    if not name or data.age < 0:
        raise HTTPException(400, "name and age are required")

    with engine.connect() as conn:
        row = conn.execute(
            text(
                "insert into patients (name, age, emergency_contact, created_by) "
                "values (:name, :age, :emergency_contact, :me) returning id"
            ),
            {
                "name": name,
                "age": data.age,
                "emergency_contact": data.emergency_contact,
                "me": me,
            },
        ).first()
        conn.commit()

    return {
        "id": str(row[0]),
        "name": name,
        "age": data.age,
        "emergency_contact": data.emergency_contact,
    }

# Edit patients (logged in). Updates a patient's name, age, and emergen contact. Only original creator can their created patient.
@router.put("/patients/{patient_id}")
def edit_patient(patient_id: UUID, data: PatientIn, me: UUID = Depends(current_user)):
    name = data.name.strip()
    if not name or data.age < 0:
        raise HTTPException(400, "name and age are required")

    with engine.connect() as conn:
        row = conn.execute(
            text(
                "update patients set name = :name, age = :age, "
                "emergency_contact = :emergency_contact, updated_at = now() "
                "where id = :id and created_by = :me returning id"
            ),
            {
                "id": patient_id,
                "me": me,
                "name": name,
                "age": data.age,
                "emergency_contact": data.emergency_contact,
            },
        ).first()
        conn.commit()

    if not row:
        raise HTTPException(404, "patient not found")

    return {"id": str(row[0]), "name": name, "age": data.age}

# Generates claim code for patient (logged in). Generates a 6 characther code. Hasehs it and saves into token. No expiry.
@router.post("/patients/{patient_id}/code")
def make_patient_code(patient_id: UUID, me: UUID = Depends(current_user)):
    with engine.connect() as conn:
        access = conn.execute(
            text(
                "select 1 from patients p "
                "left join shared_patients s "
                "on s.patient_id = p.id and s.caregiver_id = :me and s.revoked_at is null "
                "where p.id = :id and (p.created_by = :me or s.caregiver_id = :me)"
            ),
            {"id": patient_id, "me": me},
        ).first()

        if not access:
            raise HTTPException(404, "patient not found")

        # raw code leaves here only, db keeps the hash
        code = secrets.token_hex(3).upper()
        conn.execute(
            text(
                "insert into tokens (token_hash, patient_id, created_by) "
                "values (:token_hash, :patient_id, :me)"
            ),
            {
                "token_hash": hashlib.sha256(code.encode()).hexdigest(),
                "patient_id": patient_id,
                "me": me,
            },
        )
        conn.commit()

    return {"code": code}

# Share an existing patient to another caregiver. With validation.
@router.post("/patients/{patient_id}/share")
def share_patient(patient_id: UUID, data: ShareIn, me: UUID = Depends(current_user)):
    if data.caregiver_id == me:
        raise HTTPException(400, "cannot share to yourself")

    with engine.connect() as conn:
        own = conn.execute(
            text("select 1 from patients where id = :id and created_by = :me"),
            {"id": patient_id, "me": me},
        ).first()
        if not own:
            raise HTTPException(404, "patient not found")

        # no sharing strangers
        connected = conn.execute(
            text(
                "select 1 from connections "
                "where status = 'accepted' "
                "and ((requester_id = :me and addressee_id = :other) "
                "or (requester_id = :other and addressee_id = :me))"
            ),
            {"me": me, "other": data.caregiver_id},
        ).first()
        if not connected:
            raise HTTPException(400, "you are not connected with this caregiver")

        already = conn.execute(
            text(
                "select 1 from shared_patients "
                "where caregiver_id = :other and patient_id = :id and revoked_at is null"
            ),
            {"other": data.caregiver_id, "id": patient_id},
        ).first()
        if already:
            raise HTTPException(400, "patient already shared with this caregiver")

        conn.execute(
            text(
                "insert into shared_patients (caregiver_id, patient_id, shared_by) "
                "values (:other, :id, :me)"
            ),
            {"other": data.caregiver_id, "id": patient_id, "me": me},
        )
        conn.commit()

    return {"status": "shared"}
