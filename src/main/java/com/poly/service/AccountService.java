package com.poly.service;

import java.util.List;

import com.poly.entity.Account;

public interface AccountService {
	Account findById(String username);

	List<Account> getAdminnostrators();

	List<Account> findAll();

	List<Account> search(String keyword);

	Account create(Account account);

	Account update(Account account);

	void delete(String username);
}
