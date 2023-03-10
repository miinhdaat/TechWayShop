package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entity.Account;
import com.poly.entity.Authority;

public interface AuthorityDao extends JpaRepository<Authority, Integer> {
	@Query("SELECT DISTINCT a FROM Authority a WHERE a.account IN ?1")
	List<Authority> authritiesOf(List<Account> accounts);

	@Query("SELECT a FROM Authority a WHERE a.account.Username=?1")
	Authority findByUsername(String username);
}
