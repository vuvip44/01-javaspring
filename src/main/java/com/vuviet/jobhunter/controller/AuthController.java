package com.vuviet.jobhunter.controller;

import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.service.UserService;
import com.vuviet.jobhunter.util.annotation.ApiMessage;
import com.vuviet.jobhunter.entity.request.LoginDTO;
import com.vuviet.jobhunter.entity.response.ResLoginDTO;
import com.vuviet.jobhunter.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    private final UserService userService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    @ApiMessage("login user")
    public ResponseEntity<ResLoginDTO> login(@RequestBody @Valid LoginDTO loginDTO){
        //nap username va password vao security
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),loginDTO.getPassword());

        //xac thuc nguoi dung, can loadUserByUsername
        Authentication authentication=this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //create token
        String access_token=this.securityUtil.createAccessToken(authentication);

        //cho token vao securityHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res=new ResLoginDTO();
        User currentUser=this.userService.getByUsername(loginDTO.getUsername());
        if(currentUser!=null){
            ResLoginDTO.UserLogin userLogin=new ResLoginDTO.UserLogin(currentUser.getId(),currentUser.getEmail(),currentUser.getName());
            res.setUser(userLogin);
        }
        res.setAccess_token(access_token);

        //create refresh token
        String refresh_token=this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
        return ResponseEntity.ok(res);
    }
}
