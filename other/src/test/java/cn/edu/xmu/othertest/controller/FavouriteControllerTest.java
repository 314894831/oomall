package cn.edu.xmu.othertest.controller;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.other.OtherApplication;
import org.json.JSONException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OtherApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc    //配置模拟的MVC，这样可以不启动服务器测试
@Transactional
public class FavouriteControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(FavouriteControllerTest.class);;

    private String content;

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId,departId,expireTime);
        logger.debug(token);
        return token;
    }

    @Test
    public void selectFavouritesTest(){
        String token = creatTestToken(2L, 0L,100);

        String expectedResponse="";
        String responseString=null;

        try {
            responseString = this.mvc.perform(get("/favourite/favourites?page=1&pageSize=2").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse =  "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":2,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    @Test
    public void deleteFavouritesTest() {
        String token = creatTestToken(2L, 0L, 100);

        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(delete("/favourite/favourites/482").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse =  "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Commit
    public void addFavouritesTest() {
        String token = creatTestToken(1L, 0L, 100);

        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(post("/favourite/favourites/goods/569").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse =  "{\"errno\":0,\"data\":{\"id\":3833740,\"customerId\":1,\"goodsSkuId\":569,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
