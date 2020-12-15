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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OtherApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TimeSegmentControllerTest
{
    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(TimeSegmentControllerTest.class);

    private final String creatTestToken(Long userId, Long departId, int expireTime)
    {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    /**
     * 正常新增广告时间段
     */
    @Test
    void addAdTime() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String contentJson = "{\"beginTime\":\"2020-11-01 09:52:20\",\"endTime\":\"2020-11-01 10:52:20\"}";
        String responseString = this.mvc.perform(
                post("/shops/0/advertisement/timesegments")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"beginTime\":\"2020-11-01T09:52:20\",\"endTime\":\"2020-11-01T10:52:20\",\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 新增广告时时间段冲突
     */
    @Test
    void addAdTimeWithExceptions() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String contentJson = "{\"beginTime\":\"2021-01-16 07:00:00\",\"endTime\":\"2021-01-16 09:00:00\"}";
        String responseString = this.mvc.perform(
                post("/shops/0/advertisement/timesegments")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":604,\"errmsg\":\"时段冲突\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 新增广告时时间段的开始时间为空
     */
    @Test
    void addAdTimeWithExceptions1() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String contentJson = "{\"beginTime\":\"\",\"endTime\":\"2021-01-18 12:00:00\"}";
        String responseString = this.mvc.perform(
                post("/shops/0/advertisement/timesegments")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().is(400))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":611,\"errmsg\":\"开始时间不能为空\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 新增广告时时间段的结束时间为空
     */
    @Test
    void addAdTimeWithExceptions2() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String contentJson = "{\"beginTime\":\"2020-11-01 09:52:20\",\"endTime\":\"\"}";
        String responseString = this.mvc.perform(
                post("/shops/0/advertisement/timesegments")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().is(400))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":612,\"errmsg\":\"结束时间不能为空\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 新增广告时时间段的开始时间大于结束时间
     */
    @Test
    void addAdTimeWithExceptions3() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String contentJson = "{\"beginTime\":\"2021-11-18 09:52:21\",\"endTime\":\"2020-11-01 09:52:20\"}";
        String responseString = this.mvc.perform(
                post("/shops/0/advertisement/timesegments")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":610,\"errmsg\":\"开始时间大于结束时间\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员获取广告时间段列表
     */
    @Test
    void getAdTimeWithNoExceptions() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String responseString = this.mvc.perform(
                get("/shops/0/advertisement/timesegments?page=3&pagesize=1")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":14,\"pages\":14,\"pageSize\":1,\"page\":3,\"list\":[{\"id\":3,\"beginTime\":\"2021-01-03T10:00:00\",\"endTime\":\"2021-01-03T16:00:00\",\"gmtCreate\":\"2020-11-28T21:01:01\",\"gmtModified\":\"2020-11-28T21:01:01\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 正常新增秒杀时间段
     */
    @Test
    void addFlashTime() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String contentJson = "{\"beginTime\":\"2021-01-16 06:00:00\",\"endTime\":\"2021-01-16 10:00:00\"}";
        String responseString = this.mvc.perform(
                post("/shops/0/flashsale/timesegments")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"beginTime\":\"2021-01-16T06:00:00\",\"endTime\":\"2021-01-16T10:00:00\",\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 管理员获取秒杀时间段列表
     */
    @Test
    void getFlashTimeWithNoExceptions() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String responseString = this.mvc.perform(
                get("/shops/0/flashsale/timesegments?page=4&pagesize=2")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":13,\"pages\":7,\"pageSize\":2,\"page\":4,\"list\":[{\"id\":14,\"beginTime\":\"2021-01-14T06:00:00\",\"endTime\":\"2021-01-14T10:00:00\",\"gmtCreate\":\"2020-11-28T21:10:24\",\"gmtModified\":\"2020-11-28T21:10:24\"},{\"id\":15,\"beginTime\":\"2021-01-14T10:00:00\",\"endTime\":\"2021-01-14T20:00:00\",\"gmtCreate\":\"2020-11-28T21:10:24\",\"gmtModified\":\"2020-11-28T21:10:24\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常删除广告时间段
     */
    @Test
    void deleteAdTimeWithNoExceptions() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String responseString = this.mvc.perform(
                delete("/shops/0/advertisement/timesegments/27")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常删除秒杀时间段
     */
    @Test
    void deleteFlashTimeWithNoExceptions() throws Exception
    {
        String token = creatTestToken(2L, 0L, 100);
        String responseString = this.mvc.perform(
                delete("/shops/0/flashsale/timesegments/27")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
}
