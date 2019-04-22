package com.yh.cn.dao;

import com.yh.cn.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysUserDao {

    List<SysUser> selectAll();

    SysUser selectUserByUsrName(String username);

    List<String> selectQuan(int id);
}
