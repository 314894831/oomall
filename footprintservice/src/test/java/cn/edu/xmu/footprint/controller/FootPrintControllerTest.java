package cn.edu.xmu.footprint.controller;

import cn.edu.xmu.footprint.FootPrintServiceApplication;
import cn.edu.xmu.footprint.dao.FootPrintDao;
import cn.edu.xmu.footprint.model.vo.FootPrintVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FootPrintServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional

public class FootPrintControllerTest {
    @Autowired
    private FootPrintDao footprintDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(FootPrintControllerTest.class);

    /**
     * 创建测试用token
     *
     * @author 24320182203271 汤海蕴
     * @param userId
     * @return token
     * createdBy 汤海蕴 2020/12/01 13:57
     * modifiedBy 汤海蕴 2020/12/01 19:20
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId,departId,expireTime);
        logger.debug(token);
        return token;
    }

    /**
     * 插入足迹 成功
     * @author 24320182203271 汤海蕴
     * createdBy 汤海蕴 2020/12/01 13:57
     * modifiedBy 汤海蕴 2020/12/01 19:20
     */
    @Test
    public void addFootPrintTest() {
        FootPrintVo vo = new FootPrintVo();
        vo.setGoodsSpuId(123456l);
        String token = creatTestToken(1L, 0L,100);
        String footprintJson = JacksonUtil.toJson(vo);
        System.out.println("json转化成功："+footprintJson);

        String expectedResponse="";
        String responseString=null;
        try {
            responseString = this.mvc.perform(post("/footprint/users/1/footprints").header("authorization", token).contentType("application/json;charset=UTF-8").content(footprintJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {

            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void selectFootPrintsTest(){
        String token = creatTestToken(1L, 0L,100);

        String expectedResponse="";
        String responseString=null;

        try {
            responseString = this.mvc.perform(get("/footprint/footprints?userId=&beginTime=&endTime=&page=1&pageSize=2").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            System.out.println("responseString:"+responseString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse =  "{\"errno\":0,\"data\":{\"total\":17231,\"pages\":8616,\"pageSize\":2,\"page\":1,\"list\":[{\"customerId\":1,\"goodsSpuId\":675,\"gmtCreate\":\"2020-11-29T21:10:32\"},{\"customerId\":2,\"goodsSpuId\":337,\"gmtCreate\":\"2020-11-29T21:10:32\"}]},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
