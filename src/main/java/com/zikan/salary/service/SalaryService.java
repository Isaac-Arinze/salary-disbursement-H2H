package com.zikan.salary.service;


import com.zikan.salary.model.Employee;
import com.zikan.salary.model.SalaryRequest;
import com.zikan.salary.repository.EmployeeRepository;
import com.zikan.salary.repository.SalaryRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class SalaryService {

  private final SalaryRepository salaryRepository;

    public SalaryService(SalaryRepository salaryRepository) {
        this.salaryRepository = salaryRepository;
    }


    public SalaryRequest processSalaryBatch(@Valid SalaryRequest request) {
        return salaryRepository.save(request);

    }

}
