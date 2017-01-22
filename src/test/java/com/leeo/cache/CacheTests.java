package com.leeo.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.leeo.sys.user.service.UserAuthService;
import com.leeo.sys.user.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheTests {
	
	@Autowired
	private UserAuthService userAuthService;
	@Autowired
	private UserService userService;

	@Test
	public void cacheable() {
		this.userService.findByUsername("hcy");
	}
	
}
