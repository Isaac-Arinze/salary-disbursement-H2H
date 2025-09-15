package com.zikan.salary.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Employee {

    @NotBlank
    private String employeeId;
    private String name;
    private String accountNumber;
    private String bankCode;
    private long amount; // in minor units e.g. kobo/cent
    @Id
    private String id;

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
