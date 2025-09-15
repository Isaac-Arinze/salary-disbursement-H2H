# Quick Start Guide

## 🚀 How to Run the Application

### Prerequisites
- **Java 17 or higher** (Check with: `java -version`)
- **Windows PowerShell** or **Command Prompt**

### Option 1: Using the Run Scripts (Recommended)

#### For Windows Command Prompt:
```cmd
run.bat
```

#### For Windows PowerShell:
```powershell
.\run.ps1
```

### Option 2: Manual Steps

1. **Navigate to the project directory:**
   ```cmd
   cd zikan-salary-disbursement
   ```

2. **Build the application:**
   ```cmd
   mvnw.cmd clean compile
   ```

3. **Run the application:**
   ```cmd
   mvnw.cmd spring-boot:run
   ```

### Option 3: Using Test Profile (Simplified)

If you encounter issues with the full configuration, use the test profile:

```cmd
mvnw.cmd spring-boot:run -Dspring.profiles.active=test
```

## 🔍 Verify the Application is Running

1. **Open your browser** and go to: `http://localhost:8080`

2. **Test the health endpoint:**
   ```
   http://localhost:8080/test/health
   ```

3. **Check application info:**
   ```
   http://localhost:8080/test/info
   ```

## 📝 Test the Salary Processing

### Using REST API:

**POST** `http://localhost:8080/salary/process`
```json
{
  "salaryBatchId": "SAL20250821",
  "companyName": "Oando",
  "companyAccount": "1234567890",
  "salaryDate": "2025-08-21",
  "employees": [
    {
      "employeeId": "EMP001",
      "name": "John Doe",
      "accountNumber": "1111222233",
      "bankCode": "001",
      "amount": 500000
    }
  ]
}
```

### Check Acknowledgements:
**GET** `http://localhost:8080/salary/acknowledgements`

## 🛠️ Troubleshooting

### Issue: "mvn is not recognized"
**Solution:** Use the Maven wrapper (`mvnw.cmd`) instead of `mvn`

### Issue: "Java not found"
**Solution:** 
1. Install Java 17 or higher
2. Add Java to your PATH environment variable
3. Restart your command prompt

### Issue: Build fails
**Solution:** 
1. Check Java version: `java -version`
2. Use test profile: `mvnw.cmd spring-boot:run -Dspring.profiles.active=test`

### Issue: Port 8080 already in use
**Solution:** 
1. Change port in `application.properties`: `server.port=8081`
2. Or stop the process using port 8080

## 📊 What You'll See

When the application starts successfully, you'll see:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

2025-01-XX XX:XX:XX.XXX  INFO --- [main] c.z.s.SalaryDisbursementApplication : Starting SalaryDisbursementApplication
2025-01-XX XX:XX:XX.XXX  INFO --- [main] c.z.s.SalaryDisbursementApplication : Started SalaryDisbursementApplication in X.XXX seconds
```

## 🎯 Next Steps

1. **Test the basic functionality** using the REST API
2. **Configure SFTP** if you need file upload functionality
3. **Set up Infosys integration** for production use
4. **Configure security** for production deployment

## 📞 Need Help?

If you encounter any issues:
1. Check the console output for error messages
2. Verify Java version and installation
3. Try the test profile first
4. Check the logs in the console output
