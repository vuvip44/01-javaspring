package com.vuviet.jobhunter.repository;

import com.vuviet.jobhunter.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill,Long>, JpaSpecificationExecutor<Skill> {
    boolean existsByName(String name);

    List<Skill> findByIdIn(List<Long> id);
}
