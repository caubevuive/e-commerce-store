package com.example.Shopping_Cart.config;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.Shopping_Cart.model.UserDtls;
import com.example.Shopping_Cart.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthSuccessHandlerImpl implements AuthenticationSuccessHandler {

	@Autowired
	private UserService userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String email = authentication.getName();
		UserDtls userDtls = userService.getUserByEmail(email);

		if (userDtls != null) {

			boolean isNonLocked = userDtls.getAccountNonLocked() != null && userDtls.getAccountNonLocked();

			if (!isNonLocked) {
				if (userService.unlockWhenTimeExpired(userDtls)) {
					userService.resetFailedAttempts(email);
				} else {
					// 1. Lưu exception bị khóa vào Session để trang Login hiển thị ra màn hình
					HttpSession session = request.getSession();
					session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,
							new LockedException("Tài khoản của bạn đã bị khóa. Vui lòng thử lại sau!"));

					// 2. Chuyển hướng lại trang đăng nhập
					response.sendRedirect("/signin?error");
					return;
				}
			} else {
				userService.resetFailedAttempts(email);
			}
		}

		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

		if (roles.contains("ROLE_ADMIN")) {
			response.sendRedirect("/admin/");
		} else {
			response.sendRedirect("/");
		}
	}
}