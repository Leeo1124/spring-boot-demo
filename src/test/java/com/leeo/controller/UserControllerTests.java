package com.leeo.controller;
import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest//测试成功
//@WebAppConfiguration//测试失败
//@ContextConfiguration(classes = SampleWebThymeleaf3ApplicationTests.class)
public class UserControllerTests {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testHome() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(status().isOk())
				.andExpect(content().string(containsString("<title>Users")));
	}

	@Test
	public void testCreate() throws Exception {
		this.mockMvc.perform(post("/").param("username", "aaa").param("password", "123456"))
				.andExpect(status().isFound())
				.andExpect(header().string("location", RegexMatcher.matches("/[0-9]+")));
	}

	@Test
	public void testCreateValidation() throws Exception {
		this.mockMvc.perform(post("/").param("username", "").param("password", ""))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("is required")));
	}
	
	@Test
	public void list() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(get("/admin/sys/user/query3")
//				.param("page.pn", "1")
//				.param("page.size", "1")
//				.param("sort.id", "desc")
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andReturn();
		System.out.println("--------: "+mvcResult.getResponse().getContentAsString());
	}

	private static class RegexMatcher extends TypeSafeMatcher<String> {
		private final String regex;

		public RegexMatcher(String regex) {
			this.regex = regex;
		}

		public static org.hamcrest.Matcher<java.lang.String> matches(String regex) {
			return new RegexMatcher(regex);
		}

		@Override
		public boolean matchesSafely(String item) {
			return Pattern.compile(this.regex).matcher(item).find();
		}

		@Override
		public void describeMismatchSafely(String item, Description mismatchDescription) {
			mismatchDescription.appendText("was \"").appendText(item).appendText("\"");
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("a string that matches regex: ")
					.appendText(this.regex);
		}
	}
}