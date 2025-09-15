package com.zikan.salary.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zikan.salary.model.SalaryAcknowledgement;
import com.zikan.salary.model.SalaryRequest;
import com.zikan.salary.repository.SalaryAcknowledgementRepository;

@Service
public class SalaryService {

    private final SalaryAcknowledgementRepository repository;
    private final GpgService gpg;
    private final FileValidationService validationService;
    private final InfosysIntegrationService infosysService;
    private final SftpService sftpService;

    @Value("${zikan.workdir}")
    private String workdir;

    @Value("${zikan.publicKeyPath}")
    private String publicKeyPath;

    @Value("${zikan.rclone.remote}")
    private String rcloneRemote;

    @Value("${zikan.rclone.dest}")
    private String rcloneDest;

    @Value("${zikan.encryption.enabled}")
    private boolean encryptionEnabled;

    public SalaryService(SalaryAcknowledgementRepository repository, GpgService gpg, 
                        FileValidationService validationService, InfosysIntegrationService infosysService,
                        SftpService sftpService) {
        this.repository = repository;
        this.gpg = gpg;
        this.validationService = validationService;
        this.infosysService = infosysService;
        this.sftpService = sftpService;
    }

    public SalaryAcknowledgement process(SalaryRequest request) throws Exception {
        // 1) Ensure directories
        File base = new File(workdir);
        if (!base.exists() && !base.mkdirs()) {
            throw new IllegalStateException("Unable to create workdir: " + base);
        }

        // 2) Generate batch CSV
        File batch = new File(base, "salary_batch_" + request.getSalaryBatchId() + ".csv");
        try (BufferedWriter w = new BufferedWriter(new FileWriter(batch))) {
            w.write("companyName,batchId,companyAccount,salaryDate,generatedAt\n");
            w.write(String.join(",", safe(request.getCompanyName()), safe(request.getSalaryBatchId()),
                    safe(request.getCompanyAccount()), safe(request.getSalaryDate()),
                    LocalDateTime.now().toString()));
            w.write("\n\nemployeeId,name,accountNumber,bankCode,amount\n");
            for (SalaryRequest.Employee e : request.getEmployees()) {
                w.write(String.join(",",
                        safe(e.getEmployeeId()),
                        safe(e.getName()),
                        safe(e.getAccountNumber()),
                        safe(e.getBankCode()),
                        String.valueOf(e.getAmount())));
                w.write("\n");
            }
        }

        // 3) Encrypt with GPG (if enabled)
        File encrypted = batch;
        if (encryptionEnabled) {
            encrypted = gpg.encryptFile(batch, new File(publicKeyPath));
        }

        // 4) Upload via rclone (if enabled)
        if (encryptionEnabled) {
            ProcessBuilder pb = new ProcessBuilder(
                    "rclone", "copy",
                    encrypted.getAbsolutePath(),
                    rcloneRemote + ":/" + rcloneDest
            );
            pb.inheritIO();
            Process r = pb.start();
            int exit = r.waitFor();
            if (exit != 0) {
                // If upload fails, still record FAILED ack
                SalaryAcknowledgement failed = new SalaryAcknowledgement(
                        request.getSalaryBatchId(),
                        "FAILED",
                        "Upload to Drive failed (exit " + exit + ")."
                );
                return repository.save(failed);
            }
        }

        // 5) Mimic Finacle-like acknowledgement
        String[] statuses = {"SUCCESS", "PENDING", "FAILED"};
        String status = statuses[new Random().nextInt(statuses.length)];
        String message = switch (status) {
            case "SUCCESS" -> "Salary batch processed successfully.";
            case "PENDING" -> "Salary batch is pending approval.";
            default -> "Salary batch failed due to core-banking checks.";
        };

        // 6) Persist & return
        SalaryAcknowledgement ack = new SalaryAcknowledgement(request.getSalaryBatchId(), status, message);
        return repository.save(ack);
    }

    /**
     * Enhanced processing method for host-to-host integration
     * This method implements the complete workflow: SFTP -> Validation -> Infosys -> Acknowledgement
     */
    public SalaryAcknowledgement processHostToHost(File uploadedFile, String fileName) throws Exception {
        try {
            // 1. File Validation
            FileValidationService.ValidationResult validationResult = validationService.validateSalaryFile(uploadedFile, fileName);
            
            if (!validationResult.isValid()) {
                return createFailedAcknowledgement("VALIDATION_FAILED", 
                    "File validation failed: " + String.join(", ", validationResult.getErrors()));
            }

            SalaryRequest salaryRequest = validationResult.getSalaryRequest();

            // 2. Maker-Checker Workflow (simplified for demo)
            if (!approveSalaryBatch(salaryRequest)) {
                return createFailedAcknowledgement("REJECTED", 
                    "Salary batch rejected by maker-checker workflow");
            }

            // 3. Process through Infosys Core Banking
            InfosysIntegrationService.InfosysResponse infosysResponse = infosysService.processSalaryBatch(salaryRequest);

            // 4. Create detailed acknowledgement
            SalaryAcknowledgement acknowledgement = createDetailedAcknowledgement(salaryRequest, infosysResponse);

            // 5. Log to database
            return repository.save(acknowledgement);

        } catch (Exception e) {
            return createFailedAcknowledgement("PROCESSING_ERROR", 
                "Error processing salary batch: " + e.getMessage());
        }
    }

