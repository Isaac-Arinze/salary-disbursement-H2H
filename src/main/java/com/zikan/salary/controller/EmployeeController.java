package com.zikan.salary.controller;

import com.zikan.salary.model.Employee;
import com.zikan.salary.model.SalaryRequest;
import com.zikan.salary.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

@PostMapping("/create")
    public ResponseEntity<SalaryRequest> createEmployee(@Valid @RequestBody Employee employee) {
        employeeService.addEmployee(employee);
        return ResponseEntity.ok().build();

    }
}
