package cn.edu.xmu.customer.controller;

import cn.edu.xmu.customer.CustomerServiceApplication;
import cn.edu.xmu.customer.model.vo.LoginVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 用户信息相关测试类
 * @author Shuhao Peng
 * @date 2020/12/04 10:32
 */
@SpringBootTest(classes = CustomerServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
public class GetCustomerTest {
    @Autowired
    MockMvc mvc;

    private String content;

    private String login(String userName, String password) throws Exception{
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);

        String requireJson = JacksonUtil.toJson(vo);
        String response = this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andReturn().getResponse().getContentAsString();
        return  JacksonUtil.parseString(response, "data");

    }

    /**
     * 获取用户自己信息
     * @throws Exception
     */
    @Test
    public void findCustomerSelf() throws Exception {

        String token = this.login("75935640349", "123456");

        try
        {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findCustomerSelf.json")),"utf-8");
        } catch (Exception e) {e.printStackTrace();}

        String responseString = this.mvc.perform(get("/user/users").header("authorization", token))
                .andExpect(status().isOk())
                //.andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        //String expectedResponse="{ \"errno\": 0, \"data\": { \"id\": 10, \"userName\": \"75935640349\", \"mobile\": \"13959288888\", \"realName\": \"47620521437\", \"gender\": \"0\"}, \"errmsg\": \"成功\" }";
        JSONAssert.assertEquals(content,responseString,false);
    }

    public String createTestAdminuserToken(Long userId, Long departId, int expireTime){
        String token = new JwtHelper().createToken(userId, departId, expireTime);

        return token;
    }

    /**
     * 管理员获取所有用户列表(全部)
     * @author Shuhao Peng
     * @date Created in 2020/12/5 13:32
     **/
    @Test
    public void findAllCustomers1() throws Exception {

        String token = this.createTestAdminuserToken(1l, 0l,3600);

        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findAllCustomer.json")),"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String responseSuccessString = this.mvc.perform(get("/user/users/all?userName=&email=&mobile=&page=1&pagesize=3").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * 管理员获取所有用户列表(用户名不存在)
     * @author Shuhao Peng
     * @date Created in 2020/12/5 13:32
     **/
    @Test
    public void findAllCustomers2() throws Exception {

        String token = this.createTestAdminuserToken(1l, 0l,3600);

        //参数正确的请求
        content = new String("{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":3,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}");


        String responseSuccessString = this.mvc.perform(get("/user/users/all?userName=ppp&email=&mobile=&page=1&pagesize=3").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * 管理员获取所有用户列表(错误的参数请求)
     * @author Shuhao Peng
     * @date Created in 2020/12/5 14:2
     **/
    @Test
    public void findAllCustomers3() throws Exception {

        String token = this.createTestAdminuserToken(1l, 0l,3600);

        //参数正确的请求
        content = new String("{\n" +
                "  \"errno\": 503,\n" +
                "  \"errmsg\": \"字段不合法\"\n" +
                "}");

        String responseSuccessString = this.mvc.perform(get("/user/users/all?userName=&email=&mobile=&page=-1&pagesize=3").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * 管理员根据id查询用户信息
     * @author Shuhao Peng
     * @date Created in 2020/12/5 14:49
     **/
    @Test
    public void findCustomerById1() throws Exception {
        //ID存在的请求

        String token = this.createTestAdminuserToken(1l, 0l,3600);
        //参数正确的请求
        content = new String("{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 10,\n" +
                "    \"userName\": \"75935640349\",\n" +
                "    \"realName\": \"47620521437\",\n" +
                "    \"mobile\": \"13959288888\",\n" +
                "    \"email\": null,\n" +
                "    \"gender\": \"0\",\n" +
                "    \"birthday\":null,\n" +
                "    \"gmtCreate\": \"2020-11-29T21:10:28\",\n" +
                "    \"gmtModified\": \"2020-11-29T21:10:28\"\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}");

        String responseSuccessString = this.mvc.perform(get("/user/users/10").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * 管理员根据id查询用户信息(ID不存在)
     * @author Shuhao Peng
     * @date Created in 2020/12/5 14:49
     **/
    @Test
    public void findCustomerById2() throws Exception {
        //ID不存在的请求

        String token = this.createTestAdminuserToken(1l, 0l,3600);

        content = new String("{\n" +
                "  \"errno\": 504,\n" +
                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}");

        String responseFailString = this.mvc.perform(get("/user/users/99999").header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseFailString, content, true);
    }

}
