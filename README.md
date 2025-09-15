# Zikan Salary Disbursement System

A comprehensive host-to-host salary disbursement system that enables corporate clients to upload salary files via SFTP, which are then processed through the bank's system and integrated with Infosys core banking for payment processing.

## Features

### 🔐 Security
- **GPG Encryption**: All files are encrypted before transmission
- **SFTP Protocol**: Secure file transfer protocol
- **Authentication**: Multi-factor authentication for all users
- **Audit Logging**: Complete audit trail of all operations

### ✅ Validation
- **File Format Validation**: Support for CSV, XML, and JSON formats
- **Data Integrity**: Comprehensive validation of all data fields
- **Business Rules**: Validation against banking business rules
- **Maker-Checker**: Dual approval workflow for all transactions

### 🔗 Integration
- **Infosys Core Banking**: Direct integration with Infosys system
- **Real-time Processing**: Immediate processing of payment batches
- **Status Monitoring**: Real-time status updates and notifications
- **Error Handling**: Comprehensive error handling and recovery

### 📊 Monitoring
- **Transaction Tracking**: Complete tracking of all transactions
- **Status Updates**: Real-time status updates for all operations
- **Error Reporting**: Detailed error reporting and logging
- **Performance Monitoring**: System performance monitoring and alerting

## Architecture

The system implements a complete host-to-host integration flow:

1. **File Upload**: Corporate clients upload salary files via SFTP
2. **File Validation**: Comprehensive validation of file format and data
3. **Maker-Checker**: Dual approval workflow for transactions
4. **Payment Processing**: Integration with Infosys core banking system
5. **Acknowledgement**: Detailed acknowledgement files sent back to clients
6. **Database Logging**: Complete transaction logging and audit trail

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- GPG installed and configured
- rclone configured (for cloud storage)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd zikan-salary-disbursement
   ```

2. **Configure GPG keys**
   ```bash
   # Create GPG key pair
   gpg --gen-key
   
   # Export public key
   gpg --armor --export your-email@example.com > ~/zikan/keys/public.asc
   ```

3. **Configure rclone** (optional, for cloud storage)
   ```bash
   rclone config
   ```

4. **Build the application**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## Configuration

### Application Properties

The main configuration is in `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:salarydb
spring.datasource.username=sa
spring.datasource.password=

# SFTP Server Configuration
zikan.sftp.host=localhost
zikan.sftp.port=22
zikan.sftp.username=corporate
zikan.sftp.password=password
zikan.sftp.uploadDir=/uploads
zikan.sftp.processedDir=/processed
zikan.sftp.acknowledgementDir=/acknowledgements

# Infosys Core Banking Integration
zikan.infosys.baseUrl=http://localhost:8081
zikan.infosys.username=bank_user
zikan.infosys.password=bank_password
zikan.infosys.timeout=30000

# File Processing Configuration
zikan.file.processing.enabled=true
zikan.file.processing.pollInterval=5000
zikan.file.processing.maxFileSize=10MB
zikan.file.processing.supportedFormats=csv,xml,json

# Security Configuration
zikan.security.enabled=true
zikan.security.auditLogging=true
zikan.security.encryptionRequired=true
```

## API Usage

### REST API Endpoints

#### Process Salary Batch
```bash
POST /salary/process
Content-Type: application/json

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

#### Get Acknowledgements
```bash
GET /salary/acknowledgements
```

#### Get Specific Acknowledgement
```bash
GET /salary/acknowledgements/{batchId}
```

### SFTP File Upload

Corporate clients can upload salary files directly via SFTP:

1. **Connect to SFTP server**
   ```bash
   sftp corporate@localhost
   ```

2. **Upload salary file**
   ```bash
   put salary_batch.csv /uploads/
   ```

3. **Download acknowledgement**
   ```bash
   get /acknowledgements/ACK_salary_batch.txt
   ```

## File Formats

### CSV Format
```csv
companyName,batchId,companyAccount,salaryDate,generatedAt
Oando,SAL20250821,1234567890,2025-08-21,2025-08-21T10:30:00

employeeId,name,accountNumber,bankCode,amount
EMP001,John Doe,1111222233,001,500000
EMP002,Jane Smith,4444555566,002,450000
```

### JSON Format
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

### XML Format
```xml
<?xml version="1.0" encoding="UTF-8"?>
<salaryBatch>
  <batchId>SAL20250821</batchId>
  <companyName>Oando</companyName>
  <companyAccount>1234567890</companyAccount>
  <salaryDate>2025-08-21</salaryDate>
  <employees>
    <employee>
      <employeeId>EMP001</employeeId>
      <name>John Doe</name>
      <accountNumber>1111222233</accountNumber>
      <bankCode>001</bankCode>
      <amount>500000</amount>
    </employee>
  </employees>
</salaryBatch>
```

## Validation Rules

### File Format Validation
- Supported formats: CSV, XML, JSON
- Maximum file size: 10MB
- Required fields validation

### Data Validation
- **Employee ID**: 6-10 alphanumeric characters
- **Account Number**: Exactly 10 digits
- **Bank Code**: Exactly 3 digits
- **Amount**: Positive number, maximum 1,000,000 (in minor units)

### Business Rules
- Company account must have sufficient funds
- All employees must be valid in HR system
- Bank codes must be valid
- Salary date must be valid

## Error Handling

### Validation Errors
- File format errors
- Data validation errors
- Business rule violations
- Authentication failures

### Processing Errors
- Infosys integration errors
- Database connection errors
- File system errors
- Network connectivity issues

### Recovery Mechanisms
- Automatic retry for transient errors
- Manual intervention for critical errors
- Comprehensive error logging
- Alert notifications for system administrators

## Monitoring and Logging

### Audit Trail
- Complete logging of all file uploads
- Detailed transaction processing logs
- User activity logging
- System access logging

### Performance Monitoring
- Transaction processing times
- File upload/download times
- Database query performance
- System resource usage

### Alerting
- Failed transaction alerts
- System error alerts
- Performance threshold alerts
- Security incident alerts

## Security

### Authentication
- Multi-factor authentication
- Role-based access control
- Session management
- Password policies

### Authorization
- File access permissions
- API endpoint permissions
- Database access permissions
- System configuration permissions

### Encryption
- GPG file encryption
- HTTPS communication
- Database encryption
- Log file encryption

## Deployment

### Environment Setup
- Development environment
- Testing environment
- Staging environment
- Production environment

### Configuration Management
- Environment-specific configurations
- Secret management
- Database configuration
- External service configuration

### Monitoring and Logging
- Application logging
- System monitoring
- Performance monitoring
- Error tracking and alerting

## Troubleshooting

### Common Issues

#### SFTP Connection Issues
- Check SFTP server configuration
- Verify network connectivity
- Check authentication credentials
- Review firewall settings

#### File Processing Issues
- Check file format and content
- Verify validation rules
- Review error logs
- Check system resources

#### Infosys Integration Issues
- Verify Infosys service availability
- Check authentication credentials
- Review API endpoint configuration
- Check network connectivity

### Log Files
- Application logs: `logs/application.log`
- Error logs: `logs/error.log`
- Audit logs: `logs/audit.log`
- Performance logs: `logs/performance.log`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for your changes
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Email: support@zikan.com
- Documentation: [Link to documentation]
- Issue Tracker: [Link to issue tracker]

## Changelog

### Version 1.0.0
- Initial release
- SFTP file upload support
- File validation and processing
- Infosys core banking integration
- Acknowledgement system
- Database logging and audit trail
