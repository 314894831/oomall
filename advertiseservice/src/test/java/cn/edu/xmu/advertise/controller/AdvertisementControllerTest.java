package cn.edu.xmu.advertise.controller;

import cn.edu.xmu.advertise.AdvertisementServiceApplication;
import cn.edu.xmu.ooad.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AdvertisementServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdvertisementControllerTest
{
    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(AdvertisementControllerTest.class);

    private final String creatTestToken(Long userId, Long departId, int expireTime)
    {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    @Test
    void findAllAdState() throws Exception
    {
        String token = creatTestToken(1L, -2L, 100);

        String responseString = this.mvc.perform(
                get("/advertisement/states")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"审核\",\"code\":0},{\"name\":\"上架\",\"code\":4},{\"name\":\"下架\",\"code\":6}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 正常添加广告，且该广告为该时段下第一个广告，增加时即设置为默认
     * @throws Exception
     */
    @Test
    void addAdUnderTimeSegNoException() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"content\":\"广告内容\",\"weight\":\"80\",\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-11\",\"repeat\":\"1\",\"link\":\"c:user\"}";

        String responseString = this.mvc.perform(
                post("/shops/0/timesegments/1/advertisement")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"segId\":1,\"link\":\"c:user\",\"content\":\"广告内容\",\"imagePath\":null,\"state\":0,\"weight\":80,\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-11\",\"repeats\":1,\"beDefault\":1,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 正常添加广告，且该广告不是该时段下第一个广告，增加时即设置为非默认
     * @throws Exception
     */
    @Test
    void addAdUnderTimeSegNoException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"content\":\"广告内容\",\"weight\":\"80\",\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-11\",\"repeat\":\"0\",\"link\":\"c:user\"}";

        String responseString = this.mvc.perform(
                post("/shops/0/timesegments/1/advertisement")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"segId\":1,\"link\":\"c:user\",\"content\":\"广告内容\",\"imagePath\":null,\"state\":0,\"weight\":80,\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-11\",\"repeats\":0,\"beDefault\":0,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 时段id不存在
     * @throws Exception
     */
    @Test
    void addAdUnderTimeSegWithException() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"content\":\"广告内容\",\"weight\":\"80\",\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-11\",\"repeat\":\"0\",\"link\":\"c:user\"}";

        String responseString = this.mvc.perform(
                post("/shops/0/timesegments/1000/advertisement")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 添加广告时，开始时间设置为空
     * @throws Exception
     */
    @Test
    void addAdUnderTimeSegWithException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"content\":\"广告内容\",\"weight\":\"80\",\"beginDate\":\"\",\"endDate\":\"2020-12-11\",\"repeat\":\"0\",\"link\":\"c:user\"}";

        String responseString = this.mvc.perform(
                post("/shops/0/timesegments/1/advertisement")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":611,\"errmsg\":\"开始时间不能为空\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 添加广告时，结束时间设置为空
     * @throws Exception
     */
    @Test
    void addAdUnderTimeSegWithException2() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"content\":\"广告内容\",\"weight\":\"80\",\"beginDate\":\"2020-12-10\",\"endDate\":\"\",\"repeat\":\"0\",\"link\":\"c:user\"}";

        String responseString = this.mvc.perform(
                post("/shops/0/timesegments/1/advertisement")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":612,\"errmsg\":\"结束时间不能为空\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 添加广告时，开始时间大于结束时间
     * @throws Exception
     */
    @Test
    void addAdUnderTimeSegWithException3() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"content\":\"广告内容\",\"weight\":\"80\",\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-09\",\"repeat\":\"0\",\"link\":\"c:user\"}";

        String responseString = this.mvc.perform(
                post("/shops/0/timesegments/1/advertisement")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":610,\"errmsg\":\"开始时间大于结束时间\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 该时段下广告数量已达8个
     * @throws Exception
     */
    @Test
    void addAdUnderTimeSegWithException4() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"content\":\"广告内容\",\"weight\":\"80\",\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-11\",\"repeat\":\"0\",\"link\":\"c:user\"}";

        String responseString = this.mvc.perform(
                post("/shops/0/timesegments/2/advertisement")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":603,\"errmsg\":\"达到时段广告上限\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 正常更改广告的时段
     * @throws Exception
     */
    @Test
    void updateAdTimeNoException() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                post("/shops/0/timesegments/3/advertisement/152")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":152,\"segId\":3,\"link\":\"c:user\",\"content\":\"广告内容\",\"imagePath\":null,\"state\":0,\"weight\":80,\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-11\",\"repeats\":0,\"beDefault\":0,\"gmtCreate\":\"2020-12-07T16:29:52\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 更改的时段广告已满
     * @throws Exception
     */
    @Test
    void updateAdTimeWithException() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                post("/shops/0/timesegments/2/advertisement/152")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":603,\"errmsg\":\"达到时段广告上限\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 更改的时段id不存在
     * @throws Exception
     */
    @Test
    void updateAdTimeWithException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                post("/shops/0/timesegments/1000/advertisement/152")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常查看某时段的广告
     * @throws Exception
     */
    @Test
    void findAllAdByTimeIdNoException() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                get("/shops/0/timesegments/2/advertisement?page=3&pagesize=1")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":8,\"pages\":8,\"pageSize\":1,\"page\":3,\"list\":[{\"id\":155,\"segId\":2,\"link\":\"2\",\"content\":\"2\",\"imagePath\":null,\"state\":0,\"weight\":0,\"beginDate\":\"2020-12-07\",\"endDate\":\"2020-12-07\",\"repeats\":1,\"beDefault\":0,\"gmtCreate\":\"2020-12-07T16:40:11\",\"gmtModified\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常查看的时段没有广告
     * @throws Exception
     */
    @Test
    void findAllAdByTimeIdNoException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                get("/shops/0/timesegments/30/advertisement?page=3&pagesize=1")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":1,\"page\":3,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常通过广告审核
     * @throws Exception
     */
    @Test
    void examAdvertisementNoException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"conclusion\":\"true\",\"message\":\"通过审核\"}";

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/154/audit")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常不通过广告审核
     * @throws Exception
     */
    @Test
    void examAdvertisementNoException2() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"conclusion\":\"false\",\"message\":\"不通过审核\"}";

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/154/audit")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员审核已通过审核的广告
     * @throws Exception
     */
    @Test
    void examAdvertisementWithException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"conclusion\":\"false\",\"message\":\"不通过审核\"}";

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/123/audit")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":608,\"errmsg\":\"广告状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员审核的广告id不存在
     * @throws Exception
     */
    @Test
    void examAdvertisementWithException2() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"conclusion\":\"false\",\"message\":\"不通过审核\"}";

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/12/audit")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员不输入审核意见和附言
     * @throws Exception
     */
    @Test
    void examAdvertisementWithException3() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"conclusion\":\"\",\"message\":\"\"}";

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/154/audit")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":503,\"errmsg\":\"必须输入审核附言;必须输入审核结果;\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员输入的审核意见不合法
     * @throws Exception
     */
    @Test
    void examAdvertisementWithException4() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"conclusion\":\"con\",\"message\":\"通过审核\"}";

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/154/audit")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":503,\"errmsg\":\"审核意见只能为true或false\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常下架广告
     * @throws Exception
     */
    @Test
    void offLineAdvertisementNoException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/146/offshelves")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员下架处于下架状态的广告
     * @throws Exception
     */
    @Test
    void offLineAdvertisementWithException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/147/offshelves")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":608,\"errmsg\":\"广告状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员下架处于审核状态的广告
     * @throws Exception
     */
    @Test
    void offLineAdvertisementWithException2() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/151/offshelves")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":608,\"errmsg\":\"广告状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员下架的广告不存在
     * @throws Exception
     */
    @Test
    void offLineAdvertisementWithException3() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/2000/offshelves")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常上架广告
     * @throws Exception
     */
    @Test
    void onLineAdvertisementNoException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/122/onshelves")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员上架处于上架状态的广告
     * @throws Exception
     */
    @Test
    void onLineAdvertisementWithException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/139/onshelves")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":608,\"errmsg\":\"广告状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员上架处于审核状态的广告
     * @throws Exception
     */
    @Test
    void onLineAdvertisementWithException2() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/161/onshelves")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":608,\"errmsg\":\"广告状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员上架不存在的广告
     * @throws Exception
     */
    @Test
    void onLineAdvertisementWithException3() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/2000/onshelves")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常设置默认广告
     * @throws Exception
     */
    @Test
    void setDefaultAdvertisementNoException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/123/default")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员设置不存在的广告
     * @throws Exception
     */
    @Test
    void setDefaultAdvertisementWithException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/2000/default")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常删除一个处于审核状态的广告
     * @throws Exception
     */
    @Test
    void deleteAdvertisementNoException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                delete("/shops/0/advertisement/162")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常删除一个处于下架状态的广告
     * @throws Exception
     */
    @Test
    void deleteAdvertisementNoException2() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                delete("/shops/0/advertisement/163")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员删除处于上架状态的广告
     * @throws Exception
     */
    @Test
    void deleteAdvertisementWithException1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                delete("/shops/0/advertisement/123")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":608,\"errmsg\":\"广告状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员删除不存在的广告
     * @throws Exception
     */
    @Test
    void deleteAdvertisementWithException2() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseString = this.mvc.perform(
                delete("/shops/0/advertisement/2000")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员正常上传广告图片
     * @throws Exception
     **/
    @Test
    void uploadImageNoException1() throws Exception
    {
        String token = creatTestToken(1L,0L,100);
        File file = new File("."+File.separator + "src" + File.separator + "test" + File.separator+"resources" + File.separator + "img" + File.separator+"timg.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "timg.png" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/shops/0/advertisement/125/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 获取当前时段广告列表
     * @throws Exception
     */
    @Test
    void getAllAdByTimeNowNoException1() throws Exception
    {

        String responseString = this.mvc.perform(
                get("/advertisement/current"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":162,\"link\":\"23123\",\"imagePath\":null,\"content\":\"23123123\",\"segId\":28,\"state\":6,\"weight\":54,\"beDefault\":null,\"beginDate\":\"2020-12-07\",\"endDate\":\"2020-12-09\",\"repeats\":1,\"gmtCreate\":\"2020-12-10T18:27:56\",\"gmtModified\":null},{\"id\":147,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201912/1576123361855871865.jpg\",\"content\":null,\"segId\":28,\"state\":6,\"weight\":27,\"beDefault\":null,\"beginDate\":\"2020-12-15\",\"endDate\":\"2021-10-10\",\"repeats\":1,\"gmtCreate\":\"2020-12-05T13:22:05\",\"gmtModified\":\"2020-12-07T20:43:41\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员修改广告内容
     * @throws Exception
     */
    @Test
    void updateAdvertisementByIdNoException() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"content\":\"广告内容1111\",\"segId\":\"1\",\"weight\":\"90\",\"link\":\"c:user111\"}";

        String responseString = this.mvc.perform(
                put("/shops/0/advertisement/152")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
}