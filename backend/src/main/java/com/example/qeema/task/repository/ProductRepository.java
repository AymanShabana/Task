package com.example.qeema.task.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.qeema.task.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>{
}
