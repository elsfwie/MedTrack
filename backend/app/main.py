# Entry point, initializes FastAPI and includes all routers

from fastapi import FastAPI

from app import account, connections, medications, notifications, patient_auth, patients, profile

app = FastAPI(title="MedTrack API")

app.include_router(account.router)
app.include_router(profile.router)
app.include_router(patients.router)
app.include_router(connections.router)
app.include_router(patient_auth.router)
app.include_router(medications.router)
app.include_router(notifications.router)


@app.get("/health")
def health():
    return {
        "status": "ok",
        "message": "MEDTRACK API IS LIVE"
    }
