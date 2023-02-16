package com.poly.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.poly.entity.Favorite;
import com.poly.entity.Order;
import com.poly.entity.Response;
import com.poly.service.FavoriteService;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/favorites")
public class FavoriteRestController {
	@Autowired
	FavoriteService dao;

	@Autowired
	HttpServletRequest request;

	Response responseFromServ = new Response();

	@GetMapping
	public List<Favorite> getAll() {
		return dao.findByAllDesc();
	}

	@GetMapping("{product_id}/{username}")
	public List<Favorite> getCheckisShow(@PathVariable("product_id") Integer product_id,
			@PathVariable("username") String username) {

		return dao.checkFavaoriteAdmin(product_id, username);

	}

	@PostMapping()
	public Response create(@RequestBody Favorite favoriteData) {

		List<Favorite> fa = dao.checkFavaoriteAdmin(favoriteData.getProduct().getProduct_id(),
				favoriteData.getAccount().getUsername());
		if (fa.isEmpty()) {
			dao.create(favoriteData);
			responseFromServ.setText("Thêm vào danh sách yêu thích thành công!");
			return responseFromServ;
		} else {
			responseFromServ.setText("Sản phẩm đã được thêm vào danh sách yêu thích trước đó!");
			return responseFromServ;
		}
	}

	@DeleteMapping("{favorite_id}")
	public void delete(@PathVariable("favorite_id") Integer favorite_id) {
		dao.delete(favorite_id);
	}

	@DeleteMapping("{product_id}/{username}")
	public void deleteFavoriteAdmin(@PathVariable("product_id") Integer product_id,
			@PathVariable("username") String username) {
		dao.deleteFavoriteAdmin(product_id, username);
	}
}
