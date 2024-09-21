package com.vuviet.jobhunter.repository;

import com.vuviet.jobhunter.entity.Company;
import com.vuviet.jobhunter.entity.User;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {
    User getByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByCompany(Company company);
}
