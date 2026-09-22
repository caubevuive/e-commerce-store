package com.example.Shopping_Cart.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Shopping_Cart.model.Cart;
import com.example.Shopping_Cart.model.Product;
import com.example.Shopping_Cart.model.UserDtls;
import com.example.Shopping_Cart.repository.CartRepository;
import com.example.Shopping_Cart.repository.ProductRepository;
import com.example.Shopping_Cart.repository.UserRepository;
import com.example.Shopping_Cart.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Cart saveCart(Integer productId, Integer userId) {
		UserDtls user = userRepository.findById(userId).orElse(null);
		Product product = productRepository.findById(productId).orElse(null);

		if (user == null || product == null) {
			return null;
		}

		Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);
		Cart cart;
		if (cartStatus == null) {
			cart = new Cart();
			cart.setUser(user);
			cart.setProduct(product);
			cart.setQuantity(1);
		} else {
			cart = cartStatus;
			cart.setQuantity(cart.getQuantity() + 1);
		}
		return cartRepository.save(cart);
	}

	@Override
	public List<Cart> getCartsByUser(Integer userId) {
		List<Cart> carts = cartRepository.findByUserId(userId);

		for (Cart cart : carts) {
			Double totalPrice = cart.getProduct().getDiscountPrice() * cart.getQuantity();
			cart.setTotalPrice(totalPrice);
		}

		return carts;
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {
		Cart cart = cartRepository.findById(cid).orElse(null);
		if (cart != null) {
			int updateQuantity = cart.getQuantity();
			if ("in".equalsIgnoreCase(sy)) {
				updateQuantity += 1;
				cart.setQuantity(updateQuantity);
				cartRepository.save(cart);
			} else if ("de".equalsIgnoreCase(sy)) {
				updateQuantity -= 1;
				if (updateQuantity <= 0) {
					cartRepository.delete(cart);
				} else {
					cart.setQuantity(updateQuantity);
					cartRepository.save(cart);
				}
			}
		}
	}
}