package com.zikan.salary.service;

import com.zikan.salary.model.SalaryRequest;
import com.zikan.salary.model.SalaryAcknowledgement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Service for integrating with Infosys Core Banking System
 * This service handles payment processing and status monitoring
 */
@Service
public class InfosysIntegrationService {

    @Value("${zikan.infosys.baseUrl:http://localhost:8081}")
    private String infosysBaseUrl;

    @Value("${zikan.infosys.username:bank_user}")
    private String infosysUsername;

    @Value("${zikan.infosys.password:bank_password}")
    private String infosysPassword;

    @Value("${zikan.infosys.timeout:30000}")
    private int timeout;

    private final RestTemplate restTemplate;

    public InfosysIntegrationService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Process salary batch through Infosys core banking system
     */
    public InfosysResponse processSalaryBatch(SalaryRequest salaryRequest) {
        try {
            // 1. Prepare payment request for Infosys
            InfosysPaymentRequest paymentRequest = preparePaymentRequest(salaryRequest);

            // 2. Send to Infosys
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + getInfosysToken());

            HttpEntity<InfosysPaymentRequest> entity = new HttpEntity<>(paymentRequest, headers);

            ResponseEntity<InfosysPaymentResponse> response = restTemplate.postForEntity(
                infosysBaseUrl + "/api/v1/payments/batch",
                entity,
                InfosysPaymentResponse.class
            );

            // 3. Process response
            return processInfosysResponse(response.getBody(), salaryRequest);

        } catch (Exception e) {
            return createErrorResponse(salaryRequest.getSalaryBatchId(), 
                "Failed to process payment batch: " + e.getMessage());
        }
    }

    /**
     * Prepare payment request for Infosys
     */
    private InfosysPaymentRequest preparePaymentRequest(SalaryRequest salaryRequest) {
        InfosysPaymentRequest request = new InfosysPaymentRequest();
        
        request.setBatchId(salaryRequest.getSalaryBatchId());
        request.setCompanyName(salaryRequest.getCompanyName());
        request.setCompanyAccount(salaryRequest.getCompanyAccount());
        request.setPaymentDate(salaryRequest.getSalaryDate());
        request.setTransactionType("SALARY_DISBURSEMENT");
        request.setCurrency("NGN");
        
        // Convert employees to payment items
        List<InfosysPaymentItem> paymentItems = new ArrayList<>();
        for (SalaryRequest.Employee employee : salaryRequest.getEmployees()) {
            InfosysPaymentItem item = new InfosysPaymentItem();
            item.setEmployeeId(employee.getEmployeeId());
            item.setEmployeeName(employee.getName());
            item.setAccountNumber(employee.getAccountNumber());
            item.setBankCode(employee.getBankCode());
            item.setAmount(employee.getAmount());
            item.setNarration("Salary payment for " + employee.getName());
            paymentItems.add(item);
        }
        
        request.setPaymentItems(paymentItems);
        return request;
    }

    /**
     * Process Infosys response
     */
    private InfosysResponse processInfosysResponse(InfosysPaymentResponse response, SalaryRequest salaryRequest) {
        InfosysResponse result = new InfosysResponse();
        result.setBatchId(salaryRequest.getSalaryBatchId());
        result.setStatus(response.getStatus());
        result.setMessage(response.getMessage());
        result.setTransactionId(response.getTransactionId());
        result.setProcessedAt(LocalDateTime.now());
        
        // Process individual transaction statuses
        if (response.getTransactionStatuses() != null) {
            result.setTransactionStatuses(response.getTransactionStatuses());
        }
        
        return result;
    }

    /**
     * Create error response
     */
    private InfosysResponse createErrorResponse(String batchId, String errorMessage) {
        InfosysResponse response = new InfosysResponse();
        response.setBatchId(batchId);
        response.setStatus("FAILED");
        response.setMessage(errorMessage);
        response.setProcessedAt(LocalDateTime.now());
        return response;
    }

    /**
     * Get authentication token from Infosys
     */
    private String getInfosysToken() {
        // Implementation for getting authentication token
        // This would typically involve calling Infosys authentication endpoint
        return "dummy_token"; // Placeholder
    }

    /**
     * Check payment status
     */
    public InfosysResponse checkPaymentStatus(String batchId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getInfosysToken());

            ResponseEntity<InfosysPaymentResponse> response = restTemplate.getForEntity(
                infosysBaseUrl + "/api/v1/payments/batch/" + batchId + "/status",
                InfosysPaymentResponse.class
            );

            InfosysResponse result = new InfosysResponse();
            result.setBatchId(batchId);
            result.setStatus(response.getBody().getStatus());
            result.setMessage(response.getBody().getMessage());
            result.setTransactionId(response.getBody().getTransactionId());
            result.setProcessedAt(LocalDateTime.now());

            return result;

        } catch (Exception e) {
            return createErrorResponse(batchId, "Failed to check payment status: " + e.getMessage());
        }
    }

    // Inner classes for Infosys integration
    public static class InfosysPaymentRequest {
        private String batchId;
        private String companyName;
        private String companyAccount;
        private String paymentDate;
        private String transactionType;
        private String currency;
        private List<InfosysPaymentItem> paymentItems;

        // Getters and setters
        public String getBatchId() { return batchId; }
        public void setBatchId(String batchId) { this.batchId = batchId; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getCompanyAccount() { return companyAccount; }
        public void setCompanyAccount(String companyAccount) { this.companyAccount = companyAccount; }
        public String getPaymentDate() { return paymentDate; }
        public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
        public String getTransactionType() { return transactionType; }
        public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public List<InfosysPaymentItem> getPaymentItems() { return paymentItems; }
        public void setPaymentItems(List<InfosysPaymentItem> paymentItems) { this.paymentItems = paymentItems; }
    }

    public static class InfosysPaymentItem {
        private String employeeId;
        private String employeeName;
        private String accountNumber;
        private String bankCode;
        private long amount;
        private String narration;

        // Getters and setters
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        public String getBankCode() { return bankCode; }
        public void setBankCode(String bankCode) { this.bankCode = bankCode; }
        public long getAmount() { return amount; }
        public void setAmount(long amount) { this.amount = amount; }
        public String getNarration() { return narration; }
        public void setNarration(String narration) { this.narration = narration; }
    }

    public static class InfosysPaymentResponse {
        private String status;
        private String message;
        private String transactionId;
        private List<TransactionStatus> transactionStatuses;

        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public List<TransactionStatus> getTransactionStatuses() { return transactionStatuses; }
        public void setTransactionStatuses(List<TransactionStatus> transactionStatuses) { this.transactionStatuses = transactionStatuses; }
    }

    public static class TransactionStatus {
        private String employeeId;
        private String accountNumber;
        private String status;
        private String errorCode;
        private String errorMessage;

        // Getters and setters
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    public static class InfosysResponse {
        private String batchId;
        private String status;
        private String message;
        private String transactionId;
        private LocalDateTime processedAt;
        private List<TransactionStatus> transactionStatuses;

        // Getters and setters
        public String getBatchId() { return batchId; }
        public void setBatchId(String batchId) { this.batchId = batchId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public LocalDateTime getProcessedAt() { return processedAt; }
        public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
        public List<TransactionStatus> getTransactionStatuses() { return transactionStatuses; }
        public void setTransactionStatuses(List<TransactionStatus> transactionStatuses) { this.transactionStatuses = transactionStatuses; }
    }
}
