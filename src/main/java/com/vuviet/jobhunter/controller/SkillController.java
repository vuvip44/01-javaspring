package com.vuviet.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.vuviet.jobhunter.entity.Skill;
import com.vuviet.jobhunter.entity.response.ResultPaginationDTO;
import com.vuviet.jobhunter.service.SkillService;
import com.vuviet.jobhunter.util.annotation.ApiMessage;
import com.vuviet.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create a skill")
    public ResponseEntity<Skill> createSkill(@RequestBody @Valid Skill skillDTO) throws IdInvalidException{
        boolean isNameExist=this.skillService.isSkillExist(skillDTO.getName());
        if(isNameExist){
            throw new IdInvalidException("Tên "+skillDTO.getName()+" đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(skillDTO));
    }

    @PutMapping("/skills")
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> updateSkill(@RequestBody Skill skillDTO) throws IdInvalidException{
        boolean isNameExist=this.skillService.isSkillExist(skillDTO.getName());
        if(isNameExist){
            throw new IdInvalidException("Tên "+skillDTO.getName()+" đã tồn tại");
        }
        Skill skill=this.skillService.getById(skillDTO.getId());
        if(skill==null){
            throw new IdInvalidException("Id "+skillDTO.getId()+" không tồn tại");
        }
        return ResponseEntity.ok(this.skillService.updateSkill(skillDTO));
    }

    @GetMapping("/skills/{id}")
    @ApiMessage("get skill by id")
    public ResponseEntity<Skill> getSkillById(@PathVariable("id") long id) throws IdInvalidException{
        Skill skill=this.skillService.getById(id);
        if(skill==null){
            throw new IdInvalidException("Id "+id+" không tồn tại");
        }
        return ResponseEntity.ok(skill);
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skill")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(
            @Filter Specification<Skill> spec,
            Pageable pageable
            ){
        return ResponseEntity.ok(this.skillService.getAllSkills(spec,pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException{
        Skill skill=this.skillService.getById(id);
        if(skill==null){
            throw new IdInvalidException("Id "+id+" không tồn tại");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok(null);
    }
}
