package com.zikan.salary.model;

import java.util.List;

public class SalaryRequest {
    private String salaryBatchId;
    private String companyName;
    private String companyAccount;
    private String salaryDate; // yyyy-MM-dd
    private List<Employee> employees;

    public static class Employee {
        private String employeeId;
        private String name;
        private String accountNumber;
        private String bankCode;
        private long amount; // in minor units e.g. kobo/cent

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
    }

    public String getSalaryBatchId() { return salaryBatchId; }
    public void setSalaryBatchId(String salaryBatchId) { this.salaryBatchId = salaryBatchId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyAccount() { return companyAccount; }
    public void setCompanyAccount(String companyAccount) { this.companyAccount = companyAccount; }
    public String getSalaryDate() { return salaryDate; }
    public void setSalaryDate(String salaryDate) { this.salaryDate = salaryDate; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
}
