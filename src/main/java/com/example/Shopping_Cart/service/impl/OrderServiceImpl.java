package com.example.Shopping_Cart.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.Shopping_Cart.model.Cart;
import com.example.Shopping_Cart.model.OrderAddress;
import com.example.Shopping_Cart.model.OrderRequest;
import com.example.Shopping_Cart.model.ProductOrder;
import com.example.Shopping_Cart.repository.CartRepository;
import com.example.Shopping_Cart.repository.ProductOrderRepository;
import com.example.Shopping_Cart.service.OrderService;
import com.example.Shopping_Cart.util.CommonUtil;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ProductOrderRepository orderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CommonUtil commonUtil;

	@Override
	public void saveOrder(Integer userid, OrderRequest orderRequest) {
		List<Cart> carts = cartRepository.findByUserId(userid);

		for (Cart cart : carts) {
			ProductOrder order = new ProductOrder();

			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(new Date());

			OrderAddress address = new OrderAddress();
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setState(orderRequest.getState());
			address.setPincode(orderRequest.getPincode());

			order.setOrderAddress(address);

			order.setPaymentType(orderRequest.getPaymentType());
			order.setStatus("In Progress");
			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());
			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());

			ProductOrder saveOrder = orderRepository.save(order);

			try {
				commonUtil.sendMailForProductOrder(saveOrder, "Order Placed Successfully");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		cartRepository.deleteAll(carts);
	}

	@Override
	public List<ProductOrder> getOrdersByUser(Integer userId) {
		return orderRepository.findByUserId(userId);
	}

	@Override
	public ProductOrder updateOrderStatus(Integer orderId, String status) {
		ProductOrder order = orderRepository.findById(orderId).orElse(null);
		if (order != null) {
			order.setStatus(status);
			ProductOrder updateOrder = orderRepository.save(order);

			try {
				commonUtil.sendMailForProductOrder(updateOrder, status);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return updateOrder;
		}
		return null;
	}

	@Override
	public List<ProductOrder> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public ProductOrder getOrderByOrderId(String orderId) {
		return orderRepository.findByOrderId(orderId);
	}

	@Override
	public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return orderRepository.findAll(pageable);
	}
}