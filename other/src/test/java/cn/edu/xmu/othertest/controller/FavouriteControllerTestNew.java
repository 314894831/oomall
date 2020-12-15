package cn.edu.xmu.othertest.controller;

import cn.edu.xmu.other.OtherApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * 收藏服务测试类
 * @author 汤海蕴
 */
@SpringBootTest(classes = OtherApplication.class)
public class FavouriteControllerTestNew {
    private final WebTestClient webClient;

    public FavouriteControllerTestNew(){
        this.webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8080")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }



}
