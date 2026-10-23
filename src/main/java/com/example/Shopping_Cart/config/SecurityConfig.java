package com.example.Shopping_Cart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	@Autowired
	private AuthSuccessHandlerImpl authenticationSuccessHandler;

	@Autowired
	private AuthFailureHandlerImpl authenticationFailureHandler;

	@Autowired
	private OAuth2SuccessHandler oAuth2SuccessHandler;

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		return daoAuthenticationProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/signin", "/login", "/register", "/saveUser", "/verify-otp", "/resend-otp",
						"/forgot-password", "/send-forgot-password", "/reset-password", "/change-password", "/error",
						"/products/**", "/product/**", "/css/**", "/js/**", "/img/**")
				.permitAll().requestMatchers("/user/**").hasRole("USER").requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/signin").loginProcessingUrl("/login")
						.successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler)
						.permitAll())

				.oauth2Login(oauth2 -> oauth2.loginPage("/signin").successHandler(oAuth2SuccessHandler))
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/signin?logout")
						.deleteCookies("JWT_TOKEN").permitAll());

		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}