package com.vuviet.jobhunter.repository;

import com.vuviet.jobhunter.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User getByEmail(String email);
    User getById(long id);
}
