package com.example.Shopping_Cart.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Shopping_Cart.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByIsActiveTrue();

	List<Product> findByCategoryIgnoreCase(String category);

	List<Product> findByCategoryAndIsActiveTrueIgnoreCase(String category);

	List<Product> findByTitleContainingIgnoreCaseAndIsActive(String ch, boolean isActive);

	List<Product> findByTitleContainingIgnoreCase(String title);

	@Query("SELECT p FROM Product p WHERE p.isDelete = false OR p.isDelete IS NULL")
	Page<Product> findAllNotDeleted(Pageable pageable);

	@Query("SELECT p FROM Product p WHERE (p.isDelete = false OR p.isDelete IS NULL) "
			+ "AND LOWER(p.title) LIKE LOWER(CONCAT('%', :ch, '%'))")
	Page<Product> searchNotDeleted(@Param("ch") String ch, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.isActive = true AND (p.isDelete = false OR p.isDelete IS NULL)")
	Page<Product> findByIsActiveTrue(Pageable pageable);

	@Query("SELECT p FROM Product p WHERE LOWER(p.category) = LOWER(:category) AND p.isActive = true AND (p.isDelete = false OR p.isDelete IS NULL)")
	Page<Product> findByCategoryAndIsActiveTrueIgnoreCase(@Param("category") String category, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')) AND p.isActive = true AND (p.isDelete = false OR p.isDelete IS NULL)")
	Page<Product> findByTitleContainingIgnoreCaseAndIsActiveTrue(@Param("title") String title, Pageable pageable);
}