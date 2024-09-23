package com.vuviet.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.vuviet.jobhunter.entity.Permission;
import com.vuviet.jobhunter.entity.response.ResultPaginationDTO;
import com.vuviet.jobhunter.service.PermissionService;
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
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createPermission(@RequestBody @Valid Permission permission) throws IdInvalidException {
        if(this.permissionService.isPermissionExist(permission)){
            throw new IdInvalidException("Permission này đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.createPermission(permission));
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Get permission by id")
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") long id) throws IdInvalidException {
        Permission permission=this.permissionService.getById(id);
        if(permission==null){
            throw new IdInvalidException("Id "+id+" không tồn tại");
        }
        return ResponseEntity.ok(permission);
    }

    @GetMapping("/permissions")
    @ApiMessage("fetch all permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions(
            @Filter Specification<Permission> spec,
            Pageable pageable
            ){
        return ResponseEntity.ok(this.permissionService.getAllPermission(spec, pageable));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@RequestBody @Valid Permission permissionDTO) throws IdInvalidException{

        Permission permission=this.permissionService.getById(permissionDTO.getId());
        if(permission==null){
            throw new IdInvalidException("Id "+permissionDTO.getId()+" không tồn tại");
        }
        return ResponseEntity.ok(this.permissionService.updatePermission(permissionDTO));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws IdInvalidException{
        Permission permission=this.permissionService.getById(id);
        if(permission==null){
            throw new IdInvalidException("Id "+id+" không tồn tại");
        }
        this.permissionService.delete(id);
        return ResponseEntity.ok(null);
    }
}
