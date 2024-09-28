package com.vuviet.jobhunter.controller;

import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.entity.response.ResCreateUserDTO;
import com.vuviet.jobhunter.service.UserService;
import com.vuviet.jobhunter.util.annotation.ApiMessage;
import com.vuviet.jobhunter.entity.request.LoginDTO;
import com.vuviet.jobhunter.entity.response.ResLoginDTO;
import com.vuviet.jobhunter.util.SecurityUtil;
import com.vuviet.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Value("${vuviet.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    @ApiMessage("login user")
    public ResponseEntity<ResLoginDTO> login(@RequestBody @Valid LoginDTO loginDTO){
        //nap username va password vao security
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),loginDTO.getPassword());

        //xac thuc nguoi dung, can loadUserByUsername
        Authentication authentication=this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //set thong tin nguoi dung dang nhap vao context(co the su dung sau nay)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res=new ResLoginDTO();
        User currentUser=this.userService.getByUsername(loginDTO.getUsername());
        if(currentUser!=null){
            ResLoginDTO.UserLogin userLogin=new ResLoginDTO.UserLogin(currentUser.getId(),currentUser.getEmail(),currentUser.getName(),currentUser.getRole());
            res.setUser(userLogin);
        }

        //create token
        String access_token=this.securityUtil.createAccessToken(authentication.getName(),res);
        res.setAccessToken(access_token);

        //create refresh token
        String refresh_token=this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);

        //update user with refreshToken
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        //set cookies
        ResponseCookie resCookies=ResponseCookie.from("refresh_token",refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,resCookies.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount(){
        String email=SecurityUtil.getCurrentUserLogin().isPresent()?
                SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUser=this.userService.getByUsername(email);
        ResLoginDTO.UserLogin userLogin=new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount=new ResLoginDTO.UserGetAccount();
        if(currentUser!=null){
            userLogin.setId(currentUser.getId());
            userLogin.setName(currentUser.getName());
            userLogin.setEmail(currentUser.getEmail());
            userLogin.setRole(currentUser.getRole());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name="refresh_token",defaultValue = "abc") String refresh_token
    ) throws IdInvalidException {
        if(refresh_token.equals("abc")){
            throw new IdInvalidException("Không có refresh token ở cookie");
        }

        //check valid
        Jwt decodedToken=this.securityUtil.checkValidRefreshToken(refresh_token);
        String email=decodedToken.getSubject();

        //check user by token and email
        User currentUserDB=this.userService.getUserByRefreshTokenAndEmail(refresh_token,email);
        if(currentUserDB==null){
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }


        ResLoginDTO res=new ResLoginDTO();
        User currentUser=this.userService.getByUsername(email);
        if(currentUser!=null){
            ResLoginDTO.UserLogin userLogin=new ResLoginDTO.UserLogin(currentUser.getId(),currentUser.getEmail(),currentUser.getName(),currentUser.getRole());
            res.setUser(userLogin);
        }

        //create token
        String access_token=this.securityUtil.createAccessToken(email,res);
        res.setAccessToken(access_token);

        //create refresh token
        String new_refresh_token=this.securityUtil.createRefreshToken(email, res);

        //update user with refreshToken
        this.userService.updateUserToken(new_refresh_token, email);

        //set cookies
        ResponseCookie resCookies=ResponseCookie.from("refresh_token",new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,resCookies.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException{
        String email=SecurityUtil.getCurrentUserLogin().isPresent()?
                SecurityUtil.getCurrentUserLogin().get() : "";

        if(email.equals("")){
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        this.userService.updateUserToken(null,email);

        ResponseCookie deleteSpringCookies=ResponseCookie.from("refresh_token",null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookies.toString())
                .body(null);
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> register(@RequestBody @Valid User userDTO) throws IdInvalidException{
        boolean isEmailExist=this.userService.isEmailExist(userDTO.getEmail());

        if(isEmailExist){
            throw new IdInvalidException("Email "+userDTO.getEmail()+" đã tồn tại, vui lòng dùng email khác");
        }

        String hashPassword=this.passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(hashPassword);
        User ericUser=this.userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUser(ericUser));
    }

}
