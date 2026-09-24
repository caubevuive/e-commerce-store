package com.example.Shopping_Cart.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Shopping_Cart.model.Cart;
import com.example.Shopping_Cart.model.OrderRequest;
import com.example.Shopping_Cart.model.ProductOrder;
import com.example.Shopping_Cart.repository.CartRepository;
import com.example.Shopping_Cart.repository.ProductOrderRepository;
import com.example.Shopping_Cart.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ProductOrderRepository orderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Override
	public void saveOrder(Integer userid, OrderRequest orderRequest) {
		List<Cart> carts = cartRepository.findByUserId(userid);

		for (Cart cart : carts) {
			ProductOrder order = new ProductOrder();

			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(new Date());

			order.setFirstName(orderRequest.getFirstName());
			order.setLastName(orderRequest.getLastName());
			order.setEmail(orderRequest.getEmail());
			order.setMobileNo(orderRequest.getMobileNo());
			order.setAddress(orderRequest.getAddress());
			order.setCity(orderRequest.getCity());
			order.setState(orderRequest.getState());
			order.setPincode(orderRequest.getPincode());
			order.setPaymentType(orderRequest.getPaymentType());

			order.setStatus("In Progress");
			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());
			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());

			orderRepository.save(order);
		}

		cartRepository.deleteAll(carts);
	}

	@Override
	public List<ProductOrder> getOrdersByUser(Integer userId) {
		return orderRepository.findByUserId(userId);
	}
}