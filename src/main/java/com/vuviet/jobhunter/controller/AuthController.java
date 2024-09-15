package com.vuviet.jobhunter.controller;

import com.vuviet.jobhunter.dto.LoginDTO;
import com.vuviet.jobhunter.dto.ResLogin;
import com.vuviet.jobhunter.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLogin> login(@RequestBody @Valid LoginDTO loginDTO){
        //nap username va password vao security
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),loginDTO.getPassword());

        //xac thuc nguoi dung, can loadUserByUsername
        Authentication authentication=this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //create token
        String accessToken=this.securityUtil.createToken(authentication);

        //cho token vao securityHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLogin resLogin=new ResLogin();
        resLogin.setAccessToken(accessToken);
        return ResponseEntity.ok(resLogin);
    }
}
