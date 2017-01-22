package com.leeo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.leeo.sys.user.entity.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTests {
	
	@Autowired
	private UserTestRepository userTestRepository;

	@Test
	@Transactional
	public void findAll() {
		Iterable<User> users = this.userTestRepository.findAll();
		assertThat(users).hasSize(5);
	}
	
	@Test
	public void update() {
		User user = this.userTestRepository.findOne(1l);
		user.setEmail("hcy@163.com");
		this.userTestRepository.saveAndFlush(user);
	}
}