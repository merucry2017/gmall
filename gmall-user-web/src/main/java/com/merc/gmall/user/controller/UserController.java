package com.merc.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.merc.gmall.annotations.LoginRequired;
import com.merc.gmall.bean.UmsMember;
import com.merc.gmall.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class UserController {

    @Reference
    UserService userService;

    @ApiOperation(value = "返回首页",notes = "author:hxq")
    @GetMapping("/")
    @LoginRequired(loginSuccess = false)
    public void getIndex(HttpServletResponse resp) throws IOException {
        resp.sendRedirect("http://localhost:8083/index");
    }

    @ApiOperation(value = "获取所有用户",notes = "author:hxq")
    @ApiImplicitParam
    @GetMapping("/getAllUser")
    public List<UmsMember> getAllUser(){
        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }
}
