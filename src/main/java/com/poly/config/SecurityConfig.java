package com.poly.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.poly.service.SecurityService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	SecurityService securityService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(securityService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/order/**").authenticated()
				.antMatchers("/favolist/**", "/thanhtoanonline/**").authenticated().antMatchers("/admin/home/index")
				.hasAnyRole("STAF", "DIRE").antMatchers("/admin/product/**").hasAnyRole("STAF", "DIRE")
				.antMatchers("/admin/order/**").hasAnyRole("STAF", "DIRE").antMatchers("/admin/category/**")
				.hasAnyRole("STAF", "DIRE").antMatchers("/admin/trademark/**", "/admin/map/**", "/admin/post/**")
				.hasAnyRole("STAF", "DIRE").antMatchers("/admin/char/**").hasRole("DIRE")
				.antMatchers("/admin/authority/**").hasRole("DIRE").antMatchers("/admin/account/**").hasRole("DIRE")
				.anyRequest().permitAll();
		http.formLogin().loginPage("/security/login/form").loginProcessingUrl("/security/login")
				.defaultSuccessUrl("/home/index", false).failureUrl("/security/login/erorr");
		http.rememberMe().tokenValiditySeconds(86400);
		http.exceptionHandling().accessDeniedPage("/security/unauthoried");
		http.logout().logoutUrl("/security/logoff").logoutSuccessUrl("/security/logoff/success");
		http.oauth2Login().loginPage("/security/login/form").defaultSuccessUrl("/security2/login/success", true)
				.failureUrl("/security/login/form").authorizationEndpoint().baseUri("/security2/authrization");
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}
}
