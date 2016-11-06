package com.leeo.sys.user.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.leeo", locations="classpath:config/sysConfig.properties")
//@Component//这种方式已经过时了，不推荐使用
public class SysConfigProperties {

//    @Value("${com.leeo.name}")//这种方式已经过时了，不推荐使用
    private String name;

//    @Value("${com.leeo.title}")
    private String title;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}