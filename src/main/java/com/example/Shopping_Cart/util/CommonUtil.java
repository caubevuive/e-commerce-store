package com.example.Shopping_Cart.util;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.Shopping_Cart.model.ProductOrder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;

	public boolean removeSessionMessage() {
		try {
			HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
					.getSession();

			session.removeAttribute("succMsg");
			session.removeAttribute("errorMsg");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean sendMail(String recipientEmail, String title, String message)
			throws MessagingException, UnsupportedEncodingException {
		MimeMessage messageObj = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(messageObj);

		helper.setFrom("vuq11119@gmail.com", "Ecom Store");
		helper.setTo(recipientEmail);
		helper.setSubject(title);
		helper.setText(message, true);

		mailSender.send(messageObj);
		return true;
	}

	public void sendMailForProductOrder(ProductOrder order, String status) throws Exception {
		String msg = "<p>Hello <b>[[name]]</b>,</p>" + "<p>Thank you for your order. Order details:</p>" + "<hr>"
				+ "<p><b>Order ID:</b> [[orderId]]</p>"
				+ "<p><b>Order Status:</b> <span style='color: blue;'>[[orderStatus]]</span></p>"
				+ "<p><b>Product Name:</b> [[productName]]</p>" + "<p><b>Quantity:</b> [[quantity]]</p>"
				+ "<p><b>Price:</b> $<[[price]]</p>" + "<hr>" + "<p>Thank you for shopping with us at Ecom Store!</p>";

		// Xử lý lấy tên an toàn (hỗ trợ cả đơn mới lẫn đơn cũ)
		String fullName = "Customer";
		if (order.getOrderAddress() != null) {
			fullName = order.getOrderAddress().getFirstName() + " " + order.getOrderAddress().getLastName();
		} else if (order.getFirstName() != null) {
			fullName = order.getFirstName() + " " + order.getLastName();
		}

		// Xử lý lấy email nhận an toàn
		String recipientEmail = "";
		if (order.getOrderAddress() != null) {
			recipientEmail = order.getOrderAddress().getEmail();
		} else {
			recipientEmail = order.getEmail();
		}

		msg = msg.replace("[[name]]", fullName);
		msg = msg.replace("[[orderId]]", order.getOrderId());
		msg = msg.replace("[[orderStatus]]", status);
		msg = msg.replace("[[productName]]", order.getProduct().getTitle());
		msg = msg.replace("[[quantity]]", String.valueOf(order.getQuantity()));
		msg = msg.replace("[[price]]", String.valueOf(order.getPrice()));

		sendMail(recipientEmail, "Product Order Confirmation - Ecom Store", msg);
	}
}