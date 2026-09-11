package com.example.Shopping_Cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Shopping_Cart.model.UserDtls;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {

	public Boolean existsByEmail(String email);
}