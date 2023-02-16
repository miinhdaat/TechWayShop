package com.poly.controller.admin;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.poly.dao.AccountDao;
import com.poly.entity.Account;
import com.poly.service.AccountService;
import com.poly.service.RandomService;
import com.poly.service.UploadService;

@Controller
public class InfoAdminController {
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

	@RequestMapping("/admin/info")
	public String Info(Model model, @RequestParam("username") String username) {
		Account acc = accservice.findById(username);
		model.addAttribute("acc", acc);
		return "/admin/account/info";
	}

	@RequestMapping("/admin/infonew")
	public String infonew(Model model, Account acc) throws IOException {
		model.addAttribute("acc", acc);
		if (dao.existsById(acc.getUsername())) {
			accservice.update(acc);
			model.addAttribute("message", "Update thành công");
			return "/admin/account/info";
		}
		model.addAttribute("message", "Update thất bại");
		return "/admin/account/info";
	}

	@RequestMapping("/admin/uploadPhoto")
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
			return "/admin/account/info";
		}
		model.addAttribute("message", "Update thất bại");
		return "/admin/account/info";
	}

	@RequestMapping("/admin/changePW")
	public String signup2(Account acc, @RequestParam("username") String username, @RequestParam("pswnew") String pswnew,
			@RequestParam("confim") String confim, @RequestParam("confim-pswnew") String confimpswnew, Model model) {
		acc = accservice.findById(username);
		if (!pe.matches(confim, acc.getPassword())) {
			model.addAttribute("acc", acc);
			model.addAttribute("message", "Mật khẩu hiện tại không đúng!");
			return "/admin/account/info";
		} else if (confimpswnew.equals(pswnew)) {
			acc.setPassword(pe.encode(pswnew));
			accservice.update(acc);
			model.addAttribute("acc", acc);
			model.addAttribute("message", "Đổi mật khẩu thành công");
			return "/admin/account/info";
		} else {
			model.addAttribute("acc", acc);
			model.addAttribute("message", "Xác nhận mật khẩu không đúng");
			return "/admin/account/info";
		}
	}
}
