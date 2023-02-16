package com.poly.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.dao.OrderDao;
import com.poly.dao.OrderDetailDao;
import com.poly.entity.Order;
import com.poly.service.OrderDetailService;
import com.poly.service.OrderService;

@Controller
public class OrderController {
	@Autowired
	OrderService orderService;
	@Autowired
	OrderDetailService orderDetailService;
	@Autowired
	OrderDao odao;
	@Autowired
	OrderDetailDao orderDetailDao;

	@RequestMapping("/order/checkout")
	public String checkout() {
		return "user/order/checkout";
	}

	@RequestMapping("/order/detail/{id}")
	public String detail(@PathVariable("id") Integer id, Model model) {
		model.addAttribute("order", orderService.findById(id));
		return "user/order/detail";
	}

	@RequestMapping("/order/remove")
	public String remove(@RequestParam("order_id") Integer id) {
		Order order = odao.getById(id);
		order.setStatus(4);
		odao.save(order);
		return "redirect:/home/info";
	}
}