package com.se21.calbot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"com.se21.*"})
@PropertySource("classpath:application.properties")
public class SpringUnitTestConfig {
	
}