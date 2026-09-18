package com.example.Shopping_Cart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Autowired
	private AuthSuccessHandlerImpl authenticationSuccessHandler;

	@Autowired
	private AuthFailureHandlerImpl authenticationFailureHandler;

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
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/signin", "/login", "/register", "/saveUser", "/products/**",
								"/product/**", "/css/**", "/js/**", "/img/**")
						.permitAll().requestMatchers("/user/**").hasRole("USER").requestMatchers("/admin/**")
						.hasRole("ADMIN").anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/signin").loginProcessingUrl("/login")
						.successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler)
						.permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/signin?logout").permitAll());

		return http.build();
	}
}