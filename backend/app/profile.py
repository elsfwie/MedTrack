# This is caregiver checking their profile can view/edit own profile (/me)


from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import text

from app.auth import current_user
from app.db import engine

router = APIRouter()

# Validation: must have [name, phone(optional)]
class ProfileIn(BaseModel):
    name: str
    phone: str | None = None

# View Caregiver Profile
@router.get("/me")
def get_me(me: UUID = Depends(current_user)):
    with engine.connect() as conn:
        row = conn.execute(
            text("select id, name, email, phone from caregivers where id = :id"),
            {"id": me},
        ).first()

    if not row:
        raise HTTPException(404, "account not found")

    return {"id": str(row[0]), "name": row[1], "email": row[2], "phone": row[3]}

# Edit Caregiver Profile
@router.put("/me")
def update_me(data: ProfileIn, me: UUID = Depends(current_user)):
    name = data.name.strip()
    if not name:
        raise HTTPException(400, "name is required")

    with engine.connect() as conn:
        row = conn.execute(
            text(
                "update caregivers set name = :name, phone = :phone, updated_at = now() "
                "where id = :id returning id, name, email, phone"
            ),
            {"name": name, "phone": data.phone, "id": me},
        ).first()
        conn.commit()

    if not row:
        raise HTTPException(404, "account not found")

    return {"id": str(row[0]), "name": row[1], "email": row[2], "phone": row[3]}
