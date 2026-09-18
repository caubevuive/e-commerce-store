package com.example.Shopping_Cart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class CommonServiceImpl implements CommonService {

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void removeSessionMessage() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		HttpSession session = request.getSession();
		session.removeAttribute("succMsg");
		session.removeAttribute("errorMsg");
	}

	@Override
	public Boolean sendMail(String url, String recipientEmail) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom("your-email@gmail.com", "Shopping Cart Support");
			helper.setTo(recipientEmail);
			helper.setSubject("Password Reset Request");

			String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
					+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
					+ "\">Change my password</a></p>" + "<br><p>Note: This link will expire in 15 minutes.</p>";

			helper.setText(content, true);
			mailSender.send(message);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}