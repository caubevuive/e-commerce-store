package com.example.Shopping_Cart.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.Shopping_Cart.model.Category;
import com.example.Shopping_Cart.service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CommonAdvice {

	@Autowired
	private CategoryService categoryService;

	@ModelAttribute
	public void addHeaderStatus(HttpServletRequest request, Model model) {
		String uri = request.getRequestURI();

		if (uri != null && (uri.equals("/signin") || uri.equals("/login") || uri.equals("/register")
				|| uri.startsWith("/admin") || uri.startsWith("/product"))) {
			model.addAttribute("hideHeader", uri.startsWith("/admin") || uri.equals("/signin") || uri.equals("/login")
					|| uri.equals("/register"));
			model.addAttribute("isProductsPage", false);
			model.addAttribute("hideFooter", true);
		} else if (uri != null && uri.startsWith("/products")) {
			model.addAttribute("hideHeader", false);
			model.addAttribute("isProductsPage", true);
			model.addAttribute("hideFooter", false);
		} else {
			model.addAttribute("hideHeader", false);
			model.addAttribute("isProductsPage", false);
			model.addAttribute("hideFooter", false);
		}

		try {
			List<Category> categories = categoryService.getAllCategory();
			model.addAttribute("categorys", categories);
		} catch (Exception e) {
			model.addAttribute("categorys", null);
		}
	}
}