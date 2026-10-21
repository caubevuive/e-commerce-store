package com.example.Shopping_Cart.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public String generateOTP() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	public void sendOtpEmail(String toEmail, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("Account Verification - Ecom Store");
		message.setText("Dear Customer,\n\nYour One-Time Password (OTP) for account verification is: " + otp
				+ "\n\nThis code will expire in 5 minutes.\n\nBest regards,\nEcom Store Support Team");

		mailSender.send(message);
	}
}