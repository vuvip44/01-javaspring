package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.dto.Meta;
import com.vuviet.jobhunter.dto.ResultPaginationDTO;
import com.vuviet.jobhunter.dto.UserDTO;
import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.repository.UserRepository;
import jakarta.validation.constraints.Null;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public interface UserService {
    User save(User user);

    void deleteUser(long id);

    User getUserById(long id);

    ResultPaginationDTO getAllUser(Pageable pageable);

    User updateUser(User userDTO);

    User getByUsername(String email);
}

@Service
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public User getUserById(long id) {
        Optional<User> user=this.userRepository.findById(id);
        if(user.isPresent()){
            return user.get();
        }
        return null;
    }

    @Override
    public ResultPaginationDTO getAllUser(Pageable pageable) {
        Page<User> pageUser=this.userRepository.findAll(pageable);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        Meta mt=new Meta();

        mt.setPage(pageUser.getNumber()+1);
        mt.setPageSize(pageUser.getSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageUser.getContent());
        return rs;
    }

    @Override
    public User updateUser(User userDTO) {
        User user=getUserById(userDTO.getId());
        user.setPassword(userDTO.getPassword());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        this.userRepository.save(user);
        return user;
    }

    @Override
    public User getByUsername(String email) {
        return this.userRepository.getByEmail(email);
    }
}