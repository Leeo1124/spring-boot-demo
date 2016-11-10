package com.leeo.sys.user.web.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.leeo.common.entity.enums.SysThemesEnum;
import com.leeo.sys.user.entity.User;
import com.leeo.sys.user.service.UserService;

@Controller
@RequestMapping("/admin/sys/user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
	private UserService userService;
    
    private String path = SysThemesEnum.BOOTSTRAP_STYLE.getIndexPath();

	@GetMapping
	public ModelAndView list() {
		Iterable<User> users = this.userService.findAll();
		return new ModelAndView(path+"sys/user/user-list", "users", users);
	}
	
	@GetMapping("/query")
	@ResponseBody
	public Map<String, Object> query(HttpServletRequest request) {
		//数据起始位置
	    String startIndex = request.getParameter("startIndex");
	    logger.info("startIndex : " + startIndex);
	    //数据长度
	    String pageSize = request.getParameter("pageSize");
	    logger.info("pageSize : " + pageSize);
	    //获取客户端需要那一列排序
	    String orderColumn = request.getParameter("orderColumn");
	    logger.info("orderColumn : " + orderColumn);
	    //获取排序方式 默认为asc
	    String orderDir = request.getParameter("orderDir");
	    logger.info("orderDir : " + orderDir);
	    
	    List<User> users = this.userService.findAll();
	    logger.info("users : " + users);
	    Map<String, Object> result = new HashMap<>();
	    result.put("pageData", users);
	    result.put("total", users.size());
	    
		return result;
	}

	@GetMapping("{id}")
	public ModelAndView view(@PathVariable("id") User user) {
		return new ModelAndView(path+"sys/user/user-view", "user", user);
	}

	@GetMapping(params = "form")
	public String createForm(@ModelAttribute User user) {
		return path+"sys/user/user-form";
	}

	@PostMapping
	public ModelAndView create(@Valid User user, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView(path+"sys/user/form", "formErrors", result.getAllErrors());
		}
		user = this.userService.save(user);
		redirect.addFlashAttribute("globalMessage", "Successfully created a new user");
		return new ModelAndView("redirect:/admin/sys/user/{user.id}", "user.id", user.getId());
	}

	@RequestMapping("foo")
	public String foo() {
		throw new RuntimeException("Expected exception in controller");
	}

	@GetMapping(value = "delete/{id}")
	public ModelAndView delete(@PathVariable("id") Long id) {
		this.userService.delete(id);;
		Iterable<User> users = this.userService.findAll();
		return new ModelAndView("redirect:/admin/sys/user", "users", users);
	}

	@GetMapping(value = "modify/{id}")
	public ModelAndView modifyForm(@PathVariable("id") User user) {
		return new ModelAndView(path+"sys/user/user-form", "user", user);
	}

}