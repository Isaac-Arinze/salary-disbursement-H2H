package com.zikan.salary.repository;

import com.zikan.salary.model.SalaryAcknowledgement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryAcknowledgementRepository extends JpaRepository<SalaryAcknowledgement, Long> {
}
