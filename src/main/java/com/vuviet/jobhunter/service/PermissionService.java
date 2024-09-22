package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.entity.Permission;
import com.vuviet.jobhunter.entity.response.Meta;
import com.vuviet.jobhunter.entity.response.ResultPaginationDTO;
import com.vuviet.jobhunter.repository.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface PermissionService {
    boolean isPermissionExist(Permission p);

    Permission getById(long id);

    Permission createPermission(Permission permission);

    Permission updatePermission(Permission permissionDTO);

    ResultPaginationDTO getAllPermission(Specification<Permission> spec, Pageable pageable);

    void delete(long id);
}

@Service
class PermissionServiceImpl implements PermissionService{
    private final PermissionRepository permissionRespository;

    PermissionServiceImpl(PermissionRepository permissionRespository) {
        this.permissionRespository = permissionRespository;
    }

    @Override
    public boolean isPermissionExist(Permission p) {

        return this.permissionRespository.existsByModuleAndApiPathAndMethod(
                p.getModule(),
                p.getApiPath(),
                p.getMethod()
        );
    }

    @Override
    public Permission getById(long id) {
        Optional<Permission> permission=this.permissionRespository.findById(id);
        if(permission.isPresent()){
            return permission.get();
        }
        return null;
    }

    @Override
    public Permission createPermission(Permission permission) {
        return this.permissionRespository.save(permission);
    }

    @Override
    public Permission updatePermission(Permission permissionDTO) {
        Permission permission=this.getById(permissionDTO.getId());
        if(permission!=null){
            permission.setName(permissionDTO.getName());
            permission.setApiPath(permissionDTO.getApiPath());
            permission.setMethod(permissionDTO.getMethod());
            permission.setModule(permissionDTO.getModule());

            permission=this.permissionRespository.save(permission);
            return permission;
        }
        return null;
    }

    @Override
    public ResultPaginationDTO getAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission=this.permissionRespository.findAll(spec,pageable);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        Meta meta=new Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pagePermission.getContent());
        return rs;
    }

    @Override
    public void delete(long id) {
        Optional<Permission> permissionOptional=this.permissionRespository.findById(id);
        Permission currentPermission=permissionOptional.get();
        currentPermission.getRoles().forEach(role->role.getPermissions().remove(currentPermission));

        this.permissionRespository.delete(currentPermission);
    }
}