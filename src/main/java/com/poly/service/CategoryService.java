package com.poly.service;

import java.util.List;

import com.poly.entity.Category;

public interface CategoryService {

	List<Category> findAll();

	List<Category> findAllByNameLike(String kw);

	Category findById(Integer category_id);

	Category create(Category category);

	Category update(Category category);

	void delete(Integer category_id);

}
