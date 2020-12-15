package cn.edu.xmu.othertest.controller.customer;

import cn.edu.xmu.other.OtherApplication;
import cn.edu.xmu.other.mapper.CustomerPoMapper;
import cn.edu.xmu.other.model.po.CustomerPo;
import cn.edu.xmu.other.model.po.CustomerPoExample;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户密码重置和更改测试类
 * @author Shuhao Peng
 * @date 2020/12/7 14:32
 */
@SpringBootTest(classes = OtherApplication.class)
@AutoConfigureMockMvc
@Transactional
public class PasswordTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    private CustomerPoMapper customerPoMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     *  重置密码（成功）
     */
    @Test
    public void resetPassword1() throws Exception{

        //插入一条用于测试的记录(自己的真实邮箱)
        CustomerPo customerPo = new CustomerPo();
        customerPo.setEmail("867615193@qq.com");
        customerPo.setMobile("17779589348");
        customerPo.setUserName("test");
        customerPo.setPassword("123456");
        customerPo.setGmtCreate(LocalDateTime.now());
        customerPoMapper.insertSelective(customerPo);

        //发reset请求
        String requireJson = "{\"userName\":\"test\",\"email\":\"867615193@qq.com\"}";

        String responseString = this.mvc.perform(put("/user/users/password/reset").contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        //删除用于测试的数据库记录
        CustomerPoExample userPoExample1 = new CustomerPoExample();
        CustomerPoExample.Criteria criteria = userPoExample1.createCriteria();
        criteria.andUserNameEqualTo("test");
        List<CustomerPo> userPo1 = customerPoMapper.selectByExample(userPoExample1);
        customerPoMapper.deleteByPrimaryKey(userPo1.get(0).getId());
        //删除用于测试的Redis记录
        redisTemplate.delete("ip_169.254.214.85");
    }

    /**
     *  重置密码（用户名不存在）
     */
    @Test
    public void resetPassword2() throws Exception{

        //插入一条用于测试的记录(自己的真实邮箱)
        CustomerPo customerPo = new CustomerPo();
        customerPo.setEmail("867615193@qq.com");
        customerPo.setMobile("17779589348");
        customerPo.setUserName("test");
        customerPo.setPassword("123456");
        customerPo.setGmtCreate(LocalDateTime.now());
        customerPoMapper.insertSelective(customerPo);

        //发reset请求
        String requireJson = "{\"userName\":\"test1\",\"email\":\"867615193@qq.com\"}";

        String responseString = this.mvc.perform(put("/user/users/password/reset").contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 700, \"errmsg\": \"用户名不存在或者密码错误\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        //删除用于测试的数据库记录
        CustomerPoExample userPoExample1 = new CustomerPoExample();
        CustomerPoExample.Criteria criteria = userPoExample1.createCriteria();
        criteria.andUserNameEqualTo("test");
        List<CustomerPo> userPo1 = customerPoMapper.selectByExample(userPoExample1);
        customerPoMapper.deleteByPrimaryKey(userPo1.get(0).getId());

        redisTemplate.delete("ip_169.254.214.85");
    }

    /**
     *  重置密码（与系统预留的邮箱不一致）
     */
    @Test
    public void resetPassword3() throws Exception{
        //插入一条用于测试的记录(自己的真实邮箱)
        CustomerPo customerPo = new CustomerPo();
        customerPo.setEmail("867615193@qq.com");
        customerPo.setMobile("17779589348");
        customerPo.setUserName("test");
        customerPo.setPassword("123456");
        customerPo.setGmtCreate(LocalDateTime.now());
        customerPoMapper.insertSelective(customerPo);

        //发reset请求
        String requireJson = "{\"userName\":\"test\",\"email\":\"888@qq.com\"}";

        String responseString = this.mvc.perform(put("/user/users/password/reset").contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 745, \"errmsg\": \"与系统预留的邮箱不一致\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        //删除用于测试的数据库记录
        CustomerPoExample userPoExample1 = new CustomerPoExample();
        CustomerPoExample.Criteria criteria = userPoExample1.createCriteria();
        criteria.andUserNameEqualTo("test");
        List<CustomerPo> userPo1 = customerPoMapper.selectByExample(userPoExample1);
        customerPoMapper.deleteByPrimaryKey(userPo1.get(0).getId());

        redisTemplate.delete("ip_169.254.214.85");
        //redisTemplate.delete("ip_169.254.27.122");
    }

    /**
     * 修改密码：成功
     */
    @Test
    public void changePassword1() throws Exception{

        //获取一条记录
        CustomerPo userPo = customerPoMapper.selectByPrimaryKey(1L);

        //向redis插入一条记录
        redisTemplate.opsForValue().set("cp_666666","1");
        redisTemplate.expire("cp_666666", 60*1000, TimeUnit.MILLISECONDS);

        //发modify请求
        String requireJson = "{\"captcha\":\"666666\",\"newPassword\":\"Shu@!6044\"}";

        String responseString = this.mvc.perform(put("/user/users/password").contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse1 = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse1, responseString, true);

        // 测试有关数据是否有真的改变
        CustomerPo updatedPo = customerPoMapper.selectByPrimaryKey(1L);
        Assert.state(updatedPo.getPassword().equals("Shu@!6044"), "密码未修改！");

    }

    /**
     * 修改密码：密码格式不正确
     */
    @Test
    public void changePassword2() throws Exception{

        //获取一条记录
        CustomerPo userPo = customerPoMapper.selectByPrimaryKey(1L);

        //向redis插入一条记录
        redisTemplate.opsForValue().set("cp_666666","1");
        redisTemplate.expire("cp_666666", 60*1000, TimeUnit.MILLISECONDS);

        //发modify请求
        String requireJson = "{\"captcha\":\"666666\",\"newPassword\":\"shu123456\"}";

        String responseString = this.mvc.perform(put("/user/users/password").contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse1 = "{\"errno\": 503, \"errmsg\": \"密码格式不正确，请包含大小写字母数字及特殊符号;\"}";
        JSONAssert.assertEquals(expectedResponse1, responseString, true);

    }

    /**
     * 修改密码：不能与旧密码相同
     */
    @Test
    public void changePassword3() throws Exception{

        //获取一条记录
        CustomerPo userPo = customerPoMapper.selectByPrimaryKey(1L);

        //向redis插入一条记录
        redisTemplate.opsForValue().set("cp_666666","1");
        redisTemplate.expire("cp_666666", 60*1000, TimeUnit.MILLISECONDS);

        //发modify请求
        String requireJson = "{\"captcha\":\"666666\",\"newPassword\":\"Shu@!123456\"}";

        String responseString = this.mvc.perform(put("/user/users/password").contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse1 = "{\"errno\": 741, \"errmsg\": \"不能与旧密码相同\"}";
        JSONAssert.assertEquals(expectedResponse1, responseString, true);

    }

    @Test
    /**
     * 修改密码：验证码错误或无效
     */
    public void changePassword4() throws Exception{

        String requireJson = "{\"captcha\":\"222222\",\"newPassword\":\"Shu@!123456\"}";

        String responseString = this.mvc.perform(put("/user/users/password").contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 700, \"errmsg\": \"用户名不存在或者密码错误\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

}
