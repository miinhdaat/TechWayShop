package com.poly.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.service.SendMailService;

@Controller
public class ContactController {
	@Autowired
	SendMailService sendMailService;

	@RequestMapping("/contact-us")
	public String contact(Model model) {
		model.addAttribute("message",
				"Chúng tôi hợp tác với những thương hiệu và con người đầy tham vọng; chúng tôi muốn\r\n"
						+ "                                    cùng nhau xây dựng một điều gì đó tuyệt vời.");
		return "user/contact/contact";
	}

	@RequestMapping("/contact")
	public String sendMail(@RequestParam("name") String name, @RequestParam("mail") String mail,
			@RequestParam("phone") String phone, @RequestParam("subject") String subject,
			@RequestParam("content") String content, Model model) {
		sendMailActione(mail, name, subject, phone, content);
		model.addAttribute("message", "Gửi thành công!");
		return "user/contact/contact";
	}
	
	public void sendMailActione(String mail, String name ,String subject, String phone, String content) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<h3>Ý KIẾN TỪ KHÁCH HÀNG</h3>\r\n");
		stringBuilder.append("<p>Tên khách hàng: " + name + "</p>\r\n");
		stringBuilder.append("<p>Email khách hàng: " + mail + "</p>\r\n");
		stringBuilder.append("<p>SĐT khách hàng: " + phone + "</p>\r\n");
		stringBuilder.append("<p>Vấn đề: " + subject + "</p>\r\n");
		stringBuilder.append("<p>Nội dung ý kiến: " + content + "</p>\r\n");
		stringBuilder.append("<h5>Chúc bạn một ngày tốt lành!</h5>");
		sendMailService.queue("hastore.project@gmail.com", "Ý KIẾN TỪ KHÁCH HÀNG", stringBuilder.toString());
	}
}
