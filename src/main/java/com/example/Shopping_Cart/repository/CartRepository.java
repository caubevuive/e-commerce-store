package com.example.Shopping_Cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Shopping_Cart.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {

	List<Cart> findByProductId(Integer productId);

	Cart findByProductIdAndUserId(Integer productId, Integer userId);

	List<Cart> findByUserId(Integer userId);
}