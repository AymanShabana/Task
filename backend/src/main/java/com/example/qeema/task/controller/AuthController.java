package com.example.qeema.task.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.qeema.task.entity.User;
import com.example.qeema.task.service.JwtService;
import com.example.qeema.task.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping("/register")
    public ResponseEntity<Map> register(@RequestBody Map<String,String> body){
        String username = body.get("username");
        String password = body.get("password");
        String passwordHashed = this.bCryptPasswordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordHashed);
        this.userService.save(user);
        String token = this.jwtService.generateToken(user.getId());
        Map res = new HashMap<>();
        res.put("token", token);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String,String> body){
        String username = body.get("username");
        User user = this.userService.findByUsername(username);
        if(user == null){
            return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
        }
        String password = body.get("password");
        String hashedPassword = user.getPassword();
        boolean matched = this.bCryptPasswordEncoder.matches(password, hashedPassword);
        if(matched){
            String token = this.jwtService.generateToken(user.getId());
            Map res = new HashMap<>();
            res.put("token", token);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
        }
    }
}
