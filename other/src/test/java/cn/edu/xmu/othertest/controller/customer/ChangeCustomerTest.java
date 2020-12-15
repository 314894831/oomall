package cn.edu.xmu.othertest.controller.customer;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.other.OtherApplication;
import cn.edu.xmu.other.mapper.CustomerPoMapper;
import cn.edu.xmu.other.model.bo.Customer;
import cn.edu.xmu.other.model.po.CustomerPo;
import cn.edu.xmu.other.model.vo.CustomerVo.LoginVo;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * 用户更改相关测试类
 * @author Shuhao Peng
 * @date 2020/12/05 10:49
 */
@SpringBootTest(classes = OtherApplication.class)
@AutoConfigureMockMvc
@Transactional
public class ChangeCustomerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    private CustomerPoMapper customerPoMapper;

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
     * 测试更新用户资料
     * @throws Exception Assert 或 HTTP 错误
     * @author Shuhao Peng
     * Created at 2020/12/5 10:52
     */
    @Test
    public void modifyCustomerNoExceptions() throws Exception {
        //String token = this.login("The Hao","123456");
        String token = this.login("The Hao", "123456");

        String contentJson = "{\n" +
                "    \"realName\": \"Peng Shuhao\",\n" +
                "    \"gender\": \"1\",\n" +
                "    \"birthday\": \"2000-09-02\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/user/users")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关数据是否有真的改变
        CustomerPo updatedPo = customerPoMapper.selectByPrimaryKey(17329L);
        Assert.state(updatedPo.getRealName().equals("Peng Shuhao"), "用户真实姓名不相等！");
        Assert.state(updatedPo.getGender().equals((byte)1), "用户性别不相等！");
        Assert.state(updatedPo.getBirthday().toString().equals("2000-09-02"), "用户生日不相等！");
    }

    /**
     * 测试更新用户资料 (未登入无Token)
     * @throws Exception Assert 或 HTTP 错误
     * @author Shuhao Peng
     * Created at 2020/12/5 11:25
     */
    @Test
    public void modifyCustomerNotLoggedIn() throws Exception {

        String contentJson = "{\n" +
                "    \"realName\": \"Peng Shuhao\",\n" +
                "    \"gender\": \"1\",\n" +
                "    \"birthday\": \"2000-09-02\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/user/users")
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isUnauthorized()) // 未登入 401 错误
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":704,\"errmsg\":\"需要先登录\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关数据是否有真的改变
        CustomerPo updatedPo = customerPoMapper.selectByPrimaryKey(17329L);
        Assert.state(updatedPo.getRealName().equals("Shuhao Peng"), "用户真实姓名在没登录的情况下被修改了！");
        Assert.state(updatedPo.getGender().equals((byte)0), "用户性别在没登录的情况下被修改了！");
        Assert.state(updatedPo.getBirthday().toString().equals("2020-12-05"), "用户生日在没登录的情况下被修改了！");
    }

    public String createTestAdminuserToken(Long userId, Long departId, int expireTime){
        String token = new JwtHelper().createToken(userId, departId, expireTime);

        return token;
    }

    /**
     * 测试封禁用户
     * @throws Exception Assert 或 HTTP 错误
     * @author Shuhao Peng
     * Created at 2020/12/5 15:22
     */
    @Test
    public void forbidCustomer1() throws Exception {
        String token = this.createTestAdminuserToken(1l, 0l,3600);

        String responseString = this.mvc.perform(
                put("/user/users/10/ban")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关用户是否有真的被封禁
        CustomerPo updatedPo = customerPoMapper.selectByPrimaryKey(10L);
        Assert.state(updatedPo.getState() == (byte) Customer.State.FORBID.getCode().intValue(), "这个用户并未被封禁！");
    }

    /**
     * 测试封禁用户(ID不存在)
     * @author Shuhao Peng
     * @date Created in 2020/12/5 16:03
     **/
    @Test
    public void forbidCustomer2() throws Exception {
        //ID不存在的请求
        String token = this.createTestAdminuserToken(1l, 0l,3600);

        String content = new String("{\n" +
                "  \"errno\": 504,\n" +
                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}");

        String responseString = this.mvc.perform(
                put("/user/users/999999/ban")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseString, content, true);
    }

    /**
     * 测试解禁用户
     * @throws Exception Assert 或 HTTP 错误
     * @author Shuhao Peng
     * Created at 2020/12/5 16:23
     */
    @Test
    public void releaseCustomer1() throws Exception {
        String token = this.createTestAdminuserToken(1l, 0l,3600);

        String responseString = this.mvc.perform(
                put("/user/users/17328/release")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关用户是否有真的被解封
        CustomerPo updatedPo = customerPoMapper.selectByPrimaryKey(17328L);
        Assert.state(updatedPo.getState() == (byte) Customer.State.NORM.getCode().intValue(), "这个用户并未被解封！");
    }

    /**
     * 测试解禁用户(ID不存在)
     * @author Shuhao Peng
     * @date Created in 2020/12/5 16:23
     **/
    @Test
    public void releaseCustomer2() throws Exception {
        //ID不存在的请求
        String token = this.createTestAdminuserToken(1l, 0l,3600);

        String content = new String("{\n" +
                "  \"errno\": 504,\n" +
                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}");

        String responseString = this.mvc.perform(
                put("/user/users/999999/release")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseString, content, true);
    }

}
