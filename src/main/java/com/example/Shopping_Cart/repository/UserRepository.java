package com.example.Shopping_Cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.Shopping_Cart.model.UserDtls;

@Repository
public interface UserRepository extends JpaRepository<UserDtls, Integer> {

	UserDtls findByEmail(String email);

	Boolean existsByEmail(String email);

	List<UserDtls> findByRole(String role);

	UserDtls findByResetToken(String resetToken);

	@Modifying
	@Transactional
	@Query("UPDATE UserDtls u SET u.failedAttempt = ?1 WHERE u.email = ?2")
	public void updateFailedAttempt(int failedAttempt, String email);

	@Modifying
	@Transactional
	@Query("UPDATE UserDtls u SET u.accountNonLocked = ?1, u.lockTime = ?2, u.failedAttempt = ?3, u.lockDuration = ?4 WHERE u.email = ?5")
	public void accountLock(Boolean accountNonLocked, java.util.Date lockTime, int failedAttempt, int lockDuration,
			String email);
}