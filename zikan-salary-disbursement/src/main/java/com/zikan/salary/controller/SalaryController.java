package com.zikan.salary.controller;

import com.zikan.salary.model.SalaryAcknowledgement;
import com.zikan.salary.model.SalaryRequest;
import com.zikan.salary.repository.SalaryAcknowledgementRepository;
import com.zikan.salary.service.SalaryService; // <-- will fix path below
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary")
public class SalaryController {

    private final SalaryService service;
    private final SalaryAcknowledgementRepository repo;

    public SalaryController(SalaryService service, SalaryAcknowledgementRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @PostMapping("/process")
    public SalaryAcknowledgement process(@RequestBody SalaryRequest request) throws Exception {
        return service.process(request);
    }

    @PostMapping("/approve/{batchId}")
    public SalaryAcknowledgement approve(@PathVariable String batchId) throws Exception {
        return service.approveBatch(batchId);
    }

    @GetMapping("/acknowledgements")
    public List<SalaryAcknowledgement> acks() {
        return repo.findAll();
    }
}
