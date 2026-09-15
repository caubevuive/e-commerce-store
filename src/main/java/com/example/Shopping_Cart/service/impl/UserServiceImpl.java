package com.example.Shopping_Cart.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Shopping_Cart.model.UserDtls;
import com.example.Shopping_Cart.repository.UserRepository;
import com.example.Shopping_Cart.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDtls saveUser(UserDtls user, MultipartFile file) {

		user.setRole("ROLE_USER");
		user.setIsEnable(true);

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);

		UserDtls saveUser = userRepository.save(user);

		if (saveUser != null && !file.isEmpty()) {

			try {

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return saveUser;
	}

	@Override
	public Boolean existsEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public UserDtls getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<UserDtls> getUsers(String role) {
		return userRepository.findByRole(role);
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		Optional<UserDtls> findByUser = userRepository.findById(id);

		if (findByUser.isPresent()) {
			UserDtls userDtls = findByUser.get();
			userDtls.setIsEnable(status);
			userRepository.save(userDtls);
			return true;
		}
		return false;
	}
}