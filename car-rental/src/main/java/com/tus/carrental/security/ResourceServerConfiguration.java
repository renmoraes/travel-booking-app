package com.tus.carrental.security;


import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter{
	
	@Override
	public void configure(HttpSecurity http) throws Exception
	{
		http.authorizeRequests()
				.antMatchers("/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs", "/v3/api-docs", "/webjars/**").permitAll()
				.antMatchers(HttpMethod.DELETE, "/api/v1/carrentals/**").hasRole("ADMIN")
				.anyRequest().authenticated();
	}

}
