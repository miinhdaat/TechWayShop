package com.poly.service;

import java.util.List;

import com.poly.entity.Trademark;

public interface TrademarkService {

	List<Trademark> findAll();

	List<Trademark> findAllByNameLike(String kw);

	Trademark findById(Integer trademark_id);

	Trademark create(Trademark trademark_id);

	Trademark update(Trademark trademark_id);

	void delete(Integer trademark_id);

}
