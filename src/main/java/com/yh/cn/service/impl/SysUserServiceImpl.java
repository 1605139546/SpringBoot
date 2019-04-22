package com.yh.cn.service.impl;

import com.yh.cn.dao.SysUserDao;
import com.yh.cn.entity.SysUser;
import com.yh.cn.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserServiceImpl  implements SysUserService {

    private static final Logger logger = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserDao sysUserDao;


    @Override
    public List<SysUser> selectAll() {
        return sysUserDao.selectAll();
    }

    @Override
    public SysUser selectUserByUsrName(String username) {
        return sysUserDao.selectUserByUsrName(username);
    }

    @Override
    public List<String> selectQuan(int id) {
        return sysUserDao.selectQuan(id);
    }


}
