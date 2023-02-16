package com.poly.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import com.poly.dao.ProductDao;

import java.util.Random;

@Controller
@RequestMapping("/report")
public class RootController {
	private static final Random RANDOM = new Random(System.currentTimeMillis());

	@Autowired
	ProductDao DAO;

	@GetMapping
	public String getPieChart(Model model) {
		return "user/char/index3";
	}
}
