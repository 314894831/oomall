package cn.edu.xmu.othertest.controller;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.other.OtherApplication;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockHttpServletRequestDsl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.nio.file.Files;
import java.nio.file.Paths;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yhr 24320182203309
 * @date Created in 2020/12/4
 **/
@SpringBootTest(classes = OtherApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ShareControllerTest {
    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(ShareControllerTest.class);

    private String content;

    /**200创建分享链接
     * @author yhr
     * @date Created in 2020/12/5 10:33
     **/
    @Test
    public void createShare() throws Exception{
        String token=createTestToken(1L,0L,100);
        String responseString = this.mvc.perform(
                post("/share/skus/10/shares")
                        .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":458697,\"sharerId\":1,\"quantity\":0,\"gmtCreate\":\"2020-12-12T09:35:40.8113732\",\"skuVo\":null},\"errmsg\":\"成功\"}";
         JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**买家查询分享测试
     * @author yhr
     * @date Created in 2020/12/5 10:33
     **/
    @Test
    public void getShare1() throws Exception {
        String token=createTestToken(9L,0L,100);
        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/getShareSuccess2.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("content: " + content);
        String responseSuccessString = this.mvc.perform(
                get("/share/shares?beginTime=&endTime=&page=1&pagesize=3&goodsSkuId=300")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**管理员查询分享测试
     * @author yhr
     * @date Created in 2020/12/5 10:33
     **/
    @Test
    public void adminGetShare1() throws Exception {
        String token=createTestToken(1L,0L,100);
        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/getShareSuccess1.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("content: " + content);
        String responseSuccessString = this.mvc.perform(
                get("/share/shops/2/shares?page=1&pagesize=3&goodsSkuId=448")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
         JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**买家查询分享成功测试
     * @author yhr
     * @date Created in 2020/12/5 10:33
     **/
    @Test
    public void getBeShared1() throws Exception {
        String token=createTestToken(557L,0L,100);
        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/getBeShareSuccess1.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("content: " + content);
        String responseSuccessString = this.mvc.perform(
                get("/share/beshared?beginTime=&endTime=&page=1&pagesize=3&goodsSkuId=678")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
         JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**管理员查询分享成功测试
     * @author yhr
     * @date Created in 2020/12/5 10:33
     **/
    @Test
    public void adminGetBeShared1() throws Exception {
        String token=createTestToken(2L,0L,100);
        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/getBeShareSuccess.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("content: " + content);
        String responseSuccessString = this.mvc.perform(
                get("/share/shops/2/beshared?beginTime=&endTime=&page=1&pagesize=3&goodsSkuId=448")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**200创建分享活动
     * @author yhr
     * @date Created in 2020/12/5 10:33
     **/
    @Test
    public void createShareActivity() throws Exception{
        String token=createTestToken(1L,0L,100);
        String contentJson ="{\"beginTime\":\"2020-12-04 20:00:00\",\"endTime\":\"2021-12-04 20:00:00\",\"strategy\":\"分享活动1\"}";
        String responseString = this.mvc.perform(
                post("/share/shops/1/goods/1/shareactivities")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":311259,\"shopId\":1,\"goodSkuId\":1,\"beginTime\":\"2020-12-04T20:00:00\",\"endTime\":\"2021-12-04T20:00:00\",\"state\":0},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**查询分享活动
     * @author yhr
     * @date Created in 2020/12/5 14:13
     **/
    @Test
    public void getShareActivities() throws Exception {
        String token=createTestToken(2L,0L,100);
        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/getShareActivitiesSuccess.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("content: " + content);
        String responseSuccessString = this.mvc.perform(
                get("/share/shareactivities?page=1&pagesize=3&goodsSkuId=678&shopId=0")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
         JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**修改分享活动
     * @author yhr
     * @date Created in 2020/12/5 14:13
     **/
    @Test
    public void modifyShareActivities() throws Exception {
        String token=createTestToken(2L,0L,100);
        //参数正确的请求
        String contentJson ="{\"beginTime\":\"2020-12-04 20:00:00\",\"endTime\":\"2021-12-04 20:00:00\",\"strategy\":\"分享活动2\"}";
        String responseSuccessString = this.mvc.perform(
                put("/share/shops/0/shareactivities/303068")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(responseSuccessString, expectedResponse, true);
    }

    /**创建测试用token
     * @author 3309
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy 3309 2020/12/2 16:54
     * modifiedBy 3309 2020/12/2 16:54
     **/
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug("token: " + token);
        return token;
    }

    /**String转为LocalDateTime
     * @param time
     * @return
     **/
    public static LocalDateTime parseStringToDateTime(String time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, df);
    }
}
