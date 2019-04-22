package main.test;

import com.yh.cn.Application;
import com.yh.cn.entity.SysUser;
import com.yh.cn.service.SysUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= Application.class)
public class UsrServiceTest {

	@Autowired
	private SysUserService sysUserService;

    @Test
	public void test(){
        List<SysUser> sysUsers = sysUserService.selectAll();
        System.err.println(sysUsers);
    }
}
