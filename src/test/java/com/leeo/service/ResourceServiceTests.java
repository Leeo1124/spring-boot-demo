package com.leeo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;

import com.leeo.sys.resource.entity.Menu;
import com.leeo.sys.resource.entity.Resource;
import com.leeo.sys.resource.service.ResourceService;
import com.leeo.sys.user.entity.User;
import com.leeo.sys.user.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ResourceServiceTests {
	
	@Autowired
	private ResourceService resourceService;
	@Autowired
	private UserService userService;

	@Test
	public void findMenu() {
		List<Resource> resourceList = this.resourceService.findMenu();
		assertThat(resourceList).hasSize(2);
	}
	
	@Test
	public void findMenus() {
		User user = this.userService.findOne(1l);
		List<Menu> menus = this.resourceService.findMenus(user);
		System.out.println(menus);
		assertThat(menus).hasSize(1);
	}
	
	@Test
	public void toJson() {
//		new MappingJackson2HttpMessageConverter();
	}
}
