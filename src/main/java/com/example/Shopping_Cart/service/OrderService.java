package com.example.Shopping_Cart.service;

import java.util.List;

import com.example.Shopping_Cart.model.OrderRequest;
import com.example.Shopping_Cart.model.ProductOrder;

public interface OrderService {

	void saveOrder(Integer userid, OrderRequest orderRequest);

	List<ProductOrder> getOrdersByUser(Integer userId);

	public ProductOrder updateOrderStatus(Integer orderId, String status);
}