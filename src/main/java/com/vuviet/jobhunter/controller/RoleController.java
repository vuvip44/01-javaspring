package com.vuviet.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.vuviet.jobhunter.entity.Role;
import com.vuviet.jobhunter.entity.response.ResultPaginationDTO;
import com.vuviet.jobhunter.service.RoleService;
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
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createRole(@RequestBody @Valid Role role) throws IdInvalidException{
        if(this.roleService.isExistName(role.getName())){
            throw new IdInvalidException("Role với name "+role.getName()+" đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@RequestBody @Valid Role role) throws IdInvalidException{
        if(this.roleService.getById(role.getId())==null){
            throw new IdInvalidException("Id "+role.getId()+" không tồn tại");
        }

        if(this.roleService.isExistName(role.getName())){
            throw new IdInvalidException("Role với name "+role.getName()+" đã tồn tại");
        }

        return ResponseEntity.ok(this.roleService.update(role));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Get role by id")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id) throws IdInvalidException{
        Role role=this.roleService.getById(id);
        if(role==null){
            throw new IdInvalidException("Id "+role.getId()+" không tồn tại");
        }
        return ResponseEntity.ok(role);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch all roles")
    public ResponseEntity<ResultPaginationDTO> getAllRole(
            @Filter Specification<Role> spec,
            Pageable pageable
            ) {
        return ResponseEntity.ok(this.roleService.getAll(spec, pageable));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException{
        Role role=this.roleService.getById(id);
        if(role==null){
            throw new IdInvalidException("Id "+role.getId()+" không tồn tại");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok(null);
    }
}
