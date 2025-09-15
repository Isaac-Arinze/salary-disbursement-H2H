package com.zikan.salary.service;

import com.zikan.salary.model.Employee;
import com.zikan.salary.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    public  void  addEmployee(Employee employee) {
        employeeRepository.save(employee);
    }
}
