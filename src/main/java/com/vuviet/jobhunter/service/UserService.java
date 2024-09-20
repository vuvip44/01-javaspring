package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.entity.dto.*;
import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.repository.UserRepository;
import org.modelmapper.ModelMapper;
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

    ResultPaginationDTO getAllUser(Specification<User> spec,Pageable pageable);

    User getById(long id);

    void deleteUser(long id);

    User getByUsername(String email);

    boolean isEmailExist(String email);

    ResCreateUser convertToResCreateUser(User user);

    ResUpdateUser convertToResUpdateUser(User user);

    ResUserDTO convertToResUser(User user);
}

@Service
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public ResultPaginationDTO getAllUser(Specification<User> spec,Pageable pageable) {
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
                        item.getCreatedAt()))
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
    public ResCreateUser convertToResCreateUser(User user) {
        return new ModelMapper().map(user, ResCreateUser.class);
    }

    @Override
    public ResUpdateUser convertToResUpdateUser(User user) {
        return new ModelMapper().map(user, ResUpdateUser.class);
    }

    @Override
    public ResUserDTO convertToResUser(User user) {
        return new ModelMapper().map(user,ResUserDTO.class);
    }
}