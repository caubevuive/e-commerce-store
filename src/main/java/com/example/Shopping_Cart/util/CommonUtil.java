package com.example.Shopping_Cart.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class CommonUtil {

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
}