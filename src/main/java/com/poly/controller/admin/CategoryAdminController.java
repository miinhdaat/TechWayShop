package com.poly.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.dao.CategoryDao;
import com.poly.service.CategoryService;

@Controller
public class CategoryAdminController {
	@Autowired
	CategoryService categoryService;
	@Autowired
	CategoryDao cdao;

	@RequestMapping("/admin/category/list")
	public String home(Model model) {
		return "admin/category/list";
	}

}
