package com.example.Shopping_Cart.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.Shopping_Cart.model.UserDtls;
import com.example.Shopping_Cart.repository.UserRepository;
import com.example.Shopping_Cart.service.UserService;
import com.example.Shopping_Cart.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String email = request.getParameter("username");
		UserDtls userDtls = userRepository.findByEmail(email);

		if (userDtls != null) {

			if (Boolean.TRUE.equals(userDtls.getIsEnable())) {

				boolean isNonLocked = userDtls.getAccountNonLocked() != null && userDtls.getAccountNonLocked();

				if (isNonLocked) {

					int failedAttempt = userDtls.getFailedAttempt() != null ? userDtls.getFailedAttempt() : 0;

					if (failedAttempt + 1 < AppConstant.ATTEMPT_TIME) {

						userService.increaseFailedAttempts(userDtls);
					} else {

						userService.increaseFailedAttempts(userDtls);
						userService.lock(userDtls);

						UserDtls updatedUser = userRepository.findByEmail(email);
						int minutes = updatedUser.getLockDuration() != null ? updatedUser.getLockDuration() : 1;

						exception = new LockedException(
								"Your account has been locked for " + minutes + " minute(s) due to 5 failed attempts!");
					}

				} else {

					if (userService.unlockWhenTimeExpired(userDtls)) {
						exception = new LockedException("Your lock period has expired. Please try logging in again!");
					} else {
						int minutes = userDtls.getLockDuration() != null ? userDtls.getLockDuration() : 1;
						exception = new LockedException(
								"Your account is locked for " + minutes + " minute(s). Please try again later!");
					}
				}

			} else {
				exception = new LockedException("Your account has been disabled!");
			}

		} else {
			exception = new LockedException("Invalid email or password!");
		}

		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}
}