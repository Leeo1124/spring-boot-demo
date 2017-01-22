package com.leeo.config;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.leeo.cache.RedisCache;
import com.leeo.cache.RedisCacheManager;
import com.leeo.redis.RedisManager;
import com.leeo.redis.RedisSessionDAO;
import com.leeo.shiro.MShiroFilterFactoryBean;
import com.leeo.shiro.UserRealm;
import com.leeo.sys.user.service.UserService;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;

/**
 * Shiro 配置
 *
 */
@Configuration
public class ShiroConfig {

	private static final Logger logger = LoggerFactory.getLogger(ShiroConfig.class);

	/**
	 * 缓存管理器 使用Ehcache实现
	 * 
	 * @return
	 */
	@Bean(name = "cacheManager")
	public EhCacheManager getEhCacheManager() {
		EhCacheManager em = new EhCacheManager();
		em.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");

		return em;
	}

	/**
	 * 凭证匹配器
	 * 
	 * @return
	 */
	@Bean(name = "credentialsMatcher")
	public HashedCredentialsMatcher hashedCredentialsMatcher() {
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();

		hashedCredentialsMatcher.setHashAlgorithmName("SHA-1");// 散列算法:这里使用MD5算法;
		hashedCredentialsMatcher.setHashIterations(1024);// 散列的次数，比如散列两次，相当于
															// md5(md5(""));
		hashedCredentialsMatcher.setHashSalted(true);
		hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);

