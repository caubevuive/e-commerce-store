package com.example.Shopping_Cart.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.Shopping_Cart.config.JwtService;
import com.example.Shopping_Cart.model.Cart;
import com.example.Shopping_Cart.model.OrderRequest;
import com.example.Shopping_Cart.model.ProductOrder;
import com.example.Shopping_Cart.model.UserDtls;
import com.example.Shopping_Cart.service.CartService;
import com.example.Shopping_Cart.service.OrderService;
import com.example.Shopping_Cart.service.UserService;
import com.example.Shopping_Cart.service.VNPayService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private VNPayService vnpayService;

	private UserDtls getLoggedInUser(Principal p, HttpServletRequest request) {
		String email = null;

		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
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

		return email != null ? userService.getUserByEmail(email) : null;
	}

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m, HttpServletRequest request) {
		UserDtls user = getLoggedInUser(p, request);
		if (user == null) {
			return "redirect:/signin";
		}

		m.addAttribute("user", user);

		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);

		double totalOrderPrice = 0.0;
		if (carts != null && !carts.isEmpty()) {
			for (Cart c : carts) {
				if (c.getProduct() != null && c.getProduct().getDiscountPrice() != null) {
					totalOrderPrice += c.getProduct().getDiscountPrice() * c.getQuantity();
				}
			}
		}
		m.addAttribute("totalOrderPrice", totalOrderPrice);

		return "cart";
	}

	@GetMapping("/cart/quantityUpdate")
	public String updateQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}

	@GetMapping("/orders")
	public String orderPage(Principal p, Model m, HttpServletRequest request) {

		UserDtls user = getLoggedInUser(p, request);
		if (user == null) {
			return "redirect:/signin";
		}

		m.addAttribute("user", user);

		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);

		double totalOrderPrice = 0.0;
		if (carts != null && !carts.isEmpty()) {
			for (Cart c : carts) {
				totalOrderPrice += c.getProduct().getDiscountPrice() * c.getQuantity();
			}
		}
		m.addAttribute("totalOrderPrice", totalOrderPrice);

		return "order";
	}

	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest requestModel, Principal p, HttpServletRequest request,
			HttpSession session) {
		UserDtls user = getLoggedInUser(p, request);
		if (user == null) {
			return "redirect:/signin";
		}

		if ("Online Payment".equals(requestModel.getPaymentType())) {
			List<Cart> carts = cartService.getCartsByUser(user.getId());
			double totalOrderPrice = 0.0;
			if (carts != null && !carts.isEmpty()) {
				for (Cart c : carts) {
					if (c.getProduct() != null && c.getProduct().getDiscountPrice() != null) {
						totalOrderPrice += c.getProduct().getDiscountPrice() * c.getQuantity();
					}
				}
			}

			session.setAttribute("orderRequest", requestModel);
			session.setAttribute("userId", user.getId());
			session.setAttribute("totalOrderPrice", totalOrderPrice);

			return "redirect:/user/qr-payment";
		}

		orderService.saveOrder(user.getId(), requestModel);

		session.setAttribute("succMsg", "Order placed successfully!");
		return "redirect:/user/success";
	}

	@GetMapping("/qr-payment")
	public String loadQRPayment(Principal p, Model m, HttpServletRequest request, HttpSession session) {
		UserDtls user = getLoggedInUser(p, request);
		if (user == null) {
			return "redirect:/signin";
		}
		Double totalOrderPrice = (Double) session.getAttribute("totalOrderPrice");
		if (totalOrderPrice == null) {
			return "redirect:/user/cart";
		}

		m.addAttribute("totalOrderPrice", totalOrderPrice);
		m.addAttribute("bankId", "MB"); // Mã định danh MB Bank trên VietQR
		m.addAttribute("accountNo", "10022048888");
		m.addAttribute("accountName", "VU ANH QUAN");

		return "user/qr_payment";
	}

	@GetMapping("/qr-success")
	public String qrSuccess(Principal p, HttpServletRequest request, HttpSession session) {
		UserDtls user = getLoggedInUser(p, request);
		if (user == null) {
			return "redirect:/signin";
		}

		OrderRequest orderRequest = (OrderRequest) session.getAttribute("orderRequest");
		Integer userId = (Integer) session.getAttribute("userId");

		if (orderRequest != null && userId != null) {

			orderService.saveOrder(userId, orderRequest);

			session.removeAttribute("orderRequest");
			session.removeAttribute("userId");
			session.removeAttribute("totalOrderPrice");
		}

		session.setAttribute("succMsg",
				"Đã nhận yêu cầu! Đơn hàng sẽ được xử lý ngay sau khi shop kiểm tra tài khoản MB Bank.");
		return "redirect:/user/success";
	}

	@GetMapping("/success")
	public String loadSuccess() {
		return "success";
	}

	@GetMapping("/my-orders")
	public String myOrder(Principal p, Model m, HttpServletRequest request) {
		UserDtls loginUser = getLoggedInUser(p, request);
		if (loginUser == null) {
			return "redirect:/signin";
		}

		List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
		m.addAttribute("orders", orders);
		return "user/my_orders";
	}

	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
		String[] statuses = { "Pending", "In Progress", "Delivered", "Cancelled" };
		String status = statuses[st];

		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
		if (updateOrder != null) {
			session.setAttribute("succMsg", "Status Updated Successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong");
		}
		return "redirect:/user/my-orders";
	}

	@GetMapping("/profile")
	public String profile(Principal p, Model m, HttpServletRequest request) {
		UserDtls user = getLoggedInUser(p, request);
		if (user == null) {
			return "redirect:/signin";
		}
		m.addAttribute("user", user);
		return "user/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile img,
			HttpSession session) {
		UserDtls updateUser = userService.updateUserProfile(user, img);
		if (updateUser != null) {
			session.setAttribute("succMsg", "Profile Updated Successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong on server");
		}
		return "redirect:/user/profile";
	}

	@GetMapping("/change-password")
	public String loadChangePassword() {
		return "user/change_password";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, Principal p,
			HttpServletRequest request, HttpSession session) {
		UserDtls user = getLoggedInUser(p, request);
		if (user == null) {
			return "redirect:/signin";
		}

		boolean matchPassword = userService.changePassword(user, oldPassword, newPassword);

		if (matchPassword) {
			session.setAttribute("succMsg", "Password Changed Successfully");
		} else {
			session.setAttribute("errorMsg", "Current Password Incorrect");
		}

		return "redirect:/user/change-password";
	}

	@GetMapping("/check-payment-status")
	@ResponseBody
	public Map<String, Object> checkPaymentStatus(HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		Boolean isPaid = (Boolean) session.getAttribute("isPaid");

		response.put("isPaid", isPaid != null && isPaid);
		return response;
	}
}