package com.example.Shopping_Cart.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.Shopping_Cart.model.UserDtls;

public interface UserService {

	public UserDtls saveUser(UserDtls user, MultipartFile file);

	public Boolean existsEmail(String email);

	public UserDtls getUserByEmail(String email);
}