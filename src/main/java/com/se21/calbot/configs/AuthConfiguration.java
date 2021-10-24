package com.se21.calbot.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.se21.calbot.security.AuthTokenAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class AuthConfiguration  extends WebSecurityConfigurerAdapter {
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.authenticationProvider(this.authAuthenticationProvider() );
    }
    
    @Bean
    public AuthTokenAuthenticationProvider authAuthenticationProvider() {
        return new AuthTokenAuthenticationProvider();
    }

    
    @Bean(name = "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll();
    }

}
