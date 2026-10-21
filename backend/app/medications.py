# meds schedule - caregiver manages it, patient marks taken or skipped


import re
from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import text

from app.auth import current_patient, current_user
from app.db import engine

router = APIRouter()

# owner or anyone the patient was shared with
ACCESS_SQL = (
    "select 1 from patients p "
    "left join shared_patients s on s.patient_id = p.id and s.caregiver_id = :me and s.revoked_at is null "
    "where p.id = :pid and (p.created_by = :me or s.caregiver_id = :me)"
)

# only the owner gets to change things
OWNER_SQL = "select 1 from patients where id = :pid and created_by = :me"


class MedicationIn(BaseModel):
    name: str
    dosage: str | None = None
    time: str | None = None


class LogIn(BaseModel):
    status: str


@router.get("/patients/{patient_id}/medications")
def list_medications(patient_id: UUID, me: UUID = Depends(current_user)):
    with engine.connect() as conn:
        access = conn.execute(text(ACCESS_SQL), {"pid": patient_id, "me": me}).first()
        if not access:
            raise HTTPException(404, "patient not found")

        rows = conn.execute(
            text(
                "select id, name, dosage, time from medications "
                "where patient_id = :pid order by time"
            ),
            {"pid": patient_id},
        ).fetchall()

    return [
        {
            "id": str(r[0]),
            "name": r[1],
            "dosage": r[2],
            "time": str(r[3]) if r[3] else None,
        }
        for r in rows
    ]


@router.post("/patients/{patient_id}/medications", status_code=201)
def add_medication(patient_id: UUID, data: MedicationIn, me: UUID = Depends(current_user)):
    name = data.name.strip()
    if not name:
        raise HTTPException(400, "name is required")

    # time comes in as "08:00"
    if data.time and not re.match(r"^\d{1,2}:\d{2}$", data.time):
        raise HTTPException(400, "time must look like HH:MM")

    with engine.connect() as conn:
        owner = conn.execute(text(OWNER_SQL), {"pid": patient_id, "me": me}).first()
        if not owner:
            raise HTTPException(404, "patient not found")

        row = conn.execute(
            text(
                "insert into medications (patient_id, name, dosage, time, created_by) "
                "values (:pid, :name, :dosage, :time, :me) returning id"
            ),
            {
                "pid": patient_id,
                "name": name,
                "dosage": data.dosage,
                "time": data.time,
                "me": me,
            },
        ).first()
        conn.commit()

    return {"id": str(row[0]), "name": name, "dosage": data.dosage, "time": data.time}


@router.delete("/medications/{medication_id}")
def delete_medication(medication_id: UUID, me: UUID = Depends(current_user)):
    with engine.connect() as conn:
        owner = conn.execute(
            text(
                "select 1 from medications m join patients p on p.id = m.patient_id "
                "where m.id = :mid and p.created_by = :me"
            ),
            {"mid": medication_id, "me": me},
        ).first()
        if not owner:
            raise HTTPException(404, "medication not found")

        # logs point at the med, clear them first
        conn.execute(
            text("delete from medications_logs where medication_id = :mid"),
            {"mid": medication_id},
        )
        conn.execute(
            text("delete from medications where id = :mid"),
            {"mid": medication_id},
        )
        conn.commit()

    return {"status": "deleted"}


@router.get("/patient/medications")
def my_medications(patient_id: UUID = Depends(current_patient)):
    with engine.connect() as conn:
        rows = conn.execute(
            text(
                "select m.id, m.name, m.dosage, m.time, l.status "
                "from medications m "
                "left join medications_logs l "
                "on l.medication_id = m.id and l.date = current_date "
                "where m.patient_id = :pid order by m.time"
            ),
            {"pid": patient_id},
        ).fetchall()

    return [
        {
            "id": str(r[0]),
            "name": r[1],
            "dosage": r[2],
            "time": str(r[3]) if r[3] else None,
            "status_today": r[4],
        }
        for r in rows
    ]


@router.post("/medications/{medication_id}/log")
def log_medication(medication_id: UUID, data: LogIn, patient_id: UUID = Depends(current_patient)):
    if data.status not in ("taken", "skipped"):
        raise HTTPException(400, "status must be taken or skipped")

    with engine.connect() as conn:
        mine = conn.execute(
            text("select 1 from medications where id = :mid and patient_id = :pid"),
            {"mid": medication_id, "pid": patient_id},
        ).first()
        if not mine:
            raise HTTPException(404, "medication not found")

        # one entry per med per day
        already = conn.execute(
            text(
                "select 1 from medications_logs "
                "where medication_id = :mid and date = current_date"
            ),
            {"mid": medication_id},
        ).first()
        if already:
            raise HTTPException(400, "already logged today")

        conn.execute(
            text(
                "insert into medications_logs (medication_id, status) "
                "values (:mid, :status)"
            ),
            {"mid": medication_id, "status": data.status},
        )
        conn.commit()

    return {"medication_id": str(medication_id), "status": data.status}
