package com.leeo.sys.user.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.leeo.common.entity.enums.BooleanEnum;
import com.leeo.common.entity.enums.SysThemesEnum;
import com.leeo.common.entity.search.SearchOperator;
import com.leeo.common.entity.search.Searchable;
import com.leeo.common.web.bind.annotation.PageableDefaults;
import com.leeo.common.web.bind.annotation.SearchableDefaults;
import com.leeo.sys.user.entity.User;
import com.leeo.sys.user.entity.UserStatus;
import com.leeo.sys.user.service.UserService;

@Controller
@RequestMapping("/admin/sys/user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
	private UserService userService;
    
    private String path = SysThemesEnum.BOOTSTRAP_STYLE.getIndexPath();
    
    public void setCommonData(Model model) {
        model.addAttribute("status", UserStatus.values());
        model.addAttribute("admin", BooleanEnum.values());
    }

	@GetMapping
	public String list(Model model) {
		setCommonData(model);
		return path+"sys/user/user-list";
	}
	
	@GetMapping("/query")
	@ResponseBody
	public Map<String, Object> query(Model model,
			HttpServletRequest request,
			@RequestParam(value = "startIndex", defaultValue = "1") Integer startIndex,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "orderColumn", defaultValue = "id") String orderColumn,
			@RequestParam(value = "orderDir", defaultValue = "asc") String orderDir) {
		//数据起始位置
	    logger.info("startIndex : " + startIndex);
	    //数据长度
	    logger.info("pageSize : " + pageSize);
	    //获取客户端需要那一列排序
	    logger.info("orderColumn : " + orderColumn);
	    //获取排序方式 默认为asc
	    logger.info("orderDir : " + orderDir);
	    
	    Searchable searchable = Searchable.newSearchable().addSearchFilter("deleted", SearchOperator.eq, false);
	    searchable.setPage(startIndex, pageSize);
	    if(StringUtils.isNotBlank(orderColumn) && StringUtils.isNotBlank(orderDir)){
	    	Direction sort = "asc".equals(orderDir)? Sort.Direction.ASC : Sort.Direction.DESC;
	    	searchable.addSort(new Sort(sort, orderColumn));
	    }
	    
	    Page<User> users = this.userService.findAll(searchable);
	    logger.info("users : " + users.getContent());
	    Map<String, Object> result = new HashMap<>();
	    result.put("pageData", users.getContent());
	    result.put("total", users.getTotalElements());
	    
		return result;
	}
	
	@GetMapping("/query2")
	@PageableDefaults(sort = "id=desc")
    @SearchableDefaults(value = "deleted_eq=0")
	@ResponseBody
	public Map<String, Object> query2(Model model,Searchable searchable) {
	    
	    Page<User> users = this.userService.findAll(searchable);
	    logger.info("users : " + users.getContent());
	    Map<String, Object> result = new HashMap<>();
	    result.put("pageData", users.getContent());
	    result.put("total", users.getTotalElements());
	    
		return result;
	}
	
	@GetMapping("/query3")
	@ResponseBody
	public Map<String, Object> query3(Model model,
			HttpServletRequest request,
			@PageableDefault(size = 10) Pageable pageable) {
	    
	    Page<User> users = this.userService.findByExample(pageable);
	    logger.info("users : " + users.getContent());
	    Map<String, Object> result = new HashMap<>();
	    result.put("pageData", users.getContent());
	    result.put("total", users.getTotalElements());
	    
		return result;
	}

	@GetMapping("{id}/{operation}")
	public ModelAndView view(@PathVariable("id") User user, @PathVariable("operation")String operation) {
		return new ModelAndView(path+"sys/user/user-"+operation, "user", user);
	}

	@GetMapping("add")
	public String createForm(@ModelAttribute User user) {
		return path+"sys/user/user-add";
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

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
}