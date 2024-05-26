package com.example.qeema.task.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {
    

    private String jwtSecret;
    private Algorithm algorithm;

    private long validity;

    public JwtService(){
        this.jwtSecret = "THISISASECRET";
        this.algorithm = Algorithm.HMAC256(this.jwtSecret);
        this.validity = 1 * 24 * 60 * 60 * 1000;
    }

    public String generateToken(UUID uuid){
        String token = JWT.create()
        .withSubject(uuid.toString())
        .withExpiresAt(new Date(System.currentTimeMillis() + this.validity))
        .sign(this.algorithm);
        return token;
    }

    public UUID extractUUID(String token){
        JWTVerifier jwtVerifier = JWT.require(this.algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return UUID.fromString(decodedJWT.getSubject());
    }
}
