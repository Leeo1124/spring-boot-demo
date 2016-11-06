package com.leeo.sys.user.web.controller;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.leeo.sys.user.entity.User;
import com.leeo.sys.user.service.UserService;

@Controller
@RequestMapping("/admin/user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
	private UserService userService;

	@GetMapping
	public ModelAndView list() {
		Iterable<User> users = this.userService.findAll();
		return new ModelAndView("admin/user/list", "users", users);
	}

	@GetMapping("{id}")
	public ModelAndView view(@PathVariable("id") User user) {
		return new ModelAndView("admin/user/view", "user", user);
	}

	@GetMapping(params = "form")
	public String createForm(@ModelAttribute User user) {
		return "admin/user/form";
	}

	@PostMapping
	public ModelAndView create(@Valid User user, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/user/form", "formErrors", result.getAllErrors());
		}
		user = this.userService.save(user);
		redirect.addFlashAttribute("globalMessage", "Successfully created a new user");
		return new ModelAndView("redirect:/admin/user/{user.id}", "user.id", user.getId());
	}

	@RequestMapping("foo")
	public String foo() {
		throw new RuntimeException("Expected exception in controller");
	}

	@GetMapping(value = "delete/{id}")
	public ModelAndView delete(@PathVariable("id") Long id) {
		this.userService.delete(id);;
		Iterable<User> users = this.userService.findAll();
		return new ModelAndView("redirect:/admin/user", "users", users);
	}

	@GetMapping(value = "modify/{id}")
	public ModelAndView modifyForm(@PathVariable("id") User user) {
		return new ModelAndView("admin/user/form", "user", user);
	}

}