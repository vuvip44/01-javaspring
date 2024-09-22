package com.vuviet.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.vuviet.jobhunter.entity.response.ResCreateUserDTO;
import com.vuviet.jobhunter.entity.response.ResUpdateUserDTO;
import com.vuviet.jobhunter.entity.response.ResUserDTO;
import com.vuviet.jobhunter.util.annotation.ApiMessage;
import com.vuviet.jobhunter.entity.response.ResultPaginationDTO;
import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.service.UserService;
import com.vuviet.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@RequestBody @Valid User user) throws IdInvalidException {
        boolean isEmailExist=this.userService.isEmailExist(user.getEmail());
        if(isEmailExist){
            throw new IdInvalidException("Email "+user.getEmail()+" đã tồn tại, vui lòng sử dụng email khác");
        }
        String hashPassword=this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User resUser = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUser(resUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException{
        User current=this.userService.getById(id);
        if(current==null){
            throw new IdInvalidException("User có id = "+id+" không tồn tại");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("get user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException{
        User current=this.userService.getById(id);
        if(current==null){
            throw new IdInvalidException("User có id = "+id+" không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUser(current));
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable
            ) {
        return ResponseEntity.ok(this.userService.getAllUsers(spec,pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody @Valid User userDTO) throws IdInvalidException{
        User user = this.userService.updateUser(userDTO);
        if(user==null){
            throw new IdInvalidException("User có id = "+userDTO.getId()+" không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUser(user));
    }
}
