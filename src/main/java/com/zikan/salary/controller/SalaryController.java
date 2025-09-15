package com.zikan.salary.controller;

import com.zikan.salary.model.Employee;
import com.zikan.salary.model.SalaryRequest;
import com.zikan.salary.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @PostMapping("/disburse")
    public ResponseEntity<SalaryRequest> disburseSalary(@Valid @RequestBody SalaryRequest request) {
        salaryService.processSalaryBatch(request);
        return ResponseEntity.ok().build();
//        return ResponseEntity.ok("Salary batch " +request.getSalaryBatchId() + " has been successfully processed");
    }
}
