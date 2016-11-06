//package com.leeo.config;
//
//import javax.persistence.EntityManager;
//
//import org.springframework.beans.factory.config.MethodInvokingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.data.web.config.EnableSpringDataWebSupport;
//
//import com.leeo.common.repository.support.BaseRepositoryImpl;
//
//@Configuration
//@EnableJpaRepositories(repositoryBaseClass=BaseRepositoryImpl.class)
//@EnableSpringDataWebSupport
//public class JpaDataConfig {
//
//	@Bean 
//	  public MethodInvokingBean methodInvokingBean() {
//	    MethodInvokingBean methodInvokingBean = new MethodInvokingBean();
//	    methodInvokingBean.setStaticMethod("com.leeo.common.repository.RepositoryHelper.setEntityManager");
//	    methodInvokingBean.setArguments(new Object[] {EntityManager.class});
//	    
//	    return methodInvokingBean;
//	  }
//}