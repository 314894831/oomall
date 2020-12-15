package cn.edu.xmu.address.controller;

import cn.edu.xmu.address.AddressServiceApplication;
import cn.edu.xmu.ooad.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = AddressServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RegionControllerTest
{
    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(RegionControllerTest.class);

    private String content;

    private final String creatTestToken(Long userId, Long departId, int expireTime)
    {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    /**
     * 正常情况，有两个父级地区
     *
     * @throws Exception
     */
    @Test
    void getParentRegionsById() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseSuccessString = this.mvc.perform(
                get("/region/region/3/ancestor").header("authorization", token)).
                andExpect(status().isOk()).
                andExpect(content().contentType("application/json;charset=UTF-8")).
                andReturn().getResponse().getContentAsString();
        logger.info("getParentRegionsById: " + responseSuccessString);
        content = "{\"errno\":0,\"data\":[{\"id\":2,\"pid\":1,\"name\":\"厦门\",\"postalCode\":10,\"state\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-12-03T12:53:21\"},{\"id\":1,\"pid\":0,\"name\":\"福建\",\"postalCode\":36,\"state\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-12-03T12:53:21\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * 获取地区的所有父级地区时，id不存在的情况
     */
    @Test
    void getParentRegionsById1() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseSuccessString = this.mvc.perform(get("/region/region/9/ancestor").header("authorization", token)).andExpect(status().isNotFound()).andExpect(content().contentType("application/json;charset=UTF-8")).andReturn().getResponse().getContentAsString();
        logger.info("getParentRegionsById: " + responseSuccessString);
        content = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * 获取地区的所有父级地区时，id存在，但没有父级地区
     */
    @Test
    void getParentRegionsById2() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseSuccessString = this.mvc.perform(
                get("/region/region/1/ancestor").
                        header("authorization", token)).
                andExpect(status().isOk()).
                andExpect(content().contentType("application/json;charset=UTF-8")).
                andReturn().getResponse().getContentAsString();
        logger.info("getParentRegionsById: " + responseSuccessString);
        content = "{errno=0, data=[], errmsg=成功}";
        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * 正常情况，有1个父级地区
     *
     * @throws Exception
     */
    @Test
    void getParentRegionsById3() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String responseSuccessString = this.mvc.perform(
                get("/region/region/2/ancestor").header("authorization", token)).
                andExpect(status().isOk()).
                andExpect(content().contentType("application/json;charset=UTF-8")).
                andReturn().getResponse().getContentAsString();
        logger.info("getParentRegionsById: " + responseSuccessString);
        content = "{errno=0, data=[{\"id\":1,\"pid\":0,\"name\":\"福建\",\"postalCode\":36,\"state\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-12-03T12:53:21\"}], errmsg=成功}";
        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * 修改地区信息，id不存在的情况
     */
    @Test
    public void updateRegionWithExceptions() throws Exception
    {

        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"name\":\"福州\",\"postalCode\":\"11\"}";
        String responseString = this.mvc.perform(
                put("/region/region/9")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 修改地区信息，id存在的情况
     */
    @Test
    public void updateRegionNoExceptions() throws Exception
    {

        String token = creatTestToken(1L, 0L, 100);
        String contentJson = "{\"name\":\"福州\",\"postalCode\":\"11\"}";
        String responseString = this.mvc.perform(
                put("/region/region/4")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 废弃地区，id存在的情况
     */
    @Test
    public void deleteRegionNoExceptions() throws Exception
    {

        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(
                delete("/region/region/4")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 要废弃的地区已被废弃
     */
    @Test
    public void deleteRegionWithExceptions() throws Exception
    {

        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(
                delete("/region/region/5")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 废除父级地区，所有的子地区全部废除
     */
    @Test
    public void deleteRegionNoExceptions1() throws Exception
    {

        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(
                delete("/region/region/1")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 正常在一个地区下新增子地区
     */
    @Test
    public void insertChildRegionNoExceptions() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String contentJson = "{\"name\":\"湖里\",\"postalCode\":\"6\"}";
        String responseString = this.mvc.perform(
                post("/region/region/2")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 在已废弃的地区下新增子地区
     */
    @Test
    public void insertChildRegionWithExceptions() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);

        String contentJson = "{\"name\":\"湖里\",\"postalCode\":\"6\"}";
        String responseString = this.mvc.perform(
                post("/region/region/5")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":602,\"errmsg\":\"地区已废弃\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
}
