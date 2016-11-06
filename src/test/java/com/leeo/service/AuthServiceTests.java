package com.leeo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Sets;
import com.leeo.sys.auth.service.AuthService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthServiceTests {
	
	@Autowired
	private AuthService authService;

	@Test
	public void findAll() {
//		Set<Long> roles = this.authService.findRoleIds(1l, Sets.newHashSet(1l,2l));
//		assertThat(roles).hasSize(0);
	}
}
