package com.vuviet.jobhunter.controller;

import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users/")
    public ResponseEntity<User> createNewUser(@RequestBody @Valid User userDTO) {
        String hashPassword=this.passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(hashPassword);
        User user = this.userService.save(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok("Perfect");
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {

        User userDTO = this.userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/users/")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> userDTOS = this.userService.getAllUser();
        return ResponseEntity.ok(userDTOS);
    }

    @PutMapping("/users/")
    public ResponseEntity<User> updateUser(@RequestBody User userDTO) {
        User userDTO1 = this.userService.updateUser(userDTO);
        return ResponseEntity.ok(userDTO1);
    }
}
