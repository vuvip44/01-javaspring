package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.entity.Permission;
import com.vuviet.jobhunter.entity.Role;
import com.vuviet.jobhunter.entity.response.Meta;
import com.vuviet.jobhunter.entity.response.ResultPaginationDTO;
import com.vuviet.jobhunter.repository.PermissionRepository;
import com.vuviet.jobhunter.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface RoleService {
    Role create(Role roleDTO);

    Role getById(long id);

    Role update(Role roleDTO);

    void delete(long id);

    ResultPaginationDTO getAll(Specification<Role> spec, Pageable pageable);

    boolean isExistName(String name);
}

@Service
class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Role create(Role roleDTO) {
        if(roleDTO.getPermissions()!=null){
            List<Long> reqPermission=roleDTO.getPermissions()
                    .stream().map(x->x.getId()).collect(Collectors.toList());
            List<Permission> permissions=this.permissionRepository.findByIdIn(reqPermission);
            roleDTO.setPermissions(permissions);
        }
        return this.roleRepository.save(roleDTO);
    }

    @Override
    public Role getById(long id) {
        Optional<Role> role=this.roleRepository.findById(id);
        if(role.isPresent()){
            return role.get();
        }
        return null;
    }

    @Override
    public Role update(Role roleDTO) {
        Role role=this.getById(roleDTO.getId());
        if(roleDTO.getPermissions()!=null){
            List<Long> reqPermission=roleDTO.getPermissions()
                    .stream().map(x->x.getId()).collect(Collectors.toList());
            List<Permission> permissions=this.permissionRepository.findByIdIn(reqPermission);
            roleDTO.setPermissions(permissions);
        }

        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        role.setActive(roleDTO.isActive());
        role.setPermissions(roleDTO.getPermissions());
        role=this.roleRepository.save(role);
        return role;
    }

    @Override
    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

    @Override
    public ResultPaginationDTO getAll(Specification<Role> spec, Pageable pageable) {
        Page<Role> pagePermission=this.roleRepository.findAll(spec,pageable);
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
    public boolean isExistName(String name) {
        return this.roleRepository.existsByName(name);
    }
}
