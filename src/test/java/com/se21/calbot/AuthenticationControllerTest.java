package com.se21.calbot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void tokenSouldThrowsException() throws Exception {
		mvc.perform(get("/test?code=&state="))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string("Auth fail, please try it later or notify developer!"));
	}
}
