package com.yh.cn.controller;

import com.yh.cn.security.JUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class WelComeController {



    @Autowired
    ConsumerTokenServices consumerTokenServices;

    @RequestMapping("/login")
    public String gotoLogin() {
        return "login";
    }

    @RequestMapping(value = "/index")
    public String gotoIndex(HttpServletResponse response) {
        System.out.println("<<<<<<<<<<<<<<<<<<<<"+getUser());
        return "index";
    }
    @RequestMapping(value = "/success")
    public String gotoSuccess() {
        return "success";
    }

    public JUser getUser() { //为了session从获取用户信息,可以配置如下
        JUser user = new JUser();
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        if (auth.getPrincipal() instanceof UserDetails) user = (JUser) auth.getPrincipal();
        return user;
    }

    @RequestMapping("/oauth/exit")
    public void exit(HttpServletRequest request, HttpServletResponse response) {
//        consumerTokenServices.revokeToken(access_token);
        new SecurityContextLogoutHandler().logout(request, null, null);
        try {
            System.out.println(request.getHeader("referer"));
            response.sendRedirect(request.getHeader("referer"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

