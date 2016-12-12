package com.leeo.swagger;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leeo.sys.user.entity.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "SwaggerController", description = "api测试")
@RestController
@RequestMapping("swagger")
public class SwaggerController {


    /**
     * 简单接口描述
     *
     * @param userName
     * @return
     */
    @RequestMapping(value = "/demo1", method = RequestMethod.POST)
    @ApiOperation(value = "测试接口1", notes = "简单接口描述 userName必填", code = 200, produces = "application/json")
    public ModelMap getDemo(@RequestParam("userName") String userName) {
        ModelMap map = new ModelMap();
        map.addAttribute("userName", userName);
        return map;
    }

    /**
     * 参数描述
     *
     * @param userName
     * @return
     */
    @RequestMapping(value = "/demo2/{userName}", method = RequestMethod.POST)
    @ApiOperation(value = "测试接口2", notes = "参数描述", code = 200, produces = "application/json")
    public ModelMap getDemo2(@ApiParam(name = "userName", value = "编号", required = true) @PathVariable("userName") String userName) {
        ModelMap map = new ModelMap();
        map.addAttribute("userName", userName);
        return map;
    }

    /**
     * 接受对象
     *
     * @return
     */
    @RequestMapping(value = "/demo3", method = RequestMethod.POST)
    @ApiOperation(value = "测试接口3", notes = "接受对象", code = 200, produces = "application/json")
    public ModelMap getDemoa(@RequestBody User user) {
        ModelMap map = new ModelMap();
        map.addAttribute("result", user);
        return map;
    }

    @ApiIgnore //使用这个注解忽略这个接口
    @RequestMapping(value = "/demo4", method = RequestMethod.POST)
    public ModelMap getDemob(@RequestParam String content) {
        ModelMap map = new ModelMap();
        map.addAttribute("result", new java.util.Date());
        return map;
    }
}