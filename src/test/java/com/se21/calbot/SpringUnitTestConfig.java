package com.se21.calbot;

import com.se21.calbot.configs.BotConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@ComponentScan(basePackages = "com.se21.*")
@PropertySource("classpath:application.properties")
public class SpringUnitTestConfig {
    @MockBean
    BotConfiguration botConfiguration;
}