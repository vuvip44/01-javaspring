package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.entity.Company;
import com.vuviet.jobhunter.entity.Role;
import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.entity.response.*;
import com.vuviet.jobhunter.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public interface UserService {
    User createUser(User user);

    User updateUser(User userDTO);

    ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable);

    User getById(long id);

    void deleteUser(long id);

    User getByUsername(String email);

    boolean isEmailExist(String email);

    ResCreateUserDTO convertToResCreateUser(User user);

    ResUpdateUserDTO convertToResUpdateUser(User user);

    ResUserDTO convertToResUser(User user);

    void updateUserToken(String token,String email);

    User getUserByRefreshTokenAndEmail(String token,String email);

    void UpdatePassword(String newPassword, User user);
}

@Service
class UserServiceImpl implements UserService {
    private final RoleService roleService;

    private final UserRepository userRepository;

    private final CompanyService companyService;

    UserServiceImpl(RoleService roleService, UserRepository userRepository, CompanyService companyService) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.companyService = companyService;
    }
    @Override
    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUser(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    @Override
    public User getById(long id) {
        Optional<User> userOptional=this.userRepository.findById(id);
        if(userOptional.isPresent()){
            return userOptional.get();
        }
        return null;
    }

    @Override
    public void deleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public User createUser(User user) {
        if(user.getCompany()!=null){
            Optional<Company> company= Optional.ofNullable(this.companyService.getById(user.getCompany().getId()));
            user.setCompany(company.isPresent()?company.get():null);
        }

        if(user.getRole()!=null){
            Role r=this.roleService.getById(user.getRole().getId());
            user.setRole(r!=null?r:null);
        }
        return this.userRepository.save(user);
    }

    @Override
    public User updateUser(User userDTO) {
        User user=this.getById(userDTO.getId());
        if(user!=null){
            user.setName(userDTO.getName());
            user.setGender(userDTO.getGender());
            user.setAddress(userDTO.getAddress());
            user.setAge(userDTO.getAge());
            if(userDTO.getCompany()!=null){
                Optional<Company> company= Optional.ofNullable(this.companyService.getById(userDTO.getCompany().getId()));
                user.setCompany(company.isPresent()?company.get():null);
            }

            if(userDTO.getRole()!=null){
               Optional<Role> role= Optional.ofNullable(this.roleService.getById(userDTO.getRole().getId()));
               user.setRole(role.isPresent()?role.get():null);
            }
            user=this.userRepository.save(user);
        }
        return user;
    }


    @Override
    public User getByUsername(String email) {
        return this.userRepository.getByEmail(email);
    }

    @Override
    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public ResCreateUserDTO convertToResCreateUser(User user) {
        ResCreateUserDTO res=new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser companyUser=new ResCreateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setAddress(user.getAddress());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAge(user.getAge());
        res.setName(user.getName());
        res.setCreatedAt(user.getCreatedAt());

        if(user.getCompany()!=null){
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompanyUser(companyUser);
        }
        return res;
    }

    @Override
    public ResUpdateUserDTO convertToResUpdateUser(User user) {
        ResUpdateUserDTO res=new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser companyUser=new ResUpdateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setUpdatedAt(user.getUpdatedAt());

        if(user.getCompany()!=null){
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompanyUser(companyUser);
        }
        return res;

    }

    @Override
    public ResUserDTO convertToResUser(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompanyUser(com);
        }

        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    @Override
    public void updateUserToken(String token,String email) {
        User currentUser=this.userRepository.getByEmail(email);
        if(currentUser!=null){
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }

    }

    @Override
    public User getUserByRefreshTokenAndEmail(String token, String email) {

        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    @Override
    public void UpdatePassword(String newPassword, User user) {
        user.setPassword(newPassword);
        this.userRepository.save(user);
    }


}