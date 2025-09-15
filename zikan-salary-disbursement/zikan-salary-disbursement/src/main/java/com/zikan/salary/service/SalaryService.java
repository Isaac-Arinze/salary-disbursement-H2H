package com.zikan.salary.service;

import com.zikan.salary.model.SalaryAcknowledgement;
import com.zikan.salary.model.SalaryRequest;
import com.zikan.salary.repository.SalaryAcknowledgementRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SalaryService {

    private final SalaryAcknowledgementRepository repository;
    private final GpgService gpg;

    @Value("${zikan.workdir}")
    private String workdir;

    @Value("${zikan.publicKeyPath}")
    private String publicKeyPath;

    @Value("${zikan.rclone.remote}")
    private String rcloneRemote;

    @Value("${zikan.rclone.dest}")
    private String rcloneDest;

    public SalaryService(SalaryAcknowledgementRepository repository, GpgService gpg) {
        this.repository = repository;
        this.gpg = gpg;
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

        // 3) Encrypt with GPG
        File encrypted = gpg.encryptFile(batch, new File(publicKeyPath));

        // 4) Upload via rclone
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

    private String safe(String s) { return s == null ? "" : s.replace(",", " "); }
}
