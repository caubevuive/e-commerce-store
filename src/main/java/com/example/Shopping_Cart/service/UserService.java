package com.example.Shopping_Cart.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.Shopping_Cart.model.UserDtls;

public interface UserService {

	public UserDtls saveUser(UserDtls user, MultipartFile file);

	public Boolean existsEmail(String email);

	public UserDtls getUserByEmail(String email);

	public List<UserDtls> getUsers(String role);

	public Boolean updateAccountStatus(Integer id, Boolean status);

	public void increaseFailedAttempts(UserDtls user);

	public void resetFailedAttempts(String email);

	public void lock(UserDtls user);

	public boolean unlockWhenTimeExpired(UserDtls user);
}