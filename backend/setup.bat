@echo off
echo.
echo ==========================================
echo   Saily Backend Setup
echo ==========================================
echo.

REM Check if Node.js is installed
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Node.js is not installed!
    echo Please download and install Node.js from: https://nodejs.org/
    pause
    exit /b 1
)

echo Checking Node.js version...
call node --version

echo.
echo Installing dependencies...
call npm install

echo.
echo Creating .env file...
if not exist .env (
    copy .env.example .env
    echo ✓ .env file created
    echo.
    echo TODO: Edit .env and add your GOOGLE_API_KEY
) else (
    echo ✓ .env already exists
)

echo.
echo ==========================================
echo Setup complete!
echo.
echo Next steps:
echo 1. Edit .env and add your GOOGLE_API_KEY from https://ai.google.dev
echo 2. Run: npm start
echo ==========================================
echo.

pause
