package com.poly;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.poly.dao.PostDao;

@SpringBootApplication
@EnableScheduling
public class TechwayApplication {
	@Autowired static PostDao postdao;
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(TechwayApplication.class, args);
	}
}
