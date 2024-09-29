package com.vuviet.jobhunter.repository;

import com.vuviet.jobhunter.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubscriberRepository extends JpaRepository<Subscriber,Long>, JpaSpecificationExecutor<Subscriber> {
    boolean existsByEmail(String email);
}
