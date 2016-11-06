package com.leeo;

import java.util.Arrays;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.MethodInvokingBean;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.support.FormattingConversionService;

import com.leeo.common.repository.support.BaseRepositoryImpl;
import com.leeo.sys.user.entity.SysConfigProperties;

//使用注解注册Servlet,使spring能够扫描到我们自己编写的servlet和filter
@ServletComponentScan
@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass=BaseRepositoryImpl.class)
@EnableConfigurationProperties({SysConfigProperties.class})
public class SpringBootDemoApplication {
	
	private static final Logger log =
		      LoggerFactory.getLogger(SpringBootDemoApplication.class);

	public static void main(String[] args) {
//		SpringApplication.run(SpringBootDemoApplication.class, args);
		
		SpringApplication app = new SpringApplication(SpringBootDemoApplication.class);
	    app.setWebEnvironment(true);
	    app.run(args);
	    log.info("sysProp http.proxyHost:"+System.getProperty("http.proxyHost"));
	    log.info("sysProp http.proxyPort:"+System.getProperty("http.proxyPort"));
	}
	
	@Bean 
	  public MethodInvokingFactoryBean methodInvokingFactoryBean() {
		MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
		methodInvokingFactoryBean.setStaticMethod("com.leeo.common.entity.search.utils.SearchableConvertUtils.setConversionService");
		methodInvokingFactoryBean.setArguments(new Object[] {new FormattingConversionService()});
	    
	    return methodInvokingFactoryBean;
	  }
	
	 @Bean 
	  public MethodInvokingFactoryBean MethodInvokingFactoryBean() {
	    MethodInvokingFactoryBean mfBean = new MethodInvokingFactoryBean();
	    mfBean.setStaticMethod("java.lang.System.setProperties");
	    Properties props = System.getProperties();
	    props.setProperty("http.proxyHost", "proxy.yqu.com");
	    props.setProperty("http.proxyPort", "80");
	    mfBean.setArguments(new Object[] { props });
	    return mfBean;
	  }
	  
	  @Bean 
	  public MethodInvokingBean methodInvokingBean() {
	    MethodInvokingBean mBean = new MethodInvokingBean();
	    mBean.setStaticMethod("com.leeo.SpringBootDemoApplication.finish");
	    mBean.setArguments(
	        new String[] {
	            "--url", "jdbc:hsqldb:mem:testdb",
	            "--user", "sa", "--password", ""
	            }
	        );
	    return mBean;
	  }
	  
	  public static void finish(String[] args) {
	    log.info("finish "+Arrays.toString(args));
	  }
	  
	  private String getStacks() {
	    StringBuilder sb = new StringBuilder();
	    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
	    sb.append("========================\n");

	    for (int i = 0; i < elements.length; i++) {
	      sb.append(elements[i]).append("\n");
	    }
	    return sb.toString();
	  }  
}
