package com.example.qeema.task.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.qeema.task.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>{
    public Optional<User> findByUsername(String username);
}
