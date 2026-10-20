package com.example.Shopping_Cart.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.Shopping_Cart.model.UserDtls;
import com.example.Shopping_Cart.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getAttribute("email");
		String name = oAuth2User.getAttribute("name");

		UserDtls user = userRepository.findByEmail(email);
		if (user == null) {
			user = new UserDtls();
			user.setEmail(email);
			user.setName(name);
			user.setRole("ROLE_USER");
			user.setIsEnable(true);
			userRepository.save(user);
		}

		String token = jwtService.generateToken(email, user.getRole());

		Cookie cookie = new Cookie("JWT_TOKEN", token);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(24 * 60 * 60);
		response.addCookie(cookie);

		response.sendRedirect("/");
	}
}