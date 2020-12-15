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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yhr 24320182203309
 * @date Created in 2020/12/6
 **/
@SpringBootTest(classes = OtherApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AfterSaleControllerTest {
    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(AfterSaleControllerTest.class);

    private String content;

    //200申请新的售后服务
    @Test
    public void applyAfterSaleService1() throws Exception{
        String token=createTestToken(1L,0L,100);
        String contentJson ="{\"quantity\":\"1\",\"type\":\"0\",\"reason\":\"不想要了\",\"regionId\":\"1\",\"detail\":\"滨海街道\",\"consignee\":\"江小白\",\"mobile\":\"12345678901\"}";
        String responseString = this.mvc.perform(
                post("/orderItems/1/aftersales")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"orderId\":0,\"orderSn\":\"12345\",\"orderItemId\":1,\"skuId\":0,\"skuName\":\"?\",\"customerId\":1,\"shopId\":0,\"serviceSn\":\"12345\",\"type\":0,\"reason\":\"不想要了\",\"refund\":1,\"quantity\":1,\"regionId\":1,\"detail\":\"滨海街道\",\"consignee\":\"江小白\",\"mobile\":\"12345678901\",\"customerLogSn\":\"12345\",\"shopLogSn\":\"12345\",\"state\":0},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**买家查询所有售后单的信息
     * @author yhr
     * @date Created in 2020/12/7 10:33
     **/
    @Test
    public void getAfterSale1() throws Exception {
        String token=createTestToken(2L,0L,100);
        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/getAfterSaleSuccess1.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("content: " + content);
        String responseSuccessString = this.mvc.perform(
                get("/aftersales?beginTime=&endTime=&type=0&state=0&page=1&pagesize=3&spuId=448&skuId=")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * 获取所有售后状态（第一页）
     * @throws Exception
     **/
    @Test
    public void getAllAfterSale1() throws Exception{
        String token = this.createTestToken(13088L, 0L,100);
        String responseString = this.mvc.perform(get("/aftersales/states").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"待管理员审核\",\"code\":0},{\"name\":\"待买家发货\",\"code\":1},{\"name\":\"买家已发货\",\"code\":2},{\"name\":\"待店家退款\",\"code\":3},{\"name\":\"待店家发货\",\"code\":4},{\"name\":\"店家已发货\",\"code\":5},{\"name\":\"审核不通过\",\"code\":6},{\"name\":\"已取消\",\"code\":7},{\"name\":\"已结束\",\"code\":8}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**管理员查询所有售后单的信息
     * @author yhr
     * @date Created in 2020/12/7 10:33
     **/
    @Test
    public void adminGetAfterSale1() throws Exception {
        String token=createTestToken(2L,0L,100);
        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/getAfterSaleSuccess1.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("content: " + content);
        String responseSuccessString = this.mvc.perform(
                get("/shops/1/aftersales?beginTime=&endTime=&type=0&state=0&page=1&pagesize=3&spuId=448&skuId=")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**买家根据售后单id查询售后单信息
     * @author yhr
     * @date Created in 2020/12/8 10:33
     **/
    @Test
    public void getAfterSaleById1() throws Exception {
        String token=createTestToken(2L,0L,100);
        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/getAfterSaleFail.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("content: " + content);
        String responseSuccessString = this.mvc.perform(
                get("/aftersales/1")
                        .header("authorization", token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**买家根据售后单id修改售后单信息
     * @author yhr
     * @date Created in 2020/12/8 10:33
     **/
    @Test
    public void modifyAfterSaleById1() throws Exception {
        String token=createTestToken(2L,0L,100);
        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/modifyAfterSaleFail.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String contentJson="{\"quantity\":\"10\",\"reason\":\"不想要了\",\"regionId\":\"1\",\"mobile\":\"12345678900\",\"detail\":\"滨海街道\",\"consignee\":\"大白\"}";
        logger.debug("content: " + content);
        String responseSuccessString = this.mvc.perform(
                put("/aftersales/1")
                        .header("authorization", token).contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**买家根据售后单id取消售后单
     * @author yhr
     * @date Created in 2020/12/8 10:33
     **/
    @Test
    public void deleteAfterSaleById1() throws Exception {
        String token=createTestToken(2L,0L,100);
        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/deleteAfterSaleFail.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("content: " + content);
        String responseSuccessString = this.mvc.perform(
                delete("/aftersales/1")
                        .header("authorization", token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.debug("response: " + responseSuccessString);
        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * @author Kejian Shi
     * @throws Exception
     */
    @Test
    public void UploadLogSn() throws Exception{
        //LogSnVo vo = new LogSnVo("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        String json = "{" +
                "\"logSn\": \"hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh\"" +
                "}";

        String token = creatTestToken(2L,0L,10000000);
        String responseString = this.mvc.perform(put("/shops/2/aftersales/3/deliver").header("authorization",token).contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, true);


    }
    /**
     * @author Kejian Shi
     * @throws Exception
     */
    @Test
    public void confirmAfterSale() throws Exception{
        String json = "{" +
                "\"confirm\": true" +","+
                "\"conclusion\": \"string\""+
                "}";

        String token = creatTestToken(2L,0L,10000000);
        String responseString = this.mvc.perform(put("/shops/2/aftersales/3/receive").header("authorization",token).contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, true);


    }
    /**
     * @author Kejian Shi
     * @throws Exception
     */
    @Test
    public void AdminConfirmAfterSale() throws Exception{
        String json = "{" +
                "\"confirm\": true" +","+
                "\"conclusion\": \"string\""+
                "}";

        String token = creatTestToken(2L,0L,10000000);
        String responseString = this.mvc.perform(put("/shops/2/aftersales/3/confirm").header("authorization",token).contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, true);


    }
    /**
     * @author Kejian Shi
     * @throws Exception
     */
    @Test
    public void GetAfterSaleInfoById() throws Exception{
        String token = creatTestToken(2L,0L,10000000);
        String responseString = this.mvc.perform(get("/shops/3/aftersales/1").header("authorization",token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

    }
    /**
     * @author Kejian Shi
     * @throws Exception
     */
    @Test
    public void CustomerConfirmAfterSale() throws Exception{
        String token = creatTestToken(2L,0L,10000000);
        String responseString = this.mvc.perform(put("/aftersales/1/confirm").header("authorization",token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

    }


    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }
    /**
     * @author Kejian Shi
     * @throws Exception
     */
    @Test
    public void CustomerUploadLogSn() throws Exception{
        //LogSnVo vo = new LogSnVo("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        String json = "{" +
                "\"logSn\": \"hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh\"" +
                "}";

        String token = creatTestToken(2L,0L,10000000);
        String responseString = this.mvc.perform(put("/aftersales/1/sendback").header("authorization",token).contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, true);


    }
    /**
     * @author Kejian Shi
     * @throws Exception
     */
    @Test
    public void DeleteAfterSaleInfoById() throws Exception{
        String token = creatTestToken(2L,0L,10000000);
        String responseString = this.mvc.perform(delete("/aftersales/1").header("authorization",token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"找不到该订单\"}", responseString, true);

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
     **/
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug("token: " + token);
        return token;
    }
}
