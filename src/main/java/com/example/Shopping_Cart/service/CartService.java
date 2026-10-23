package com.example.Shopping_Cart.service;

import java.util.List;

import com.example.Shopping_Cart.model.Cart;

public interface CartService {
	Cart saveCart(Integer productId, Integer userId);

	List<Cart> getCartsByUser(Integer userId);

	void updateQuantity(String sy, Integer cid);
}