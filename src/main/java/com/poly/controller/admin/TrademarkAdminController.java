package com.poly.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.dao.TrademarkDao;
import com.poly.service.TrademarkService;

@Controller
public class TrademarkAdminController {
	@Autowired
	TrademarkService trademarkService;
	@Autowired
	TrademarkDao tdao;

	@RequestMapping("/admin/trademark/list")
	public String home(Model model) {
		return "admin/trademark/list";
	}

}
