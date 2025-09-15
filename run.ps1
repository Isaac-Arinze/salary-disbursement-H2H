Write-Host "Starting Zikan Salary Disbursement System..." -ForegroundColor Green
Write-Host ""

# Check if Java is installed
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java version found: $($javaVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "Error: Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Java 17 or higher" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if Maven wrapper exists
if (-not (Test-Path "mvnw.cmd")) {
    Write-Host "Error: Maven wrapper not found" -ForegroundColor Red
    Write-Host "Please ensure mvnw.cmd is in the current directory" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Building the application..." -ForegroundColor Yellow
& .\mvnw.cmd clean compile
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Build failed" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "Starting the application..." -ForegroundColor Green
Write-Host "The application will be available at: http://localhost:8080" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Yellow
Write-Host ""

& .\mvnw.cmd spring-boot:run

Read-Host "Press Enter to exit"
