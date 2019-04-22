package com.yh.cn.controller;

import com.yh.cn.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class UsrController {

    @Autowired
    private SysUserService sysUserService;


    @RequestMapping(value = "/selectAll")
    public Object getMap(HttpServletResponse response) {
        //防止跨域乱码
        response.setContentType("/application/json");
        response.setCharacterEncoding("utf-8");
        return sysUserService.selectAll();
    }


}