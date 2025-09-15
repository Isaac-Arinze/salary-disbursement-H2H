@echo off
echo Starting Zikan Salary Disbursement System...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

REM Check if Maven wrapper exists
if not exist "mvnw.cmd" (
    echo Error: Maven wrapper not found
    echo Please ensure mvnw.cmd is in the current directory
    pause
    exit /b 1
)

echo Building the application...
call mvnw.cmd clean compile
if %errorlevel% neq 0 (
    echo Error: Build failed
    pause
    exit /b 1
)

echo.
echo Starting the application...
echo The application will be available at: http://localhost:8080
echo.
echo Press Ctrl+C to stop the application
echo.

call mvnw.cmd spring-boot:run

pause
