@echo off
setlocal
title MedTrack Backend Setup
cd /d "%~dp0"

echo.
echo Welcome %USERNAME%
echo.

where python >nul 2>nul
if errorlevel 1 (
    echo Python not found on this PC. Install Python first, then run this again.
    pause
    exit /b 1
)

if exist ".venv\Scripts\python.exe" (
    echo venv already there. Skipping.
) else (
    echo Making venv...
    python -m venv .venv
    if errorlevel 1 (
        echo Failed to make venv.
        pause
        exit /b 1
    )
)

set "NEEDS_INSTALL=1"
".venv\Scripts\python.exe" -c "import fastapi, uvicorn, sqlalchemy, psycopg, pydantic, dotenv, requests, jose" >nul 2>nul
if not errorlevel 1 set "NEEDS_INSTALL=0"

if "%NEEDS_INSTALL%"=="1" (
    echo Installing requirements...
    ".venv\Scripts\python.exe" -m pip install -q -r requirements.txt
    if errorlevel 1 (
        echo Install failed. Check your internet.
        pause
        exit /b 1
    )
) else (
    echo Requirements already installed. Skipping.
)

rem ---- .env: copy template if missing, then fill in secrets ----
if not exist ".env" (
    copy /y ".env.example" ".env" >nul
    echo Made .env from template.
)

findstr /c:"password=<your-db-password>" ".env" >nul 2>nul
if errorlevel 1 goto skippass
echo.
echo DB password is still the placeholder. Ask your team lead for the Supabase password.
:askpass
set "DBPASS="
set /p "DBPASS=Paste it here: "
if not defined DBPASS goto askpass
powershell -NoProfile -Command "(Get-Content -LiteralPath '.env') | ForEach-Object { $_.Replace('password=<your-db-password>', 'password=' + $env:DBPASS) } | Set-Content -LiteralPath '.env' -Encoding ASCII"
set "DBPASS="
echo Password saved.
:skippass

findstr /c:"supabase_service_role=<your-service-role-key>" ".env" >nul 2>nul
if errorlevel 1 goto skipkey2
echo.
echo Supabase service role key is still the placeholder. Ask your team lead (Supabase dashboard - Settings - API - service_role).
:askkey
set "SVCKEY="
set /p "SVCKEY=Paste it here: "
if not defined SVCKEY goto askkey
powershell -NoProfile -Command "(Get-Content -LiteralPath '.env') | ForEach-Object { $_.Replace('supabase_service_role=<your-service-role-key>', 'supabase_service_role=' + $env:SVCKEY) } | Set-Content -LiteralPath '.env' -Encoding ASCII"
set "SVCKEY="
echo Service role key saved.
:skipkey2

echo.
call .venv\Scripts\activate.bat
choice /c YN /m "Start backend server now? "

if errorlevel 2 goto shell

echo.
echo Server on http://127.0.0.1:8000   docs at http://127.0.0.1:8000/docs
echo Press Ctrl+C to stop it.
echo.
uvicorn app.main:app --reload

:shell
echo.
echo venv is on. Run uvicorn again if you need it. Type exit to close.
echo.
cmd /k
endlocal
