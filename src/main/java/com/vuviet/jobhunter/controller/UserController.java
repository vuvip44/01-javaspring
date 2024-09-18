package com.vuviet.jobhunter.controller;

import com.vuviet.jobhunter.dto.ResultPaginationDTO;
import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
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

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional
            ) {
        String sCurrent=currentOptional.isPresent()? currentOptional.get() : "";
        String sPageSize=pageSizeOptional.isPresent()? pageSizeOptional.get():"";

        int current=Integer.parseInt(sCurrent);
        int pageSize=Integer.parseInt(sPageSize);

        Pageable pageable= PageRequest.of(current-1,pageSize);

        return ResponseEntity.ok(this.userService.getAllUser(pageable));
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User userDTO) {
        User userDTO1 = this.userService.updateUser(userDTO);
        return ResponseEntity.ok(userDTO1);
    }
}
