package com.poly.controller.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.dao.AccountDao;
import com.poly.entity.Account;
import com.poly.service.AccountService;
import com.poly.service.SendMailService;

@Controller
public class ForgotControler {
	private static Random generator = new Random();
	private static final String alpha = "abcdefghijklmnopqrstuvwxyz"; // a-z
	private static final String alphaUpperCase = alpha.toUpperCase(); // A-Z
	private static final String digits = "0123456789"; // 0-9
	private static final String specials = "~=+%^*/()[]{}/!@#$?|";
	private static final String ALL = alpha + alphaUpperCase + digits + specials;

	@Autowired
	AccountService accservice;

	@Autowired
	AccountDao dao;

	@Autowired
	SendMailService sendMailService;

	@Autowired
	PasswordEncoder pe;

	@RequestMapping("/home/forgot")
	public String Login() {
		return "user/acc/forgot";
	}

	@PostMapping("/forgotPW")
	public String confirmmk(Model model, @RequestParam("name") String name, @RequestParam("email") String email) {
		if (!dao.existsById(name)) {
			model.addAttribute("message", "Tên tài khoản không tồn tại!");
			return "user/acc/forgot";
		}
		String username = name.trim();
		Account acc = accservice.findById(username);

		try {
			if (email.equals(acc.getEmail())) {

				String Pss = randomPassword();
				acc.setPassword(pe.encode(Pss));
				dao.save(acc);
				sendMailForgot(acc, Pss, "YÊU CẦU KHÔI PHỤC LẠI MẬT KHẨU");

				model.addAttribute("message", "Gửi email thành công");
				return "user/acc/forgot";
			} else {
				model.addAttribute("message", "Email không khớp với email đã đăng kí ");
				return "user/acc/forgot";
			}
		} catch (Exception e) {
			return e.getMessage();
		}

	}

	public void sendMailForgot(Account account, String rdPass, String subject) {
		System.out.println(account.getUsername());
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<h1>YÊU CẦU KHÔI PHỤC LẠI MẬT KHẨU</h1>\r\n");
		stringBuilder.append("<h3>Xin chào " + account.getFullname() + ",</h3>\r\n");
		stringBuilder.append("<p>Bạn vừa yêu cầu khôi phục lại mật khẩu trên website của chúng tôi. </p>\r\n"
				+ "<p>Tên tài khoản: " + account.getUsername() + " </p>\r\n" + "<p>Mật khẩu mới của bạn: " + rdPass
				+ " </p>\r\n" + "<p>Vui lòng thay đổi mật khẩu mới sau khi đăng nhập thành công.</p>\r\n" + "<hr>\r\n"
				+ "<h5>Chúc bạn một ngày tốt lành!</h5>");
		sendMailService.queue(account.getEmail(), subject, stringBuilder.toString());
	}

	public String randomPassword() {
		List<String> result = new ArrayList<>();
		Consumer<String> appendChar = s -> {
			int number = randomNumber(0, s.length() - 1);
			result.add("" + s.charAt(number));
		};
		appendChar.accept(digits);
		appendChar.accept(specials);
		while (result.size() < 10) {
			appendChar.accept(ALL);
		}
		Collections.shuffle(result, generator);
		return String.join("", result);
	}

	public static int randomNumber(int min, int max) {
		return generator.nextInt((max - min) + 1) + min;
	}
}
