package com.example.Shopping_Cart.Controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.Shopping_Cart.config.JwtService;
import com.example.Shopping_Cart.model.Cart;
import com.example.Shopping_Cart.model.Category;
import com.example.Shopping_Cart.model.Product;
import com.example.Shopping_Cart.model.UserDtls;
import com.example.Shopping_Cart.service.CartService;
import com.example.Shopping_Cart.service.CategoryService;
import com.example.Shopping_Cart.service.CommonService;
import com.example.Shopping_Cart.service.EmailService;
import com.example.Shopping_Cart.service.ProductService;
import com.example.Shopping_Cart.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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

	@Autowired
	private JwtService jwtService;

	@Autowired
	private EmailService emailService;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m, jakarta.servlet.http.HttpServletRequest request) {
		String email = null;

		if (request.getCookies() != null) {
			for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
				if ("JWT_TOKEN".equals(cookie.getName())) {
					try {
						email = jwtService.extractUsername(cookie.getValue());
					} catch (Exception e) {

					}
					break;
				}
			}
		}

		if (email == null && p != null) {
			if (p instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) {
				var oauthToken = (org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) p;
				email = oauthToken.getPrincipal().getAttribute("email");
			} else {
				email = p.getName();
			}
		}

		if (email != null) {
			UserDtls userDtls = userService.getUserByEmail(email);
			if (userDtls != null) {
				m.addAttribute("user", userDtls);

				List<Cart> carts = cartService.getCartsByUser(userDtls.getId());
				m.addAttribute("countCart", carts.size());
			} else {
				m.addAttribute("countCart", 0);
			}
		} else {
			m.addAttribute("countCart", 0);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	@GetMapping("/")
	public String index(Model m) {
		List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream()
				.sorted((c1, c2) -> c2.getId().compareTo(c1.getId())).toList();

		List<Product> allActiveProducts = productService.getAllActiveProducts("").stream()
				.sorted((p1, p2) -> p2.getId().compareTo(p1.getId())).toList();

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
	public String register(Model model) {
		model.addAttribute("user", new UserDtls());
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
	public String saveUser(@Valid @ModelAttribute("user") UserDtls user, BindingResult result,
			@RequestParam("img") MultipartFile file, HttpSession session, Model model) {

		if (result.hasErrors()) {
			model.addAttribute("user", user);
			return "register";
		}

		Boolean existsEmail = userService.existsEmail(user.getEmail());
		if (existsEmail) {
			session.setAttribute("errorMsg", "Email address is already registered!");
			return "redirect:/register";
		}

		String otp = emailService.generateOTP();

		session.setAttribute("tempUser", user);
		session.setAttribute("tempFile", file);
		session.setAttribute("generatedOtp", otp);

		// Send OTP via email
		try {
			emailService.sendOtpEmail(user.getEmail(), otp);
			session.setAttribute("succMsg", "An OTP code has been sent to your email. Please verify!");
		} catch (Exception e) {
			session.setAttribute("errorMsg", "Failed to send email. Please check server settings!");
			return "redirect:/register";
		}

		return "redirect:/verify-otp";
	}

	// 2. Display OTP verification page
	@GetMapping("/verify-otp")
	public String showVerifyOtpPage(HttpSession session, Model model) {
		UserDtls tempUser = (UserDtls) session.getAttribute("tempUser");
		if (tempUser == null) {
			return "redirect:/register";
		}
		return "verify_otp";
	}

	// 3. Process OTP code submission and save user to DB if correct
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") String inputOtp, HttpSession session, Model model) {
		String sessionOtp = (String) session.getAttribute("generatedOtp");
		UserDtls tempUser = (UserDtls) session.getAttribute("tempUser");
		MultipartFile file = (MultipartFile) session.getAttribute("tempFile");

		if (tempUser == null || sessionOtp == null) {
			return "redirect:/register";
		}

		if (sessionOtp.equals(inputOtp.trim())) {
			// Save user permanently into database
			UserDtls saveUser = userService.saveUser(tempUser, file);

			if (saveUser != null) {
				// Clear temporary session data
				session.removeAttribute("tempUser");
				session.removeAttribute("tempFile");
				session.removeAttribute("generatedOtp");

				session.setAttribute("succMsg", "Account registered successfully! You can now log in.");
				return "redirect:/signin";
			} else {
				session.setAttribute("errorMsg", "Something went wrong on server during registration.");
				return "redirect:/register";
			}
		} else {
			model.addAttribute("error", "Invalid OTP code. Please try again.");
			return "verify_otp";
		}
	}

	// 4. Handle resend OTP request
	@GetMapping("/resend-otp")
	public String resendOtp(HttpSession session, Model model) {
		UserDtls tempUser = (UserDtls) session.getAttribute("tempUser");
		if (tempUser == null) {
			return "redirect:/register";
		}

		String newOtp = emailService.generateOTP();
		session.setAttribute("generatedOtp", newOtp);
		emailService.sendOtpEmail(tempUser.getEmail(), newOtp);

		model.addAttribute("msg", "A new OTP code has been sent to your email.");
		return "verify_otp";
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

			commonService.sendMail(resetLink, email);

			session.setAttribute("succMsg", "Password reset link has been sent to your email!");

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