    /**
     * Process salary batch from SFTP upload
     */
    public void processSftpUpload(File file, String fileName) {
        try {
            SalaryAcknowledgement result = processHostToHost(file, fileName);
            
            // Send acknowledgement back to corporate client via SFTP
            sendAcknowledgementToClient(result, fileName);
            
        } catch (Exception e) {
            // Log error and create failure acknowledgement
            SalaryAcknowledgement errorAck = createFailedAcknowledgement("SFTP_PROCESSING_ERROR", 
                "Error processing SFTP upload: " + e.getMessage());
            sendAcknowledgementToClient(errorAck, fileName);
        }
    }

    /**
     * Maker-Checker approval workflow
     */
    private boolean approveSalaryBatch(SalaryRequest request) {
        // This would implement the actual maker-checker workflow
        // For now, we'll do basic validation
        
        // Check if company account has sufficient funds
        if (!checkSufficientFunds(request)) {
            return false;
        }
        
        // Check if all employees are valid
        if (!validateEmployees(request)) {
            return false;
        }
        
        // Additional business rules can be added here
        return true;
    }

    /**
     * Check if company account has sufficient funds
     */
    private boolean checkSufficientFunds(SalaryRequest request) {
        // This would integrate with core banking to check account balance
        // For demo purposes, we'll assume funds are available
        return true;
    }

    /**
     * Validate employees against HR system
     */
    private boolean validateEmployees(SalaryRequest request) {
        // This would integrate with HR system to validate employee records
        // For demo purposes, we'll assume all employees are valid
        return true;
    }

    /**
     * Create detailed acknowledgement with transaction statuses
     */
    private SalaryAcknowledgement createDetailedAcknowledgement(SalaryRequest request, 
            InfosysIntegrationService.InfosysResponse infosysResponse) {
        
        SalaryAcknowledgement acknowledgement = new SalaryAcknowledgement();
        acknowledgement.setSalaryBatchId(request.getSalaryBatchId());
        acknowledgement.setStatus(infosysResponse.getStatus());
        acknowledgement.setMessage(infosysResponse.getMessage());
        
        // Add detailed transaction information
        if (infosysResponse.getTransactionStatuses() != null) {
            StringBuilder detailedMessage = new StringBuilder(acknowledgement.getMessage());
            detailedMessage.append("\n\nTransaction Details:\n");
            
            for (InfosysIntegrationService.TransactionStatus status : infosysResponse.getTransactionStatuses()) {
                detailedMessage.append(String.format("Employee %s (%s): %s\n", 
                    status.getEmployeeId(), status.getAccountNumber(), status.getStatus()));
                
                if (status.getErrorMessage() != null) {
                    detailedMessage.append(String.format("  Error: %s\n", status.getErrorMessage()));
                }
            }
            
            acknowledgement.setMessage(detailedMessage.toString());
        }
        
        return acknowledgement;
    }

    /**
     * Create failed acknowledgement
     */
    private SalaryAcknowledgement createFailedAcknowledgement(String status, String message) {
        SalaryAcknowledgement acknowledgement = new SalaryAcknowledgement();
        acknowledgement.setStatus(status);
        acknowledgement.setMessage(message);
        return acknowledgement;
    }

    /**
     * Send acknowledgement back to corporate client
     */
    private void sendAcknowledgementToClient(SalaryAcknowledgement acknowledgement, String originalFileName) {
        try {
            // Create acknowledgement file
            String ackFileName = "ACK_" + originalFileName.replaceAll("\\.[^.]+$", ".txt");
            File ackFile = new File(workdir + "/acknowledgements/" + ackFileName);
            ackFile.getParentFile().mkdirs();
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ackFile))) {
                writer.write("Salary Batch Acknowledgement\n");
                writer.write("============================\n\n");
                writer.write("Batch ID: " + acknowledgement.getSalaryBatchId() + "\n");
                writer.write("Status: " + acknowledgement.getStatus() + "\n");
                writer.write("Message: " + acknowledgement.getMessage() + "\n");
                writer.write("Processed At: " + LocalDateTime.now() + "\n");
            }
            
            // Upload acknowledgement to SFTP server for client to download
            // This would use the SftpService to upload the acknowledgement file
            
        } catch (Exception e) {
            // Log error but don't fail the main process
            System.err.println("Failed to send acknowledgement to client: " + e.getMessage());
        }
    }

    private String safe(String s) { return s == null ? "" : s.replace(",", " "); }

    /**
     * Approve a pending salary batch for payment processing
     */
    public SalaryAcknowledgement approveBatch(String batchId) throws Exception {
        SalaryAcknowledgement existing = repository.findBySalaryBatchId(batchId);
        if (existing == null) {
            throw new IllegalArgumentException("Salary batch not found: " + batchId);
        }

        if ("PENDING".equals(existing.getStatus())) {
            existing.setStatus("APPROVED");
            existing.setMessage("Salary batch approved and payment will be processed.");
        } else if ("APPROVED".equals(existing.getStatus())) {
            existing.setMessage("Salary batch was already approved.");
        } else {
            throw new IllegalStateException("Cannot approve batch with status: " + existing.getStatus());
        }

        return repository.save(existing);
    }
}
