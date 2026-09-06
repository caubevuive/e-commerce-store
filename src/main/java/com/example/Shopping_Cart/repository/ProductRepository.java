package com.example.Shopping_Cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Shopping_Cart.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}