package com.leeo.sys.resource.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.leeo.sys.resource.entity.Resource;
import com.leeo.sys.resource.service.ResourceService;
import com.leeo.sys.user.web.controller.UserController;

@Controller
@RequestMapping(value = "/admin/resource")
public class ResourceController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
	private ResourceService resourceService;

	@GetMapping
	public ModelAndView list() {
		Iterable<Resource> resources = this.resourceService.findAll();
		return new ModelAndView("admin/user/list", "resources", resources);
	}
}
