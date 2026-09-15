package com.example.Shopping_Cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Shopping_Cart.model.UserDtls;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {

	UserDtls findByEmail(String email);

	Boolean existsByEmail(String email);

	List<UserDtls> findByRole(String role);
}