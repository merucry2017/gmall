package com.merc.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.merc.gmall.bean.UmsMember;
import com.merc.gmall.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Reference
    UserService userService;

    @GetMapping("/getAllUser")
    public List<UmsMember> getAllUser(){
        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }
}
