# Handles the reminders (notifications) from caregivers to the patients. Caregiver to send, patient to read.


from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import text

from app.auth import current_patient, current_user
from app.db import engine

router = APIRouter()

# need access to the patient to remind them
ACCESS_SQL = (
    "select 1 from patients p "
    "left join shared_patients s on s.patient_id = p.id and s.caregiver_id = :me and s.revoked_at is null "
    "where p.id = :pid and (p.created_by = :me or s.caregiver_id = :me)"
)


class ReminderIn(BaseModel):
    message: str


@router.post("/patients/{patient_id}/reminders", status_code=201)
def send_reminder(patient_id: UUID, data: ReminderIn, me: UUID = Depends(current_user)):
    message = data.message.strip()
    if not message:
        raise HTTPException(400, "message is required")

    with engine.connect() as conn:
        access = conn.execute(text(ACCESS_SQL), {"pid": patient_id, "me": me}).first()
        if not access:
            raise HTTPException(404, "patient not found")

        row = conn.execute(
            text(
                "insert into notifications (patient_id, sent_by, message) "
                "values (:pid, :me, :message) returning id"
            ),
            {"pid": patient_id, "me": me, "message": message},
        ).first()
        conn.commit()

    return {"id": str(row[0]), "message": message}


@router.get("/patient/notifications")
def my_notifications(patient_id: UUID = Depends(current_patient)):
    with engine.connect() as conn:
        rows = conn.execute(
            text(
                "select n.id, n.message, n.created_at, c.name "
                "from notifications n "
                "join caregivers c on c.id = n.sent_by "
                "where n.patient_id = :pid "
                "order by n.created_at desc limit 50"
            ),
            {"pid": patient_id},
        ).fetchall()

    return [
        {
            "id": str(r[0]),
            "message": r[1],
            "created_at": r[2],
            "sent_by": r[3],
        }
        for r in rows
    ]
