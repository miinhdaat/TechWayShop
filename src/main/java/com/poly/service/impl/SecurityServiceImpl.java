package com.poly.service.impl;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.poly.dao.AccountDao;
import com.poly.dao.AuthorityDao;
import com.poly.dao.RoleDao;
import com.poly.entity.Account;
import com.poly.entity.Authority;
import com.poly.entity.Role;
import com.poly.service.SecurityService;

@Service
public class SecurityServiceImpl implements SecurityService {
	@Autowired
	AccountDao accountDao;
	@Autowired
	AuthorityDao authorityDao;
	@Autowired
	RoleDao roleDao;
	@Autowired
	HttpServletRequest request;
	@Autowired
	BCryptPasswordEncoder pe;

	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		try {
			Account account = accountDao.findByUsOrM(usernameOrEmail, usernameOrEmail);
//            System.out.println(account.getFullname());
			String[] roles = account.getAuthorities().stream().map(au -> au.getRole().getRole_id())
					.collect(Collectors.toList()).toArray(new String[0]);
			if (account.getActive() == true) {
				return User.withUsername(account.getUsername()).password(account.getPassword()).roles(roles).build();
			} else {
				request.setAttribute("message", "Tài khoản chưa kích hoạt");
				return null;
			}
		} catch (Exception e) {
			throw new UsernameNotFoundException(usernameOrEmail + " not found");
		}
	}

	@Override
	public void loginFromOAuth2(OAuth2AuthenticationToken oauth2) {
		String email = oauth2.getPrincipal().getAttribute("email");
		String[] parts = email.split("@");
		String userN = parts[0];
		String pass = pe.encode("123@abcxyzacas");
		System.out.println(email);
		Account a = accountDao.findByEmail(email);
		if (accountDao.existsById(userN) == false && a == null) {
			Account ac = new Account();
			ac.setEmail(email);
			ac.setUsername(userN);
			ac.setFullname(oauth2.getPrincipal().getAttribute("name"));
			ac.setPassword(pass);
			ac.setPhoto("avata.jpg");
			ac.setActive(true);
			accountDao.save(ac);
			Authority au = new Authority();
			au.setAccount(ac);
			Role ro = new Role();
			ro.setRole_id("CUST");
			au.setRole(ro);
			authorityDao.save(au);
			System.out.println("Vừa tạo tk thành công!");
			UserDetails user = User.withUsername(userN).password(pass).roles("CUST").build();
			Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
		} else {
			Account account = accountDao.findByEmail(email);
			String[] roles = account.getAuthorities().stream().map(au -> au.getRole().getRole_id())
					.collect(Collectors.toList()).toArray(new String[0]);
			UserDetails user = User.withUsername(account.getUsername()).password(pass).roles(roles).build();
			Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
	}
}
