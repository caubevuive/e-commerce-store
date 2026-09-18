package com.example.Shopping_Cart.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
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

	public static final int MAX_FAILED_ATTEMPTS = 5;

	@Override
	public UserDtls saveUser(UserDtls user, MultipartFile file) {

		user.setRole("ROLE_USER");
		user.setIsEnable(true);

		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		user.setLockTime(null);
		user.setLockDuration(0);

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

	@Override
	public void increaseFailedAttempts(UserDtls user) {
		int currentAttempts = user.getFailedAttempt() != null ? user.getFailedAttempt() : 0;
		int newFailAttempts = currentAttempts + 1;
		userRepository.updateFailedAttempt(newFailAttempts, user.getEmail());
	}

	@Override
	public void resetFailedAttempts(String email) {
		UserDtls user = userRepository.findByEmail(email);
		if (user != null) {
			user.setFailedAttempt(0);
			user.setLockDuration(0); // Reset mức phạt về ban đầu khi nhập đúng
			userRepository.save(user);
		}
	}

	@Override
	public void lock(UserDtls user) {
		int currentDuration = user.getLockDuration() != null ? user.getLockDuration() : 0;
		int nextDuration;

		// Logic tính thời gian khóa tăng dần: 0 -> 1p -> 3p -> 5p -> 7p...
		if (currentDuration == 0) {
			nextDuration = 1;
		} else if (currentDuration == 1) {
			nextDuration = 3;
		} else {
			nextDuration = currentDuration + 2; // Tăng thêm 2 phút mỗi lần vi phạm tiếp theo
		}

		userRepository.accountLock(false, new Date(), MAX_FAILED_ATTEMPTS, nextDuration, user.getEmail());
	}

	@Override
	public boolean unlockWhenTimeExpired(UserDtls user) {
		if (user.getLockTime() == null) {
			return false;
		}

		int duration = user.getLockDuration() != null ? user.getLockDuration() : 1;
		long lockTimeDurationInMillis = duration * 60 * 1000L; // Chuyển số phút phạt ra milisecond

		long lockTimeInMillis = user.getLockTime().getTime();
		long currentTimeInMillis = System.currentTimeMillis();

		if (lockTimeInMillis + lockTimeDurationInMillis < currentTimeInMillis) {
			user.setAccountNonLocked(true);
			user.setLockTime(null);
			user.setFailedAttempt(0);

			userRepository.save(user);
			return true;
		}
		return false;
	}
}