package cn.edu.xmu.othertest.controller;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.other.OtherApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OtherApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AddressControllerTest
{
    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(AddressControllerTest.class);

    private final String creatTestToken(Long userId, Long departId, int expireTime)
    {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    /**
     * 正常添加地址
     * @throws Exception
     */
    @Test
    public void addAddressNoExceptions() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String contentJson = "{\"regionId\":\"3\",\"detail\":\"这是一个详细地址描述\",\"consignee\":\"张三\",\"mobile\":\"1360979565\"}";
        String responseString = this.mvc.perform(
                post("/addresses")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"regionId\":3,\"detail\":\"这是一个详细地址描述\",\"consignee\":\"张三\",\"mobile\":\"1360979565\",\"beDefault\":0,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 电话号码不符合规范
     * @throws Exception
     */
    @Test
    public void addAddressWithExceptions() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String contentJson = "{\"regionId\":\"3\",\"detail\":\"这是一个详细地址描述\",\"consignee\":\"张三\",\"mobile\":\"136097 565\"}";
        String responseString = this.mvc.perform(
                post("/addresses")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().is(400))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":503,\"errmsg\":\"手机号格式不正确;\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 添加的用户地址簿已满
     * @throws Exception
     */
    @Test
    public void addAddressWithExceptions1() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);

        String contentJson = "{\"regionId\":\"3\",\"detail\":\"这是一个详细地址描述\",\"consignee\":\"李四\",\"mobile\":\"1360978565\"}";
        String responseString = this.mvc.perform(
                post("/addresses")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":601,\"errmsg\":\"达到地址簿上限\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 正常设置默认地址
     * @throws Exception
     */
    @Test
    public void setDefaultAddressNoExceptions() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/addresses/12/default")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 要设置的地址id不存在
     * @throws Exception
     */
    @Test
    public void setDefaultAddressWithExceptions() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/addresses/1000/default")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 修改地址信息，正常情况
     */
    @Test
    public void updateAddressNoExceptions() throws Exception
    {

        String token = creatTestToken(2L, 0L, 100);
        String contentJson = "{\"regionId\":\"3\",\"detail\":\"这是一个详细地址描述\",\"consignee\":\"王五\",\"mobile\":\"1360978565\"}";
        String responseString = this.mvc.perform(
                put("/addresses/10")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 修改其他人的地址
     */
    @Test
    public void updateAddressWithExceptions() throws Exception
    {

        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"regionId\":\"3\",\"detail\":\"这是一个详细地址描述\",\"consignee\":\"王五\",\"mobile\":\"1360978565\"}";
        String responseString = this.mvc.perform(
                put("/addresses/10")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 删除地址，正常情况
     */
    @Test
    public void deleteAddressNoExceptions() throws Exception
    {

        String token = creatTestToken(2L, 0L, 100);
        String responseString = this.mvc.perform(
                delete("/addresses/21")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 删除地址，删除的id不存在
     */
    @Test
    public void deleteAddressWithExceptions() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String responseString = this.mvc.perform(
                delete("/addresses/1000")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 正常获取买家收货地址
     */
    @Test
    public void getAllAddressesNoExceptions() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String responseString = this.mvc.perform(
                get("/addresses?page=1&pagesize=10")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":20,\"pages\":2,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":3,\"regionId\":3,\"detail\":\"李四的详细地址\",\"consignee\":\"李四\",\"mobile\":\"12159842368\",\"beDefault\":0,\"gmtCreate\":\"2020-12-01T15:27:20\",\"gmtModified\":null},{\"id\":4,\"regionId\":3,\"detail\":\"这是一个详细地址描述\",\"consignee\":\"王五\",\"mobile\":\"1360978565\",\"beDefault\":0,\"gmtCreate\":\"2020-12-01T15:27:48\",\"gmtModified\":\"2020-12-02T12:03:04\"},{\"id\":5,\"regionId\":3,\"detail\":\"李四的详细地址\",\"consignee\":\"李四\",\"mobile\":\"12547896321\",\"beDefault\":0,\"gmtCreate\":\"2020-12-02T11:31:04\",\"gmtModified\":null},{\"id\":6,\"regionId\":6,\"detail\":\"李四的详细地址\",\"consignee\":\"李四\",\"mobile\":\"54236985412\",\"beDefault\":0,\"gmtCreate\":\"2020-12-02T11:31:41\",\"gmtModified\":null},{\"id\":7,\"regionId\":6,\"detail\":\"李四的详细地址\",\"consignee\":\"李四\",\"mobile\":\"12345678909\",\"beDefault\":0,\"gmtCreate\":\"2020-12-02T11:32:06\",\"gmtModified\":null},{\"id\":8,\"regionId\":6,\"detail\":\"李四的详细地址\",\"consignee\":\"李四\",\"mobile\":\"12345678654\",\"beDefault\":0,\"gmtCreate\":\"2020-12-02T11:32:25\",\"gmtModified\":null},{\"id\":9,\"regionId\":3,\"detail\":\"李四的详细地址\",\"consignee\":\"李四\",\"mobile\":\"12546985412\",\"beDefault\":0,\"gmtCreate\":\"2020-12-02T11:32:41\",\"gmtModified\":null},{\"id\":10,\"regionId\":3,\"detail\":\"这是一个详细地址描述\",\"consignee\":\"王五\",\"mobile\":\"1360978565\",\"beDefault\":1,\"gmtCreate\":\"2020-12-02T11:32:58\",\"gmtModified\":\"2020-12-02T12:03:43\"},{\"id\":11,\"regionId\":3,\"detail\":\"李四的详细地址\",\"consignee\":\"李四\",\"mobile\":\"25489663215\",\"beDefault\":0,\"gmtCreate\":\"2020-12-02T11:33:21\",\"gmtModified\":null},{\"id\":12,\"regionId\":3,\"detail\":\"李四的详细地址\",\"consignee\":\"李四\",\"mobile\":\"12369854120\",\"beDefault\":0,\"gmtCreate\":\"2020-12-02T11:33:39\",\"gmtModified\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 分页数小于0
     */
    @Test
    public void getAllAddressesWithExceptions() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String responseString = this.mvc.perform(
                get("/addresses?page=0&pagesize=-1")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
}
