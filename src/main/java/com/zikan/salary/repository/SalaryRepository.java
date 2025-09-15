package com.zikan.salary.repository;

import com.zikan.salary.model.SalaryRequest;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface SalaryRepository extends JpaRepository<SalaryRequest, String> {
}
