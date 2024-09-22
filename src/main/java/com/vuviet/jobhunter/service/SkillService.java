package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.entity.Job;
import com.vuviet.jobhunter.entity.Skill;
import com.vuviet.jobhunter.entity.response.Meta;
import com.vuviet.jobhunter.entity.response.ResultPaginationDTO;
import com.vuviet.jobhunter.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface SkillService {
    Skill createSkill(Skill skill);

    Skill updateSkill(Skill skillDTO);

    Skill getById(long id);

    ResultPaginationDTO getAllSkills(Specification<Skill> spec, Pageable pageable);

    boolean isSkillExist(String name);

    void deleteSkill(long id);

}

@Service
class SkillServiceImpl implements SkillService{
    private final SkillRepository skillRepository;

    SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Skill createSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    @Override
    public Skill updateSkill(Skill skillDTO) {
        return this.skillRepository.save(skillDTO);
    }

    @Override
    public Skill getById(long id) {
        Optional<Skill> skillOptional=this.skillRepository.findById(id);
        if(skillOptional.isPresent()){
            return skillOptional.get();
        }
        return null;
    }

    @Override
    public ResultPaginationDTO getAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill=this.skillRepository.findAll(spec,pageable);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        Meta meta=new Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageSkill.getContent());
        return rs;
    }

    @Override
    public boolean isSkillExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    @Override
    public void deleteSkill(long id) {
        //xoa job_id trong bang job_skill
        Optional<Skill> skillOptional=this.skillRepository.findById(id);
        Skill currentSkill=skillOptional.get();

//        for (Job job : currentSkill.getJobs()) {
//            job.getSkills().remove(currentSkill);
//        }
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        this.skillRepository.delete(currentSkill);
    }
}