		return hashedCredentialsMatcher;
	}

	/**
	 * Realm实现
	 * 
	 * @param cacheManager
	 * @return
	 */
	@Bean(name = "userRealm")
	public UserRealm userRealm(RedisCacheManager redisCacheManager) {
		UserRealm realm = new UserRealm();
		realm.setCacheManager(redisCacheManager);
		realm.setCredentialsMatcher(hashedCredentialsMatcher());
		return realm;
	}
	
	/**
	 * 自定义redisManager-redis
	 */
	@Bean(name = "redisCacheManager")
	public RedisCacheManager getRedisCacheManager(){
		RedisCacheManager redisCacheManager = new RedisCacheManager();
		redisCacheManager.setRedisManager(getRedisManager());
		
		return redisCacheManager;
	}
	
	/**
	 * 自定义cacheManager
	 * @return
	 */
	@Bean(name = "redisCache")
	public RedisCache getRedisCache(){
		return new RedisCache(getRedisManager());
	}
	
	/**
	 * shiro redisManager
	 * @return
	 */
	@Bean(name = "redisManager")
	public RedisManager getRedisManager(){
		RedisManager redisManager = new RedisManager();
		redisManager.setHost("127.0.0.1");
		redisManager.setPort(32768);
		redisManager.setExpire(1800);
		
		return redisManager;
	}
	
	/**
	 * session会话存储的实现类
	 * @return
	 */
	@Bean(name = "redisShiroSessionDAO")
	public RedisSessionDAO  getRedisSessionDAO(){
		RedisSessionDAO sessionDAO = new RedisSessionDAO();
		sessionDAO.setRedisManager(getRedisManager());
		
		return sessionDAO;
	}
	
	/**
	 * sessionIdCookie的实现,用于重写覆盖容器默认的JSESSIONID
	 * @return
	 */
	@Bean(name = "sharesession")
	public SimpleCookie getSharesession(){
		SimpleCookie simpleCookie= new SimpleCookie();
		simpleCookie.setName("SHAREJSESSIONID");
		simpleCookie.setPath("/");
		simpleCookie.setHttpOnly(true);
		
		return simpleCookie;
	}
	
	@Bean(name = "rememberMeCookie")
	public SimpleCookie getRememberMeCookie(){
		//这个参数是cookie的名称，对应前端的checkbox的name = rememberMe  
	    SimpleCookie simpleCookie = new SimpleCookie("rememberMe");  
//		simpleCookie.setDomain(domain);
		simpleCookie.setPath("/");
		simpleCookie.setHttpOnly(true);
		//记住我cookie生效时间30天 ,单位秒
		simpleCookie.setMaxAge(259200);
		
		return simpleCookie;
	}
	
	/**
	 * rememberMe管理器
	 * @return
	 */
	@Bean(name = "rememberMeManager")
	public CookieRememberMeManager getRememberMeManager(){
		logger.info("-----------ShiroConfiguration.rememberMeManager()-----------");
		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//		cookieRememberMeManager.setCipherKey(Base64.decode("GAevYnznvgNCURavBhCr1w=="));
		cookieRememberMeManager.setCookie(getRememberMeCookie());
		
		return cookieRememberMeManager;
	}
	
	/**
	 * session管理器
	 * @return
	 */
	@Bean(name = "sessionManager")
	public DefaultWebSessionManager getDefaultWebSessionManager(){
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		//设置全局会话超时时间，默认30分钟(1800000)
		sessionManager.setGlobalSessionTimeout(30*60*1000);
		//是否在会话过期后会调用SessionDAO的delete方法删除会话 默认true
		sessionManager.setDeleteInvalidSessions(true);
		//会话验证器调度时间
		sessionManager.setSessionValidationInterval(30*60*1000);
		//定时检查失效的session
		sessionManager.setSessionValidationSchedulerEnabled(true);
		//session存储的实现
		sessionManager.setSessionDAO(getRedisSessionDAO());
		//sessionIdCookie的实现,用于重写覆盖容器默认的JSESSIONID
		sessionManager.setSessionIdCookie(getSharesession());
		sessionManager.setSessionIdCookieEnabled(true);
		
		return sessionManager;
	}

	/**
	 * 安全管理器
	 * 
	 * @param userRealm
	 * @return
	 */
	@Bean(name = "securityManager")
	public DefaultWebSecurityManager getDefaultWebSecurityManager(UserRealm userRealm) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(userRealm);
		// <!-- 用户授权/认证信息Cache, 采用EhCache 缓存 -->
		securityManager.setCacheManager(getRedisCacheManager());
		securityManager.setSessionManager(getDefaultWebSessionManager());
		securityManager.setRememberMeManager(getRememberMeManager());
		
		return securityManager;
	}

	/**
	 * 注册DelegatingFilterProxy（Shiro） 集成Shiro有2种方法： 1.
	 * 按这个方法自己组装一个FilterRegistrationBean（这种方法更为灵活，可以自己定义UrlPattern，
	 * 在项目使用中你可能会因为一些很但疼的问题最后采用它， 想使用它你可能需要看官网或者已经很了解Shiro的处理原理了） 2.
	 * 直接使用ShiroFilterFactoryBean（这种方法比较简单，其内部对ShiroFilter做了组装工作，
	 * 无法自己定义UrlPattern， 默认拦截 /*）
	 *
	 * @param dispatcherServlet
	 * @return
	 */
	// @Bean
	// public FilterRegistrationBean filterRegistrationBean() {
	// FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
	// filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
	// //
	// 该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
	// filterRegistration.addInitParameter("targetFilterLifecycle", "true");
	// filterRegistration.setEnabled(true);
	// filterRegistration.addUrlPatterns("/*");//
	// 可以自己灵活的定义很多，避免一些根本不需要被Shiro处理的请求被包含进来
	// return filterRegistration;
	// }

	/**
	 * Shiro生命周期处理器
	 * 
	 * @return
	 */
	@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * AOP式方法级权限检查
	 * 
	 * @return
	 */
	@Bean
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
		daap.setProxyTargetClass(true);
		return daap;
	}

	/**
	 * 开启shiro aop注解支持. 使用代理方式;所以需要开启代码支持;
	 * 
	 * @param securityManager
	 * @return
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(
			DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
		aasa.setSecurityManager(securityManager);
		return aasa;
	}

	/**
	 * 加载shiroFilter权限控制规则（从数据库读取然后配置）
	 *
	 */
	private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean, UserService userService) {
		/////////////////////// 下面这些规则配置最好配置到配置文件中 ///////////////////////
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
		// authc：该过滤器下的页面必须验证后才能访问，它是Shiro内置的一个拦截器org.apache.shiro.web.filter.authc.FormAuthenticationFilter
		filterChainDefinitionMap.put("/user", "authc");// 这里为了测试，只限制/user，实际开发中请修改为具体拦截的请求规则
		// anon：它对应的过滤器里面是空的,什么都没做
		logger.info("##################从数据库读取权限规则，加载到shiroFilter中##################");
//		filterChainDefinitionMap.put("/user/edit/**", "authc,perms[user:edit]");// 这里为了测试，固定写死的值，也可以从数据库或其他配置中读取

		filterChainDefinitionMap.put("/static/**", "anon");
		filterChainDefinitionMap.put("/login", "anon");
		filterChainDefinitionMap.put("/**", "authc");// anon 可以理解为不拦截

		// 配置退出过滤器,其中的具体代码Shiro已经替我们实现了
//		 filterChainDefinitionMap.put("/logout", "logout");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
	}

	/**
	 * Shiro的Web过滤器
	 * 
	 * @param securityManager
	 * @param userService
	 * @return
	 */
	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager,
			UserService userService) {

		ShiroFilterFactoryBean shiroFilterFactoryBean = new MShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		
		Map<String, Filter> filters = new LinkedHashMap<String, Filter>();  
        LogoutFilter logoutFilter = new LogoutFilter();  
        logoutFilter.setRedirectUrl("/login");
        filters.put("logout", logoutFilter);  
        shiroFilterFactoryBean.setFilters(filters);  
          
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();  
        filterChainDefinitionMap.put("/hystrix.stream", "anon");
        
        filterChainDefinitionMap.put("/logout", "logout");
        //配置记住我或认证通过可以访问的地址  
        filterChainDefinitionMap.put("/index", "user");
        filterChainDefinitionMap.put("/", "user");

//        filterChainDefinitionMap.put("/user/**", "authc,roles[user]");  
        filterChainDefinitionMap.put("/admin/**","authc");  
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap); 
		
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/login");
		// 登录成功后要跳转的连接
		shiroFilterFactoryBean.setSuccessUrl("/admin/index");
		// 未授权界面;
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");

//		loadShiroFilterChain(shiroFilterFactoryBean, userService);
		return shiroFilterFactoryBean;
	}

	/**
	 * ShiroDialect，为了在thymeleaf里使用shiro的标签的bean
	 * 
	 * @return
	 */
	@Bean
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}

}