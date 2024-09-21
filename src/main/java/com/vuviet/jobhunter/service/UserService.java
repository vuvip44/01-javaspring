package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.entity.Company;
import com.vuviet.jobhunter.entity.response.*;
import com.vuviet.jobhunter.entity.User;
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

}

@Service
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CompanyService companyService;

    UserServiceImpl(UserRepository userRepository, CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }
    @Override
    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser=this.userRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> new ResUserDTO(
                        item.getId(),
                        item.getEmail(),
                        item.getName(),
                        item.getGender(),
                        item.getAddress(),
                        item.getAge(),
                        item.getUpdatedAt(),
                        item.getCreatedAt(),
                        new ResUserDTO.CompanyUser(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null)))
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
        ResUserDTO res=new ResUserDTO();
        ResUserDTO.CompanyUser companyUser=new ResUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setAddress(user.getAddress());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAge(user.getAge());
        res.setName(user.getName());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());

        if(user.getCompany()!=null){
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompanyUser(companyUser);
        }
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

}