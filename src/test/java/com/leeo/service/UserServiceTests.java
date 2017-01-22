package com.leeo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.base.Splitter;
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
		assertThat(users).hasSize(5);
	}
	
	@Test
	public void findByIds() {
		List<Long> ids = Arrays.asList(StringUtils.split("1,2", ',')).stream()
				.filter(id -> NumberUtils.isNumber(id))
				.map(id -> Long.valueOf(id))
				.collect(Collectors.toList());
		Iterable<User> users = this.userService.findAll(ids);
		assertThat(users).hasSize(2);
	}
	
	@Test
	public void findByIds2() {
		Iterable<User> users = this.userService.findAll(new Long[]{1l,2l});
		assertThat(users).hasSize(2);
	}
	
	@Test
	public void findbySql() {
		String sql = "select * from sys_user where deleted = ?";
		List<User> users = this.userService.findbySql(sql, 0);
		users.stream().forEach(user -> System.out.println(user));
		assertThat(users).hasSize(5);
	}
	
	@Test
	public void findListbySql() {
		String sql = "select id from sys_user where deleted = ?";
		List<BigInteger> users = this.userService.findListbySql(sql, 0);
		users.stream().forEach(user -> System.out.println(user));
		assertThat(users).hasSize(5);
	}
	
	@Test
	public void update() {
		User user = this.userService.findOne(1l);
//		user.setEmail("hcy@163.com");
//		this.userService.update(user);
	}
	
	@Test
	public void executeSql() {
		String sql = "update sys_user set email = ? where id = ?";
		List<Object> values = new ArrayList<>();
		values.add("hcy@163.com");
		values.add(1);
		int count = this.userService.executeSql(sql, values);
		assertThat(count == 1);
	}
	
}
