package com.vuviet.jobhunter.repository;

import com.vuviet.jobhunter.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company,Long> {
}
