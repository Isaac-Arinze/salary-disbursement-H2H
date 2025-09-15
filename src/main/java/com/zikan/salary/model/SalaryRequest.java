package com.zikan.salary.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;
@Entity
public class SalaryRequest{
    @NotBlank
    private String salaryBatchId;

    @NotBlank
    private String companyName;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{10}$")
    private String companyAccount;

    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    private String salaryDate; // yyyy-MM-dd

    @NotEmpty
    @Valid
//    private List<Employee> employees;
    private Long string;
    @Id
    private Long id;


    public String getSalaryBatchId() { return salaryBatchId; }
    public void setSalaryBatchId(String salaryBatchId) { this.salaryBatchId = salaryBatchId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyAccount() { return companyAccount; }
    public void setCompanyAccount(String companyAccount) { this.companyAccount = companyAccount; }
    public String getSalaryDate() { return salaryDate; }
    public void setSalaryDate(String salaryDate) { this.salaryDate = salaryDate; }
//    public List<Employee> getEmployees() { return employees; }
//    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    public void setString(Long string) {
        this.string = string;
    }

    public Long getString() {
        return string;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
