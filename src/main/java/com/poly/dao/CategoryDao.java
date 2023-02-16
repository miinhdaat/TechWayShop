package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Category;

public interface CategoryDao extends JpaRepository<Category, Integer> {

	List<Category> findAllByNameLike(String kw);
}
