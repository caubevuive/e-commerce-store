package com.example.Shopping_Cart.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Shopping_Cart.model.Cart;
import com.example.Shopping_Cart.model.OrderRequest;
import com.example.Shopping_Cart.model.ProductOrder;
import com.example.Shopping_Cart.model.UserDtls;
import com.example.Shopping_Cart.service.CartService;
import com.example.Shopping_Cart.service.OrderService;
import com.example.Shopping_Cart.service.UserService;

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

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {
		UserDtls user = userService.getUserByEmail(p.getName());
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
	public String orderPage(Principal p, Model m) {
		UserDtls user = userService.getUserByEmail(p.getName());
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
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p, HttpSession session) {
		UserDtls user = userService.getUserByEmail(p.getName());
		orderService.saveOrder(user.getId(), request);

		session.setAttribute("succMsg", "Order placed successfully!");
		return "redirect:/user/success";
	}

	@GetMapping("/success")
	public String loadSuccess() {
		return "success";
	}

	@GetMapping("/my-orders")
	public String myOrder(Principal p, Model m) {
		UserDtls loginUser = userService.getUserByEmail(p.getName());
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
}