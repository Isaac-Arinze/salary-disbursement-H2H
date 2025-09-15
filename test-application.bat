@echo off
echo =========================================
echo   Zikan Salary Disbursement System
echo   Testing Script
echo =========================================
echo.

echo Testing the application endpoints...
echo.

echo 1. Testing Health Check...
curl -s http://localhost:8080/test/health
echo.
echo.

echo 2. Testing Application Info...
curl -s http://localhost:8080/test/info
echo.
echo.

echo 3. Testing Salary Processing...
curl -s -X POST http://localhost:8080/salary/process ^
  -H "Content-Type: application/json" ^
  -d "{\"salaryBatchId\":\"SAL20250821\",\"companyName\":\"Oando\",\"companyAccount\":\"1234567890\",\"salaryDate\":\"2025-08-21\",\"employees\":[{\"employeeId\":\"EMP001\",\"name\":\"John Doe\",\"accountNumber\":\"1111222233\",\"bankCode\":\"001\",\"amount\":500000}]}"
echo.
echo.

echo 4. Testing Acknowledgements...
curl -s http://localhost:8080/salary/acknowledgements
echo.
echo.

echo =========================================
echo   Testing Complete!
echo =========================================
echo.
echo Additional Testing Options:
echo 1. H2 Database Console: http://localhost:8080/h2-console
echo 2. JDBC URL: jdbc:h2:mem:zikandb
echo 3. Username: sa
echo 4. Password: (leave empty)
echo.
echo Manual Testing URLs:
echo - Health: http://localhost:8080/test/health
echo - Info: http://localhost:8080/test/info
echo - Acknowledgements: http://localhost:8080/salary/acknowledgements
echo.

pause
