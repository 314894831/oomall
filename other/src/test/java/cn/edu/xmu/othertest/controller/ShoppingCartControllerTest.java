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

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yhr 24320182203309
 * @date Created in 2020/12/1
 **/
@SpringBootTest(classes = OtherApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartControllerTest.class);

    private String content;

    /**
     * 正常添加购物车
     * @throws Exception
     */
    @Test
    public void addToCart() throws Exception{
        String token=createTestToken(1L,0L,100);
        String requireJson = "{\"goodSkuID\":\"14\",\"quantity\":\"10\"}";
        String responseString = this.mvc.perform(
                post("/carts")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":null,\"errmsg\":\"成功\"}";
        //JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 正常添加同一个商品到购物车，只对数量进行修改
     * @throws Exception
     */
    @Test
    public void addToCart1() throws Exception{
        String token=createTestToken(1L,0L,100);
        String requireJson = "{\"goodSkuID\":\"4\",\"quantity\":\"2\"}";
        String responseString = this.mvc.perform(
                post("/carts")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":null,\"errmsg\":\"成功\"}";
        //JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * skuid为空
     * @throws Exception
     */
    @Test
    public void addToCart2() throws Exception{
        String token=createTestToken(1L,0L,100);
        String requireJson = "{\"goodSkuID\":\"\",\"quantity\":\"10\"}";

        String responseString = this.mvc.perform(
                post("/carts")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":503,\"errmsg\":\"SkuID不能为空;\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * quantity为空
     * @throws Exception
     */
    @Test
    public void addToCart3() throws Exception{
        String token=createTestToken(1L,0L,100);
        String requireJson = "{\"goodSkuID\":\"4\",\"quantity\":\"\"}";

        String responseString = this.mvc.perform(
                post("/carts")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":503,\"errmsg\":\"数量不能为空;\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    //查询测试
    /** @author yhr
     * @date Created in 2020/12/2 10:33
     **/
    @Test
    public void getCart1() throws Exception {
        String token=createTestToken(1L,0L,100);
        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/getCartSuccess.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("content: " + content);
        String responseSuccessString = this.mvc.perform(get("/carts?page=1&pagesize=3").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
        //JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * 测试更新cart
     * @throws Exception Assert 或 HTTP 错误
     * @author 24320182203309 yhr
     */
    @Test
    public void modifyCart() throws Exception {
        String token=createTestToken(2L,0L,100);
        String requireJson = "{\"goodSkuID\":\"4\",\"quantity\":\"10\"}";

        String responseString = this.mvc.perform(
                put("/carts/1")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试更新cart，购物车id不存在
     * @throws Exception Assert 或 HTTP 错误
     * @author 24320182203309 yhr
     */
    @Test
    public void modifyCartWithException() throws Exception {
        String token=createTestToken(1L,0L,100);
        String requireJson = "{\"goodSkuID\":\"4\",\"quantity\":\"10\"}";
        String responseString = this.mvc.perform(
                put("/carts/3")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试更新cart，更新其他人的购物车
     * @throws Exception Assert 或 HTTP 错误
     * @author 24320182203309 yhr
     */
    @Test
    public void modifyCartWithException1() throws Exception {
        String token=createTestToken(1L,0L,100);
        String requireJson = "{\"goodSkuID\":\"4\",\"quantity\":\"10\"}";
        String responseString = this.mvc.perform(
                put("/carts/1")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试  删除购物车明细
     * @throws Exception Assert 或 HTTP 错误
     * @author 24320182203309 yhr
     */
    @Test
    public void deleteCartItem() throws Exception {
        String token=createTestToken(3L,0L,100);
        String responseString = this.mvc.perform(
                delete("/carts/11")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试  删除其他买家的购物车明细
     * @throws Exception Assert 或 HTTP 错误
     * @author 24320182203309 yhr
     */
    @Test
    public void deleteCartItemWithException() throws Exception {
        String token=createTestToken(3L,0L,100);
        String responseString = this.mvc.perform(
                delete("/carts/1")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试  删除用户购物车
     * @throws Exception Assert 或 HTTP 错误
     * @author 24320182203309 yhr
     */
    @Test
    public void deleteCart() throws Exception {
        String token=createTestToken(3L,0L,100);
        String responseString = this.mvc.perform(
                delete("/carts")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    /**
     * 创建测试用token
     * @author 3309
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy 3309 2020/12/2 16:54
     * modifiedBy 3309 2020/12/2 16:54
     */
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug("token: " + token);
        return token;
    }
}
