package com.poly.service.impl;

import org.springframework.stereotype.Service;

import com.poly.service.RandomService;

@Service
public class RandomServiceImpl implements RandomService {

	@Override
	public String randomS(String id) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(10);
		for (int i = 0; i < 10; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		String rdmS = sb.toString() + "-" + id;
		return rdmS;
	}

}
