package com.example.Shopping_Cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Shopping_Cart.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByIsActiveTrue();

	List<Product> findByCategoryIgnoreCase(String category);

	List<Product> findByCategoryAndIsActiveTrueIgnoreCase(String category);
}