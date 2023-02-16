package com.poly.controller.user;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.poly.dao.VoteDao;
import com.poly.entity.Account;
import com.poly.entity.Product;
import com.poly.entity.Vote;
import com.poly.service.CommentService;
import com.poly.service.RandomService;
import com.poly.service.UploadService;

@Controller
public class VoteController {
	@Autowired
	HttpServletRequest request;
	@Autowired
	VoteDao dao;
	@Autowired
	CommentService commservice;
	@Autowired
	RandomService randomService;
	@Autowired
	UploadService uploadService;

	@RequestMapping("/vote/add")
	public String commentAdd(Model model, @RequestParam("product_id") Integer product_id,
			@RequestParam("content") String content, @RequestParam("vote") Integer vote,
			@RequestParam("image_comment") MultipartFile file) throws IOException {
		String username = request.getRemoteUser();
		Vote vo = new Vote();
		Account acc = new Account();
		acc.setUsername(username);
		Product pro = new Product();
		pro.setProduct_id(product_id);
		vo.setAccount(acc);
		vo.setProduct(pro);
		vo.setVote_Content(content);
		vo.setVote(vote);
		if (file != null) {
			String getFileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
					.trim();
			String filename = randomService.randomS("cmt") + "." + getFileType;
			uploadService.save(file, "/assets/images/comments/", filename);
			vo.setImage(filename);
		}
		dao.save(vo);

		return "redirect:/product/detail/" + product_id;
	}

}
