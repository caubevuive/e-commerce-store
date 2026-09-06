package com.example.Shopping_Cart.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Shopping_Cart.model.Product;
import com.example.Shopping_Cart.repository.ProductRepository;
import com.example.Shopping_Cart.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

}