package com.zikan.salary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zikan.salary.model.SalaryAcknowledgement;

@Repository
public interface SalaryAcknowledgementRepository extends JpaRepository<SalaryAcknowledgement, Long> {
    SalaryAcknowledgement findBySalaryBatchId(String salaryBatchId);
}
