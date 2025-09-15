# Host-to-Host Salary Disbursement System Architecture

## Overview
This document describes the complete architecture for the host-to-host salary disbursement system that enables corporate clients (like Oando) to upload salary files via SFTP, which are then processed through the bank's system and integrated with Infosys core banking for payment processing.

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           CORPORATE CLIENT (Oando)                              │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐            │
│  │   Payroll       │    │   File          │    │   SFTP          │            │
│  │   Manager       │    │   Generator     │    │   Client        │            │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘            │
└─────────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ 1. Upload Salary File (CSV/XML/JSON)
                                    │    via SFTP
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                            BANK'S SYSTEM                                        │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐            │
│  │   SFTP          │    │   File          │    │   Maker-        │            │
│  │   Server        │    │   Validation    │    │   Checker       │            │
│  │   (Apache SSHD) │    │   Service       │    │   Workflow      │            │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘            │
│                                    │                                           │
│                                    │ 2. File Processing & Validation          │
│                                    ▼                                           │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐            │
│  │   GPG           │    │   Database      │    │   Audit         │            │
│  │   Encryption    │    │   Logging       │    │   Logging       │            │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘            │
└─────────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ 3. Process Payment
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        INFOSYS CORE BANKING SYSTEM                             │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐            │
│  │   Payment       │    │   Transaction   │    │   Status        │            │
│  │   Processing    │    │   Monitoring    │    │   Updates       │            │
│  │   Engine        │    │   System        │    │   Service       │            │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘            │
└─────────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ 4. Payment Status & Acknowledgement
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                            BANK'S SYSTEM                                        │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐            │
│  │   Acknowledgement│    │   Database      │    │   SFTP          │            │
│  │   Generator      │    │   Storage       │    │   Server        │            │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘            │
└─────────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ 5. Send Acknowledgement File
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           CORPORATE CLIENT (Oando)                              │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐            │
│  │   SFTP          │    │   Acknowledgement│    │   Payroll       │            │
│  │   Client        │    │   Processor     │    │   Manager       │            │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘            │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## Detailed Component Flow

### 1. File Upload Phase
- **Corporate Client**: Payroll manager generates salary file (CSV/XML/JSON)
- **SFTP Upload**: File is uploaded to bank's SFTP server
- **File Monitoring**: Bank's system monitors SFTP directory for new files

### 2. File Processing Phase
- **File Validation**: 
  - Format validation (CSV, XML, JSON)
  - Data integrity checks (account numbers, amounts, employee IDs)
  - Business rule validation (sufficient funds, valid bank codes)
- **Maker-Checker Workflow**:
  - Maker reviews and validates the file
  - Checker approves or rejects the transaction
  - Audit trail is maintained

### 3. Payment Processing Phase
- **GPG Encryption**: File is encrypted for security
- **Infosys Integration**: Payment batch is sent to Infosys core banking system
- **Transaction Processing**: Individual employee payments are processed
- **Status Monitoring**: Real-time status updates are received

### 4. Acknowledgement Phase
- **Status Aggregation**: Individual transaction statuses are collected
- **Acknowledgement Generation**: Detailed acknowledgement file is created
- **Database Logging**: All transactions and statuses are logged
- **Client Notification**: Acknowledgement file is sent back via SFTP

## Key Features

### Security
- **GPG Encryption**: All files are encrypted before transmission
- **SFTP Protocol**: Secure file transfer protocol
- **Authentication**: Multi-factor authentication for all users
- **Audit Logging**: Complete audit trail of all operations

### Validation
- **File Format Validation**: Support for CSV, XML, and JSON formats
- **Data Integrity**: Comprehensive validation of all data fields
- **Business Rules**: Validation against banking business rules
- **Maker-Checker**: Dual approval workflow for all transactions

### Integration
- **Infosys Core Banking**: Direct integration with Infosys system
- **Real-time Processing**: Immediate processing of payment batches
- **Status Monitoring**: Real-time status updates and notifications
- **Error Handling**: Comprehensive error handling and recovery

### Monitoring
- **Transaction Tracking**: Complete tracking of all transactions
- **Status Updates**: Real-time status updates for all operations
- **Error Reporting**: Detailed error reporting and logging
- **Performance Monitoring**: System performance monitoring and alerting

## Configuration

### SFTP Server Configuration
```properties
zikan.sftp.host=localhost
zikan.sftp.port=22
zikan.sftp.username=corporate
zikan.sftp.password=password
zikan.sftp.uploadDir=/uploads
zikan.sftp.processedDir=/processed
zikan.sftp.acknowledgementDir=/acknowledgements
```

### Infosys Integration Configuration
```properties
zikan.infosys.baseUrl=http://localhost:8081
zikan.infosys.username=bank_user
zikan.infosys.password=bank_password
zikan.infosys.timeout=30000
```

### File Processing Configuration
```properties
zikan.file.processing.enabled=true
zikan.file.processing.pollInterval=5000
zikan.file.processing.maxFileSize=10MB
zikan.file.processing.supportedFormats=csv,xml,json
```

## API Endpoints

### Salary Processing
- `POST /salary/process` - Process salary batch via REST API
- `GET /salary/acknowledgements` - Get all acknowledgements
- `GET /salary/acknowledgements/{batchId}` - Get specific acknowledgement

### SFTP Processing
- Automatic processing of files uploaded via SFTP
- Real-time monitoring of SFTP directory
- Automatic acknowledgement generation and delivery

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

## Compliance and Audit

### Audit Trail
- Complete logging of all file uploads
- Detailed transaction processing logs
- User activity logging
- System access logging

### Compliance
- Banking regulation compliance
- Data protection compliance
- Security standard compliance
- Audit requirement compliance

## Performance Considerations

### Scalability
- Horizontal scaling support
- Load balancing capabilities
- Database optimization
- Caching mechanisms

### Monitoring
- Performance metrics collection
- System health monitoring
- Alert notifications
- Capacity planning support

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
