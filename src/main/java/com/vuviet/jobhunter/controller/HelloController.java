package com.vuviet.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/home")
    public ResponseEntity<String> getHelloWorld(){
        return ResponseEntity.ok("Hello");
    }
}
