# Caregiver search, friend requests, accept/decline

from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import text

from app.auth import current_user
from app.db import engine

router = APIRouter()

# Validation
class ConnectionIn(BaseModel):
    caregiver_id: UUID


## Caregiver to Caregiver Friend System ##

# Searches for other caregivers by name or email (ignores yourself). checks the 'connections' table to verify if you are friends with another caretaker.
@router.get("/caregivers")
def search_caregivers(q: str = "", me: UUID = Depends(current_user)):
    term = f"%{q.strip().lower()}%"
    with engine.connect() as conn:
        rows = conn.execute(
            text(
                "select c.id, c.name, c.email, "
                "case when cn.id is null then null "
                "when cn.status = 'accepted' then 'accepted' "
                "when cn.requester_id = :me then 'sent' "
                "else 'received' end as status "
                "from caregivers c "
                "left join connections cn on cn.status in ('pending', 'accepted') "
                "and ((cn.requester_id = :me and cn.addressee_id = c.id) "
                "or (cn.addressee_id = :me and cn.requester_id = c.id)) "
                "where c.id <> :me "
                "and (lower(c.name) like :term or lower(c.email) like :term) "
                "order by c.name limit 20"
            ),
            {"me": me, "term": term},
        ).fetchall()

    return [
        {"id": str(r[0]), "name": r[1], "email": r[2], "status": r[3]}
        for r in rows
    ]

# Sends a friends reqeust. With validation.
@router.post("/connections", status_code=201)
def send_connection(data: ConnectionIn, me: UUID = Depends(current_user)):
    if data.caregiver_id == me:
        raise HTTPException(400, "cannot connect to yourself")

    with engine.connect() as conn:
        target = conn.execute(
            text("select 1 from caregivers where id = :id"),
            {"id": data.caregiver_id},
        ).first()
        if not target:
            raise HTTPException(404, "caregiver not found")

        existing = conn.execute(
            text(
                "select 1 from connections "
                "where (requester_id = :me and addressee_id = :other) "
                "or (requester_id = :other and addressee_id = :me)"
            ),
            {"me": me, "other": data.caregiver_id},
        ).first()
        if existing:
            raise HTTPException(400, "already connected or request pending")

        conn.execute(
            text(
                "insert into connections (requester_id, addressee_id, status) "
                "values (:me, :other, 'pending')"
            ),
            {"me": me, "other": data.caregiver_id},
        )
        conn.commit()

    return {"status": "pending"}

# List connections. Fetches all your avaibale connection it rows and sorts then into three list.
@router.get("/connections")
def list_connections(me: UUID = Depends(current_user)):
    with engine.connect() as conn:
        rows = conn.execute(
            text(
                "select cn.id, cn.status, cn.requester_id, c.id, c.name, c.email "
                "from connections cn "
                "join caregivers c on c.id = case "
                "when cn.requester_id = :me then cn.addressee_id "
                "else cn.requester_id end "
                "where cn.requester_id = :me or cn.addressee_id = :me "
                "order by cn.created_at"
            ),
            {"me": me},
        ).fetchall()

    received, sent, connected = [], [], []
    for r in rows:
        entry = {
            "connection_id": str(r[0]),
            "caregiver_id": str(r[3]),
            "name": r[4],
            "email": r[5],
        }
        if r[1] == "accepted":
            connected.append(entry)
        elif r[2] == me:
            sent.append(entry)
        else:
            received.append(entry)

    return {"received": received, "sent": sent, "connected": connected}

# Accepting friend requests.
@router.post("/connections/{connection_id}/accept")
def accept_connection(connection_id: UUID, me: UUID = Depends(current_user)):
    with engine.connect() as conn:
        row = conn.execute(
            text(
                "update connections set status = 'accepted', updated_at = now() "
                "where id = :id and addressee_id = :me and status = 'pending' "
                "returning id"
            ),
            {"id": connection_id, "me": me},
        ).first()
        conn.commit()

    if not row:
        raise HTTPException(404, "request not found")

    return {"status": "accepted"}

# Rejecting/Declining a friend request. Entirly deleting.
@router.post("/connections/{connection_id}/decline")
def decline_connection(connection_id: UUID, me: UUID = Depends(current_user)):
    with engine.connect() as conn:
        # row gone means they can ask again later
        row = conn.execute(
            text(
                "delete from connections "
                "where id = :id and addressee_id = :me and status = 'pending' "
                "returning id"
            ),
            {"id": connection_id, "me": me},
        ).first()
        conn.commit()

    if not row:
        raise HTTPException(404, "request not found")

    return {"status": "declined"}
