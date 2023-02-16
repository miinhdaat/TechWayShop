package com.poly.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.util.ArrayUtils;
import com.poly.dao.AccountDao;
import com.poly.dao.PostDao;
import com.poly.dao.ProductDao;
import com.poly.dao.VoteDao;
import com.poly.entity.PathSaveFile;
import com.poly.entity.Response;
import com.poly.service.DeleteJunkImages;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/deleteRunk")
public class DeleteImageJunk {
	@Autowired
	DeleteJunkImages deleteJunkImages;
	@Autowired
	PostDao postDao;
	@Autowired
	AccountDao accountDao;
	@Autowired
	VoteDao voteDao;
	@Autowired
	ProductDao productDao;
	
	@GetMapping
	public Response junk() {
		Response re = new Response();
		try {
			deleteJunkImages();
			re.setText("Xoá thành công!");
			return re;
		} catch (Exception e) {
			re.setText("Xoá thất bại!");
			return re;
		}
	}
	
	public void deleteJunkImages() {
		deleteJunkImages.DeleteI(PathSaveFile.PATH+("/assets/images/post"), postDao.findAllImage());
		deleteJunkImages.DeleteI(PathSaveFile.PATH+("/assets/images/client"), accountDao.findAllImage());
		deleteJunkImages.DeleteI(PathSaveFile.PATH+("/assets/images/comments"), voteDao.findAllImage());
		deleteJunkImages.DeleteI(PathSaveFile.PATH+("/assets/images/products"), productImages());
	}
	
	public String[] productImages() {
		String[] i1 = productDao.findAllImage1();
		String[] i2 = productDao.findAllImage2();
		String[] i3 = productDao.findAllImage3();
		String[] i4 = productDao.findAllImage4(); 
		String[] all = (String[])ArrayUtils.concat(i1, i2, i3, i4);
		return all;
	}
}
