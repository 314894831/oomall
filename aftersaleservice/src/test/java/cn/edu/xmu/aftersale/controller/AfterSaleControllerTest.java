package cn.edu.xmu.aftersale.controller;

import cn.edu.xmu.aftersale.AfterSaleServiceApplication;
import cn.edu.xmu.aftersale.model.vo.LogSnVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AfterSaleServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AfterSaleControllerTest
{

    @Autowired
    private MockMvc mvc;

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
        String responseString = this.mvc.perform(put("/aftersale/shops/2/aftersales/3/deliver").header("authorization",token).contentType("application/json;charset=UTF-8").content(json))
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
        String responseString = this.mvc.perform(put("/aftersale/shops/2/aftersales/3/receive").header("authorization",token).contentType("application/json;charset=UTF-8").content(json))
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
        String responseString = this.mvc.perform(put("/aftersale/shops/2/aftersales/3/confirm").header("authorization",token).contentType("application/json;charset=UTF-8").content(json))
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
        String responseString = this.mvc.perform(get("/aftersale/shops/3/aftersales/1").header("authorization",token).contentType("application/json;charset=UTF-8"))
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
        String responseString = this.mvc.perform(put("/aftersale/aftersales/1/confirm").header("authorization",token).contentType("application/json;charset=UTF-8"))
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
        String responseString = this.mvc.perform(put("/aftersale/aftersales/1/sendback").header("authorization",token).contentType("application/json;charset=UTF-8").content(json))
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
        String responseString = this.mvc.perform(delete("/aftersale/aftersales/1").header("authorization",token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, true);

    }



}
