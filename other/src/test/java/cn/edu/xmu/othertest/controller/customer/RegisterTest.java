package cn.edu.xmu.othertest.controller.customer;

import cn.edu.xmu.other.OtherApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 注册验证测试类 该类测试验证方法是否正确
 * @author Shuhao Peng
 * @date 2020/11/30 16:35
 */
@SpringBootTest(classes = OtherApplication.class)
@AutoConfigureMockMvc
@Transactional
public class RegisterTest {
    @Autowired
    MockMvc mvc;

    /**
     * 获取所有状态
     * @throws Exception
     */
    @Test
    public void getAllState() throws Exception {
        String responseString=this.mvc.perform(get("/user/users/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{ \"errno\": 0, \"data\": [ { \"name\": \"后台用户\", \"code\": 0 }, { \"name\": \"正常用户\", \"code\": 4 }, { \"name\": \"禁止用户\", \"code\": 6 } ], \"errmsg\": \"成功\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 正常注册
     * @throws Exception
     */
    @Test
    public void register1() throws Exception {
        String requireJson="{\n    \"mobile\": \"17712349876\",\n    \"email\": \"8676151@qq.com\",\n    \"username\": \"The Hao\",\n    \"password\": \"Shu60441!@692\",\n    \"realname\": \"Shuhao Peng\",\n    \"gender\": \"0\",\n    \"birthday\": \"2000-01-01\"\n}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{ \"errno\": 0, \"data\": { \"id\": 17333, \"userName\": \"The Hao\", \"mobile\": \"17712349876\", \"email\": \"8676151@qq.com\", \"realName\": \"Shuhao Peng\", \"gender\": \"0\", \"birthday\": \"2000-01-01\"}, \"errmsg\": \"成功\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 手机号不正确
     * @throws Exception
     */
    @Test
    public void register2() throws Exception {
        String requireJson="{\n    \"mobile\": \"?17712349876\",\n    \"email\": \"8676151@qq.com\",\n    \"username\": \"The Hao\",\n    \"password\": \"Shu60441!@692\",\n    \"realname\": \"Shuhao Peng\",\n    \"gender\": \"0\",\n    \"birthday\": \"2000-01-01\"\n}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{ \"errno\": 503, \"errmsg\": \"手机号格式不正确;\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * email不正确
     * @throws Exception
     */
    @Test
    public void register3() throws Exception {
        String requireJson="{\n    \"mobile\": \"17712349876\",\n    \"email\": \"8676151qq.com\",\n    \"username\": \"The Hao\",\n    \"password\": \"Shu60441!@692\",\n    \"realname\": \"Shuhao Peng\",\n    \"gender\": \"0\",\n    \"birthday\": \"2000-01-01\"\n}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{ \"errno\": 503, \"errmsg\": \"Email格式不正确;\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

}

