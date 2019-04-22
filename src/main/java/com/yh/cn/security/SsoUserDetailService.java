package com.yh.cn.security;

import com.yh.cn.entity.SysUser;
import com.yh.cn.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SsoUserDetailService implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser=sysUserService.selectUserByUsrName(username);
        if (sysUser!=null) {
            JUser user = new JUser();
            //查询用户权限
            List<String> perms =sysUserService.selectQuan(sysUser.getId());
            Set<SimpleGrantedAuthority> set = new HashSet<>();
            for (String perm : perms) {
                set.add(new SimpleGrantedAuthority(perm));
            }
            BeanUtils.copyProperties(sysUser, user);
            user.setPermissions(set);
            return user;
        }
        throw new UsernameNotFoundException("用户名不存在！");

    }

}
