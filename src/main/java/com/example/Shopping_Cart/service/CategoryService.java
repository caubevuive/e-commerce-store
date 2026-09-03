package com.example.Shopping_Cart.service;

import java.util.List;

import com.example.Shopping_Cart.model.Category;

public interface CategoryService {

	public Category saveCategory(Category category);

	public List<Category> getAllCategory();

	public Boolean existCategory(String name);

	public Boolean deleteCategory(int id);

	public Category getCategoryById(int id);
}