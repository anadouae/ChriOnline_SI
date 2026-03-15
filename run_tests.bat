@echo off
REM Script de test pour le projet ChriOnline

echo ========================================
echo ChriOnline - Execution des tests
echo ========================================
echo.

setlocal enabledelayedexpansion
cd /d "%~dp0"

REM Verifier que Maven est disponible
where ./mvnw >nul 2>nul
if %errorlevel% neq 0 (
    echo ERREUR: Maven n'est pas trouvé dans le PATH
    echo Veuillez installer Maven ou l'ajouter au PATH
    exit /b 1
)

echo Maven trouve:
./mvnw --version
echo.

REM Nettoyer et compiler
echo Compilation du projet...
./mvnw clean compile -q
if %errorlevel% neq 0 (
    echo ERREUR lors de la compilation
    exit /b 1
)
echo [OK] Compilation reussie
echo.

REM Executer les tests
echo Execution des tests unitaires...
./mvnw test
if %errorlevel% neq 0 (
    echo ERREUR lors de l'execution des tests
    exit /b 1
)

echo.
echo ========================================
echo Tests executes avec succes !
echo ========================================
pause

