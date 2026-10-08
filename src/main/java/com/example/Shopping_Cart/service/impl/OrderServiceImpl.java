package com.example.Shopping_Cart.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

			// Lưu đơn hàng vào database
			ProductOrder saveOrder = orderRepository.save(order);

			// Tự động gửi email xác nhận ngay khi khách đặt hàng thành công
			try {
				commonUtil.sendMailForProductOrder(saveOrder, "Order Placed Successfully");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Xóa giỏ hàng sau khi đặt thành công
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
}