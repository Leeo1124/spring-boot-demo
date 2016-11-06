package com.leeo.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.leeo.sys.user.entity.User;
import com.leeo.sys.user.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {
	
	@Autowired
	private UserService userService;

	@Test
	public void findAll() {
		Iterable<User> users = this.userService.findAll();
		assertThat(users).hasSize(0);
	}
}
