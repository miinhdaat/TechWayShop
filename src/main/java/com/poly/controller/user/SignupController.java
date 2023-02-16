package com.poly.controller.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.dao.AccountDao;
import com.poly.dao.AuthorityDao;
import com.poly.entity.Account;
import com.poly.entity.Authority;
import com.poly.entity.Role;
import com.poly.service.AccountService;
import com.poly.service.SendMailService;

@Controller
public class SignupController {
	@Autowired
	AccountService accservice;

	@Autowired
	AccountDao dao;

	@Autowired
	HttpServletRequest request;

	@Autowired
	PasswordEncoder pe;

	@Autowired
	AuthorityDao authorityDao;

	@Autowired
	SendMailService sendMailService;

	@RequestMapping("/home/signup")
	public String Signup() {
		return "user/acc/signup";
	}

	@RequestMapping("/signupnew")
	public String signup2(Account acc, @RequestParam("confim-Password") String confim, Model model) {
		if (!dao.existsById(acc.getUsername())) {
			Account a = dao.findByEmail(acc.getEmail());
			if (a != null) {
				model.addAttribute("messageRegister", "Tài khoản đã có người sử dụng");
				return "user/acc/signup";
			}
			if (confim.equals(acc.getPassword())) {
				acc.setPhoto("avata.jpg");
				acc.setPassword(pe.encode(confim));
				acc.setActive(false);
				accservice.create(acc);
				Authority au = new Authority();
				au.setAccount(acc);
				Role ro = new Role();
				ro.setRole_id("CUST");
				au.setRole(ro);
				authorityDao.save(au);
				String urlActive = request.getRequestURL().toString().replace("signupnew",
						"signup/active_account?username=" + acc.getUsername());
				sendMailRegister(acc, "Kích hoạt tài khoản của bạn.", urlActive);
				model.addAttribute("message", "Vui lòng kiểm tra email để kích hoạt tài khoản");
				return "user/security/signin";
			}
			model.addAttribute("messageRegister", "Đăng kí không thành công");
			return "user/acc/signup";
		} else {
			model.addAttribute("messageRegister", "Tài khoản đã có người sử dụng");
			return "user/acc/signup";
		}
	}

	public void sendMailRegister(Account account, String subject, String url) {
		System.out.println(account.getUsername());
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<h1>YÊU CẦU XÁC NHẬN EMAIL - KÍCH HOẠT TÀI KHOẢN</h1>\r\n");
		stringBuilder.append("<h3>Xin chào " + account.getFullname() + ",</h3>\r\n");
		stringBuilder.append("<p>Bạn vừa đăng ký thành công tài khoản tại cửa hàng của chúng tôi với tên tài khoản: "
				+ account.getUsername() + " </p>\r\n"
				+ "<p>Vui lòng ấn vào liên kết dưới đây để kích hoạt tài khoản của bạn.</p>\r\n" + url + " \r\n"
				+ "<hr>\r\n" + "<h5>Chúc bạn một ngày tốt lành!</h5>");
		sendMailService.queue(account.getEmail(), subject, stringBuilder.toString());
	}

	@RequestMapping("/signup/active_account")
	public String activeAccount(Model model, @RequestParam("username") String username) {
		Account acc = dao.getById(username);
		acc.setActive(true);
		dao.save(acc);
		model.addAttribute("message", "Tài khoản kích hoạt thành công vui lòng đăng nhập lại");
		return "user/security/signin";
	}
}
