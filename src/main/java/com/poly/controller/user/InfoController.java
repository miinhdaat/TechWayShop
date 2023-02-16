package com.poly.controller.user;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.poly.dao.AccountDao;
import com.poly.entity.Account;
import com.poly.entity.Order;
import com.poly.service.AccountService;
import com.poly.service.OrderService;
import com.poly.service.RandomService;
import com.poly.service.UploadService;

@Controller
public class InfoController {
	@Autowired
	OrderService orderService;
	@Autowired
	AccountService accservice;
	@Autowired
	AccountDao dao;
	@Autowired
	PasswordEncoder pe;
	@Autowired
	RandomService randomService;
	@Autowired
	UploadService uploadService;

	@RequestMapping("/home/info")
	public String Info(Model model, @RequestParam("username") String username,
			@RequestParam("page") Optional<Integer> page) {
		Account acc = accservice.findById(username);
		model.addAttribute("acc", acc);
		List<Order> od = orderService.findByUsername(username);
		if (!od.isEmpty()) {
			model.addAttribute("orders", od);
		}
		return "user/acc/infomation";
	}

	@RequestMapping("/infonew")
	public String infonew(Model model, Account acc) throws IOException {
		model.addAttribute("acc", acc);
		if (dao.existsById(acc.getUsername())) {
			accservice.update(acc);
			model.addAttribute("message", "Update thành công");
			return "user/acc/infomation";
		}
		model.addAttribute("message", "Update thất bại");
		return "user/acc/infomation";
	}

	@RequestMapping("/changePW")
	public String signup2(Account acc, @RequestParam("username") String username, @RequestParam("pswnew") String pswnew,
			@RequestParam("confim") String confim, @RequestParam("confim-pswnew") String confimpswnew, Model model) {
		acc = accservice.findById(username);
		if (!pe.matches(confim, acc.getPassword())) {
			model.addAttribute("acc", acc);
			model.addAttribute("message", "Mật khẩu hiện tại không đúng!");
			List<Order> od = orderService.findByUsername(username);
			if (!od.isEmpty()) {
				model.addAttribute("orders", od);
			}
			return "user/acc/infomation";
		} else if (confimpswnew.equals(pswnew)) {
			acc.setPassword(pe.encode(pswnew));
			accservice.update(acc);
			model.addAttribute("acc", acc);
			model.addAttribute("message", "Đổi mật khẩu thành công");
			List<Order> od = orderService.findByUsername(username);
			if (!od.isEmpty()) {
				model.addAttribute("orders", od);
			}
			return "user/acc/infomation";
		} else {
			model.addAttribute("acc", acc);
			model.addAttribute("message", "Xác nhận mật khẩu không đúng");
			List<Order> od = orderService.findByUsername(username);
			if (!od.isEmpty()) {
				model.addAttribute("orders", od);
			}
			return "user/acc/infomation";
		}
	}

	@RequestMapping("/uploadPhoto")
	public String uploadPhoto(Model model, @RequestParam("photo_img") MultipartFile file,
			@RequestParam("idUsernameInfo") String user) throws IOException {
		if (dao.existsById(user)) {
			Account acc = dao.getById(user);
			String getFileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
					.trim();
			String filename = randomService.randomS(user) + "." + getFileType;
			uploadService.save(file, "/assets/images/client", filename);
			acc.setPhoto(filename);
			accservice.update(acc);
			model.addAttribute("acc", acc);
			model.addAttribute("message", "Update thành công");
			return "user/acc/infomation";
		}
		model.addAttribute("message", "Update thất bại");
		return "user/acc/infomation";
	}
}
