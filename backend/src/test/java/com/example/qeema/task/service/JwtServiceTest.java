package com.example.qeema.task.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
    }

    @Test
    public void testGenerateToken() {
        UUID uuid = UUID.randomUUID();
        String token = jwtService.generateToken(uuid);

        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    public void testExtractUUID() {
        UUID uuid = UUID.randomUUID();
        String token = jwtService.generateToken(uuid);

        UUID extractedUuid = jwtService.extractUUID(token);

        assertNotNull(extractedUuid);
        assertEquals(uuid, extractedUuid);
    }

    @Test
    public void testTokenValidity() {
        UUID uuid = UUID.randomUUID();
        String token = jwtService.generateToken(uuid);

        Algorithm algorithm = Algorithm.HMAC256("THISISASECRET");
        Date expiresAt = JWT.require(algorithm).build().verify(token).getExpiresAt();

        assertNotNull(expiresAt);
        assertTrue(expiresAt.after(new Date()));
    }
}
