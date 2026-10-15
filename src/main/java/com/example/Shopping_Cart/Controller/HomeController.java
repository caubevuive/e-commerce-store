package com.example.Shopping_Cart.Controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.Shopping_Cart.model.Cart;
import com.example.Shopping_Cart.model.Category;
import com.example.Shopping_Cart.model.Product;
import com.example.Shopping_Cart.model.UserDtls;
import com.example.Shopping_Cart.service.CartService;
import com.example.Shopping_Cart.service.CategoryService;
import com.example.Shopping_Cart.service.CommonService;
import com.example.Shopping_Cart.service.ProductService;
import com.example.Shopping_Cart.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private CartService cartService;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);

			List<Cart> carts = cartService.getCartsByUser(userDtls.getId());
			m.addAttribute("countCart", carts.size());
		} else {
			m.addAttribute("countCart", 0);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	@GetMapping("/")
	public String index(Model m) {
		List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream()
				.sorted((c1, c2) -> c2.getId().compareTo(c1.getId())).limit(6).toList();

		List<Product> allActiveProducts = productService.getAllActiveProducts("").stream()
				.sorted((p1, p2) -> p2.getId().compareTo(p1.getId())).limit(8).toList();

		m.addAttribute("category", allActiveCategory);
		m.addAttribute("categorys", allActiveCategory);
		m.addAttribute("products", allActiveProducts);
		return "index";
	}

	@GetMapping({ "/login", "/signin" })
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/products")
	public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "8") Integer pageSize,
			@RequestParam(name = "ch", defaultValue = "") String ch) {

		Page<Product> page = null;

		if (ch != null && !ch.trim().isEmpty()) {
			page = productService.searchActiveProductPagination(pageNo, pageSize, ch);
		} else {
			page = productService.getAllActiveProductPagination(pageNo, pageSize, category);
		}

		List<Product> products = page.getContent();

		m.addAttribute("products", products);
		m.addAttribute("productsSize", products.size());
		m.addAttribute("categories", categoryService.getAllCategory());
		m.addAttribute("paramValue", ch);
		m.addAttribute("selectedCategory", category);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "product";
	}

	@GetMapping("/product/{id}")
	public String product(@PathVariable int id, Model m) {
		Product productById = productService.getProductById(id);
		m.addAttribute("product", productById);
		return "view_product";
	}

	@GetMapping("/add-cart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);

		if (saveCart != null) {
			session.setAttribute("succMsg", "Product added to cart successfully");
		} else {
			session.setAttribute("errorMsg", "Failed to add product to cart");
		}
		return "redirect:/product/" + pid;
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file,
			HttpSession session) {
		Boolean existsEmail = userService.existsEmail(user.getEmail());

		if (existsEmail) {
			session.setAttribute("errorMsg", "Email already exists!");
		} else {
			UserDtls saveUser = userService.saveUser(user, file);

			if (!ObjectUtils.isEmpty(saveUser)) {
				session.setAttribute("succMsg", "Registered successfully!");
			} else {
				session.setAttribute("errorMsg", "Something went wrong on server!");
			}
		}
		return "redirect:/register";
	}

	@GetMapping("/forgot-password")
	public String showForgotPassword() {
		return "forgot_password";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpServletRequest request, HttpSession session) {
		UserDtls user = userService.getUserByEmail(email);

		if (user != null) {
			String token = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, token);

			String resetLink = request.getRequestURL().toString().replace(request.getRequestURI(), "")
					+ "/reset-password?token=" + token;

			Boolean sendMail = commonService.sendMail(resetLink, email);
			if (sendMail) {
				session.setAttribute("succMsg", "Password reset link has been sent to your email!");
			} else {
				session.setAttribute("errorMsg", "Failed to send email. Please check server settings!");
			}
		} else {
			session.setAttribute("errorMsg", "Email address not found!");
		}
		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, Model m) {
		UserDtls user = userService.getUserByToken(token);

		if (user == null) {
			m.addAttribute("msg", "Your token is invalid or has expired!");
			return "message";
		}
		m.addAttribute("token", token);
		return "reset_password";
	}

	@PostMapping("/reset-password")
	public String processResetPassword(@RequestParam String token, @RequestParam String password, HttpSession session) {
		UserDtls user = userService.getUserByToken(token);

		if (user == null) {
			session.setAttribute("errorMsg", "Your token is invalid or has expired!");
		} else {
			userService.updateUserPassword(user, password);
			session.setAttribute("succMsg", "Password reset successfully! Please login.");
		}
		return "redirect:/signin";
	}
}