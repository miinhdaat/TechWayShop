package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Trademark;

public interface TrademarkDao extends JpaRepository<Trademark, Integer> {
	List<Trademark> findAllByNameLike(String kw);

}
