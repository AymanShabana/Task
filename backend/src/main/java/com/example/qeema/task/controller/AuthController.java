package com.example.qeema.task.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.qeema.task.entity.User;
import com.example.qeema.task.service.JwtService;
import com.example.qeema.task.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "APIs for user authentication")
public class AuthController {
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                     content = @Content)
    })
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
    @Operation(summary = "Login a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged in successfully",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                     content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                     content = @Content)
    })
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
