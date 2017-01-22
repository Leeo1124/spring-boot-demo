package com.leeo.sys.user.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.leeo.cache.RedisCache;
import com.leeo.common.entity.enums.SysThemesEnum;
import com.leeo.common.utils.UserLogUtils;
import com.leeo.sys.resource.entity.Menu;
import com.leeo.sys.resource.service.ResourceService;
import com.leeo.sys.user.entity.User;
import com.leeo.sys.user.service.UserService;

/**
 * 用户登录
 * 
 * @author Leeo
 *
 */
@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private ResourceService resourceService;
	@Autowired
    private RedisCache<String, Integer> redisCache;
	
	@Value(value = "${user.password.maxRetryCount}")
    private int maxRetryCount = 10;
	
	private String path = SysThemesEnum.BOOTSTRAP_STYLE.getIndexPath();

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

	@RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
	public String loginForm(Model model) {
//		Subject currentUser = SecurityUtils.getSubject();
//		if(null != currentUser){
//			return "redirect:/admin/index";
//		}
		model.addAttribute("user", new User());
		
		return path+"login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(/*@Valid*/ User user, /*BindingResult bindingResult,*/ RedirectAttributes redirectAttributes) {
//		if (bindingResult.hasErrors()) {
//			return "login";
//		}

		String username = user.getUsername();
		String password = user.getPassword();
		// 获取当前的Subject
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		Integer retryCount = null;
		String retryCountCache = username+"-retryCount";
		try {
//			if (null != currentUser){
//				currentUser.logout();
//			}
//	        // Log the user out and kill their session if possible.
//	        Session session = currentUser.getSession(false);
//	        if (session != null){
//	        	session.stop();
//	        }
			
			retryCount = null == redisCache.get(retryCountCache)? 0 : redisCache.get(retryCountCache);
			if (retryCount.intValue() >= maxRetryCount) {
                UserLogUtils.log(
                        username,
                        "passwordError",
                        "password error, retry limit exceed! password: {},max retry count {}",
                        password, maxRetryCount);
//                throw new UserPasswordRetryLimitExceedException(maxRetryCount);
                throw new LockedAccountException();
            }
			
			// 在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
			// 每个Realm都能在必要时对提交的AuthenticationTokens作出反应
			// 所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
	        if (!currentUser.isAuthenticated()){
		        logger.info("对用户[" + username + "]进行登录验证..验证开始");
		        token.setRememberMe(true);
		        currentUser.login(token);
				logger.info("对用户[" + username + "]进行登录验证..验证通过");
	
				redisCache.remove(retryCountCache);
				logger.info("用户[" + username + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
				Session session = currentUser.getSession(false);
				session.setAttribute("user", this.userService.findByLoginName(username));
	        }
			
			return "redirect:/admin/index";
		} catch (UnknownAccountException uae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
			redirectAttributes.addFlashAttribute("message", "未知账户");
			
			redisCache.put(retryCountCache, ++retryCount);
            UserLogUtils.log(
                    username,
                    "passwordError",
                    "password error! password: {} retry count: {}",
                    password, retryCount);
		} catch (IncorrectCredentialsException ice) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");
			redirectAttributes.addFlashAttribute("message", "密码不正确");
		

			redisCache.put(retryCountCache, ++retryCount);
            UserLogUtils.log(
                    username,
                    "passwordError",
                    "password error! password: {} retry count: {}",
                    password, retryCount);
		} catch (LockedAccountException lae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");
			redirectAttributes.addFlashAttribute("message", "账户已锁定");
		

			redisCache.put(retryCountCache, ++retryCount);
            UserLogUtils.log(
                    username,
                    "passwordError",
                    "password error! password: {} retry count: {}",
                    password, retryCount);
		} catch (ExcessiveAttemptsException eae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误次数过多");
			redirectAttributes.addFlashAttribute("message", "用户名或密码错误次数过多");
		

			redisCache.put(retryCountCache, ++retryCount);
            UserLogUtils.log(
                    username,
                    "passwordError",
                    "password error! password: {} retry count: {}",
                    password, retryCount);
		} catch (AuthenticationException ae) {
			// 通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");
			ae.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "用户名或密码不正确");
		

			redisCache.put(retryCountCache, ++retryCount);
            UserLogUtils.log(
                    username,
                    "passwordError",
                    "password error! password: {} retry count: {}",
                    password, retryCount);
		}
		token.clear();
		
		return "redirect:/login";
	}

//	/**
//	 * 退出系统
//	 * redirect会报错org.apache.shiro.session.UnknownSessionException: There is no session with id
//	 * @param redirectAttributes
//	 * @return
//	 */
//	@RequestMapping(value = "/logout", method = RequestMethod.GET)
//	public String logout(RedirectAttributes redirectAttributes) {
//		Subject currentUser = SecurityUtils.getSubject();    
//		currentUser.logout(); 
//		redirectAttributes.addFlashAttribute("message", "您已安全退出");
//		logger.info("用户【{}】退出系统.", currentUser.getPrincipal());
//		
//		return "redirect:/login";
//	}
	
	@RequestMapping("/admin/index")
	public String index(Model model) {

		return path+"index";
	}
	
	@RequestMapping(value="/admin/menu/{id}", method = RequestMethod.GET)
	@ResponseBody
	public List<Menu> menu(@PathVariable("id")User user) {
		return this.resourceService.findMenus(user);
	}
	
	@RequestMapping("/403")
	public String unauthorizedRole() {
		logger.info("------没有权限-------");
		return "403";
	}

	@RequestMapping(value = "/sessions", method = RequestMethod.GET)
    public Object sessions (HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", request.getSession().getId());
        map.put("message", request.getSession().getAttribute("map"));
        return map;
    }
}