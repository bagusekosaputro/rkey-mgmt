package com.rkey.returnmgmt;

import com.rkey.returnmgmt.helper.JWTRequestFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SpringBootApplication
public class ReturnMgmtApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReturnMgmtApplication.class, args);
	}

	@EnableWebSecurity
	@Configuration
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable()
					.addFilterAfter(new JWTRequestFilter(), UsernamePasswordAuthenticationFilter.class)
					.authorizeRequests()
					.antMatchers("/pending/return", "/return/{id}").permitAll()
					.anyRequest().authenticated();
		}
	}
}
