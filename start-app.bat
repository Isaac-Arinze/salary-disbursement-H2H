@echo off
echo ========================================
echo   Zikan Salary Disbursement System
echo ========================================
echo.

echo Starting the application...
echo This may take a few moments to download dependencies and start...
echo.

echo The application will be available at: http://localhost:8080
echo.
echo Test endpoints:
echo - Health Check: http://localhost:8080/test/health
echo - Application Info: http://localhost:8080/test/info
echo.

echo Press Ctrl+C to stop the application
echo.

cd zikan-salary-disbursement
call ..\mvnw.cmd spring-boot:run

pause
