package com.poly.controller.user;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.dao.AccountDao;
import com.poly.dao.PostDao;
import com.poly.dao.ProductDao;
import com.poly.entity.Product;
import com.poly.service.AccountService;
import com.poly.service.SendMailService;

@Controller
public class HomeController {
	@Autowired
	ProductDao pdao;
	@Autowired
	PostDao postdao;
	@Autowired
	AccountService accservice;
	@Autowired
	SendMailService sendMailService;
	@Autowired
	AccountDao dao;

	@RequestMapping("/home/index")
	public String home(Model model) {
		List<Product> list = pdao.findByAllDis();
		model.addAttribute("item1", list);
		List<Product> list1 = pdao.findByAllSpe();
		model.addAttribute("item2", list1);
		List<Product> list2 = pdao.getTop10();
		model.addAttribute("item3", list2);
		return "user/home/index";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		return "user/about/about";
	}
}
