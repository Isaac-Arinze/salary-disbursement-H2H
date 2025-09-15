package com.zikan.salary.service;

import com.zikan.salary.model.SalaryRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service for validating salary files and implementing maker-checker workflow
 */
@Service
public class FileValidationService {

    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^[0-9]{10}$");
    private static final Pattern BANK_CODE_PATTERN = Pattern.compile("^[0-9]{3}$");
    private static final Pattern EMPLOYEE_ID_PATTERN = Pattern.compile("^[A-Z0-9]{6,10}$");

    /**
     * Validate uploaded salary file
     */
    public ValidationResult validateSalaryFile(File file, String fileName) {
        ValidationResult result = new ValidationResult();
        
        try {
            // 1. File format validation
            if (!isValidFileFormat(fileName)) {
                result.addError("Invalid file format. Supported formats: CSV, XML, JSON");
                return result;
            }

            // 2. Parse file based on format
            SalaryRequest salaryRequest = parseFile(file, fileName);
            if (salaryRequest == null) {
                result.addError("Failed to parse file. Please check file format and content.");
                return result;
            }

            // 3. Business validation
            validateBusinessRules(salaryRequest, result);

            // 4. Data integrity validation
            validateDataIntegrity(salaryRequest, result);

            result.setSalaryRequest(salaryRequest);
            result.setValid(result.getErrors().isEmpty());

        } catch (Exception e) {
            result.addError("File validation failed: " + e.getMessage());
        }

        return result;
    }

    /**
     * Check if file format is supported
     */
    private boolean isValidFileFormat(String fileName) {
        String lowerCase = fileName.toLowerCase();
        return lowerCase.endsWith(".csv") || 
               lowerCase.endsWith(".xml") || 
               lowerCase.endsWith(".json");
    }

    /**
     * Parse file based on format
     */
    private SalaryRequest parseFile(File file, String fileName) {
        String lowerCase = fileName.toLowerCase();
        
        if (lowerCase.endsWith(".csv")) {
            return parseCsvFile(file);
        } else if (lowerCase.endsWith(".xml")) {
            return parseXmlFile(file);
        } else if (lowerCase.endsWith(".json")) {
            return parseJsonFile(file);
        }
        
        return null;
    }

    /**
     * Parse CSV file
     */
    private SalaryRequest parseCsvFile(File file) {
        // Implementation for CSV parsing
        // This would read the CSV and convert to SalaryRequest object
        return new SalaryRequest(); // Placeholder
    }

    /**
     * Parse XML file
     */
    private SalaryRequest parseXmlFile(File file) {
        // Implementation for XML parsing
        return new SalaryRequest(); // Placeholder
    }

    /**
     * Parse JSON file
     */
    private SalaryRequest parseJsonFile(File file) {
        // Implementation for JSON parsing
        return new SalaryRequest(); // Placeholder
    }

    /**
     * Validate business rules
     */
    private void validateBusinessRules(SalaryRequest request, ValidationResult result) {
        // 1. Company validation
        if (request.getCompanyName() == null || request.getCompanyName().trim().isEmpty()) {
            result.addError("Company name is required");
        }

        if (request.getCompanyAccount() == null || request.getCompanyAccount().trim().isEmpty()) {
            result.addError("Company account number is required");
        }

        // 2. Batch validation
        if (request.getSalaryBatchId() == null || request.getSalaryBatchId().trim().isEmpty()) {
            result.addError("Salary batch ID is required");
        }

        // 3. Date validation
        if (request.getSalaryDate() == null || request.getSalaryDate().trim().isEmpty()) {
            result.addError("Salary date is required");
        }

        // 4. Employee validation
        if (request.getEmployees() == null || request.getEmployees().isEmpty()) {
            result.addError("At least one employee record is required");
        }
    }

    /**
     * Validate data integrity
     */
    private void validateDataIntegrity(SalaryRequest request, ValidationResult result) {
        if (request.getEmployees() != null) {
            for (int i = 0; i < request.getEmployees().size(); i++) {
                SalaryRequest.Employee employee = request.getEmployees().get(i);
                validateEmployee(employee, i + 1, result);
            }
        }
    }

    /**
     * Validate individual employee record
     */
    private void validateEmployee(SalaryRequest.Employee employee, int recordNumber, ValidationResult result) {
        // Employee ID validation
        if (employee.getEmployeeId() == null || !EMPLOYEE_ID_PATTERN.matcher(employee.getEmployeeId()).matches()) {
            result.addError("Record " + recordNumber + ": Invalid employee ID format");
        }

        // Name validation
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            result.addError("Record " + recordNumber + ": Employee name is required");
        }

        // Account number validation
        if (employee.getAccountNumber() == null || !ACCOUNT_NUMBER_PATTERN.matcher(employee.getAccountNumber()).matches()) {
            result.addError("Record " + recordNumber + ": Invalid account number format (must be 10 digits)");
        }

        // Bank code validation
        if (employee.getBankCode() == null || !BANK_CODE_PATTERN.matcher(employee.getBankCode()).matches()) {
            result.addError("Record " + recordNumber + ": Invalid bank code format (must be 3 digits)");
        }

        // Amount validation
        if (employee.getAmount() <= 0) {
            result.addError("Record " + recordNumber + ": Amount must be greater than 0");
        }

        if (employee.getAmount() > 100000000) { // 1 million in minor units
            result.addError("Record " + recordNumber + ": Amount exceeds maximum limit");
        }
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();
        private SalaryRequest salaryRequest;

        public void addError(String error) {
            errors.add(error);
        }

        public void addWarning(String warning) {
            warnings.add(warning);
        }

        // Getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
        public SalaryRequest getSalaryRequest() { return salaryRequest; }
        public void setSalaryRequest(SalaryRequest salaryRequest) { this.salaryRequest = salaryRequest; }
    }
}
