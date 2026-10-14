package com.example.Shopping_Cart.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Shopping_Cart.model.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

	List<ProductOrder> findByUserId(Integer userId);

	ProductOrder findByOrderId(String orderId);

	Page<ProductOrder> findAll(Pageable pageable);

	Page<ProductOrder> findByUserId(Integer userId, Pageable pageable);
}