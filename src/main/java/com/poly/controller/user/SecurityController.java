package com.poly.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.dao.AccountDao;
import com.poly.service.SecurityService;

@Controller
public class SecurityController {
	@Autowired
	AccountDao accdao;
	@Autowired
	SecurityService securityService;

	@RequestMapping("/security/login/form")
	public String loginForm(Model model) {
		model.addAttribute("message", "Để tiếp tục, hãy đăng nhập.");
		return "user/security/signin";
	}

	@RequestMapping("/security/login/success")
	public String loginSuccess(Model model) {
		model.addAttribute("message", "Đăng nhập thành công");
		return "user/security/signin";
	}

	@RequestMapping("/security/login/erorr")
	public String loginErorr(Model model) {
		model.addAttribute("message", "Tài khoản chưa kích hoạt hoặc sai thông tin tài khoản");
		return "user/security/signin";
	}

	@RequestMapping("/security/unauthoried")
	public String unauthoried() {
		return "user/security/404";
	}

	@RequestMapping("/security/logoff/success")
	public String logoff(Model model) {
		model.addAttribute("message", "Đăng xuất thành công ");
		return "user/security/signin";
	}

	@RequestMapping("/security2/login/success")
	public String success(Model model, OAuth2AuthenticationToken oauth2) {
		securityService.loginFromOAuth2(oauth2);
		model.addAttribute("message", "Đăng nhập thành công");
		return "forward:/security/login/success";
	}
}
