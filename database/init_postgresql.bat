@echo off
REM ChriOnline - Initialisation base PostgreSQL
REM Prérequis : PostgreSQL installé, psql dans le PATH

set PGHOST=localhost
set PGPORT=5432
set PGUSER=postgres

echo Creation de la base chrionline si necessaire...
psql -U %PGUSER% -c "SELECT 1 FROM pg_database WHERE datname = 'chrionline'" -t | findstr /C:"1" >nul
if errorlevel 1 (
  psql -U %PGUSER% -c "CREATE DATABASE chrionline;"
)

echo Application du schema...
psql -U %PGUSER% -d chrionline -f "%~dp0schema_postgresql.sql"

echo Termine.
pause
