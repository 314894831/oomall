package cn.edu.xmu.othertest.controller.customer;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.other.OtherApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 登录验证测试类 该类测试验证方法是否正确
 * @author Shuhao Peng
 * @date 2020/12/3 10:47
 */
@SpringBootTest(classes = OtherApplication.class)
@AutoConfigureMockMvc
@Transactional
public class LoginTest {
    @Autowired
    MockMvc mvc;

    /**
     * 正常登录
     * @author Shuhao Peng
     * @date Created in 2020/12/3 10:50
     */
    @Test
    public void login1() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        requireJson = "{\"userName\":\"75935640349\",\"password\":\"123456\"}";
        res = this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andExpect(jsonPath("$.data").isString())
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        //System.out.println(JacksonUtil.parseString(responseString, "data"));
    }

    /**
     * 用户名错误的用户登录
     * @author Shuhao Peng
     * @date Created in 2020/12/3 11：03
     */
    @Test
    public void login2() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        requireJson = "{\"userName\":\"NotExist\",\"password\":\"123456\"}";
        res = this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.AUTH_INVALID_ACCOUNT.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andReturn().getResponse().getContentAsString();

    }

    /**
     * 密码错误的用户登录
     * @author Shuhao Peng
     * @date Created in 2020/12/3 11：49
     */
    @Test
    public void login3() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        requireJson = "{\"userName\":\"75935640349\",\"password\":\"666666\"}";
        res = this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.AUTH_INVALID_ACCOUNT.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andReturn().getResponse().getContentAsString();

    }

    /**
     * 没有输入用户名的用户登录
     * @author Shuhao Peng
     * @date Created in 2020/12/3 11：51
     */
    @Test
    public void login4() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        requireJson = "{\"password\":\"123456\"}";
        res = this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.FIELD_NOTVALID.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andReturn().getResponse().getContentAsString();

    }

    /**
     * 没有输入密码的用户登录
     * @author Shuhao Peng
     * @date Created in 2020/12/3 11：53
     */
    @Test
    public void login5() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        requireJson = "{\"userName\":\"75935640349\",\"password\":\"\"}";
        res = this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.FIELD_NOTVALID.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andReturn().getResponse().getContentAsString();

    }

    /**
     * 用户重复登录
     * @author Shuhao Peng
     * @date Created in 2020/12/3 11：57
     */
    @Test
    public void login6() throws Exception {
        String requireJson = null;
        String response = null;
        ResultActions res = null;

        requireJson = "{\"userName\":\"75935640349\",\"password\":\"123456\"}";
        res = this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        response = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andExpect(jsonPath("$.data").isString())
                .andReturn().getResponse().getContentAsString();

        res = this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        String response1 = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andReturn().getResponse().getContentAsString();

        String jwt = JacksonUtil.parseString(response, "data");
        String jwt1 = JacksonUtil.parseString(response1, "data");
        assertNotEquals(jwt, jwt1);
    }

    /**
     * 当前状态不可登录的用户登录
     * @author Shuhao Peng
     * @date Created in 2020/12/3 11：57
     */
    @Test
    public void login7() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;
        requireJson = "{\"userName\":\"68458128598\",\"password\":\"123456\"}";
        res = this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.AUTH_USER_FORBIDDEN.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        //endregion
    }

    /**
     * 用户正常登出
     * @author Shuhao Peng
     * @date Created in 2020/12/5 12：49
     */
    @Test
    public void logout() throws  Exception{
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        requireJson = "{\"userName\":\"75935640349\",\"password\":\"123456\"}";
        res = this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andExpect(jsonPath("$.data").isString())
                .andReturn().getResponse().getContentAsString();
        String json = JacksonUtil.parseString(responseString,"data");

        //region 用户正常登出
        res = this.mvc.perform(get("/user/users/logout")
                .contentType("application/json;charset=UTF-8")
                .header("authorization",json));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andReturn().getResponse().getContentAsString();
        //endregion
    }
}
