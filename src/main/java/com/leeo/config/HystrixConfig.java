package com.leeo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.leeo.filter.HystrixRequestContextServletFilter;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

@Configuration
@EnableHystrix
public class HystrixConfig {

	@Bean
	public HystrixMetricsStreamServlet hystrixMetricsStreamServlet() {
		return new HystrixMetricsStreamServlet();
	}

	@Bean
	public ServletRegistrationBean registration(HystrixMetricsStreamServlet servlet) {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean();
		registrationBean.setServlet(servlet);
		registrationBean.setEnabled(true);// 是否启用该registrationBean
		registrationBean.addUrlMappings("/hystrix.stream");
		
		return registrationBean;
	}
	
	@Bean
    public FilterRegistrationBean indexFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new HystrixRequestContextServletFilter());
        registration.addUrlPatterns("/*");
        
        return registration;
    }
}