package com.yh.cn.service;

import com.yh.cn.entity.SysUser;

import java.util.List;

public interface SysUserService {

    List<SysUser> selectAll();

    SysUser selectUserByUsrName(String username);

    List<String> selectQuan(int id);

}
