package com.zikan.salary.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "salary_acknowledgements")
public class SalaryAcknowledgement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String salaryBatchId;
    private String status;   // SUCCESS, PENDING, FAILED
    private String message;

    private Instant createdAt = Instant.now();

    public SalaryAcknowledgement() {}
    public SalaryAcknowledgement(String salaryBatchId, String status, String message) {
        this.salaryBatchId = salaryBatchId;
        this.status = status;
        this.message = message;
    }

    public Long getId() { return id; }
    public String getSalaryBatchId() { return salaryBatchId; }
    public void setSalaryBatchId(String salaryBatchId) { this.salaryBatchId = salaryBatchId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
