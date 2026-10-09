from fastapi import FastAPI

app = FastAPI(title="MedTrack API")

@app.get("/health")
def health():
    return {"status": "ok"}
