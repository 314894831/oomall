package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author 24320182203282 王显伟
 * created at 11/30/20 12:27 PM
 * @detail cn.edu.xmu.oomall
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
public class WangxianweiTest {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    private String token = "";

    @BeforeEach
    public void setUp(){

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    

    
    @Test
    @Order(1)
    public void getBesharedTest1() throws Exception{
        token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?page=1&pageSize=1").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 13,\n" +
                "        \"pages\": 13,\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434151,\n" +
//                "                \"sku\": {\n" +
//                "                \t\"id\":505,\n" +
//                "                \t\"name\":\"+\",\n" +
//                "                \t\"skuSn\":null,\n" +
//                "                \t\"imageUrl\":\"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
//                "                \t\"inventory\":100,\n" +
//                "                \t\"originalPrice\": 1399,\n" +
//                "\t\t\t        \"price\": 1399,\n" +
//                "\t\t\t        \"disable\": false\n" +
//                "                }," +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    @Test
    @Order(2)
    public void getBesharedTest2() throws Exception {
        token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?skuId=505&page=1&pageSize=10").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 2,\n" +
                "        \"pages\": 1,\n" +
                "        \"pageSize\": 10,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434151,\n" +
//                "                \"sku\": {\n" +
//                "                \t\"id\":505,\n" +
//                "                \t\"name\":\"+\",\n" +
//                "                \t\"skuSn\":null,\n" +
//                "                \t\"imageUrl\":\"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
//                "                \t\"inventory\":100,\n" +
//                "                \t\"originalPrice\": 1399,\n" +
//                "\t\t\t        \"price\": 1399,\n" +
//                "\t\t\t        \"disable\": false\n" +
//                "                }," +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 434542,\n" +
//                "                \"sku\": {\n" +
//                "                \t\"id\":505,\n" +
//                "                \t\"name\":\"+\",\n" +
//                "                \t\"skuSn\":null,\n" +
//                "                \t\"imageUrl\":\"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
//                "                \t\"inventory\":100,\n" +
//                "                \t\"originalPrice\": 1399,\n" +
//                "\t\t\t        \"price\": 1399,\n" +
//                "\t\t\t        \"disable\": false\n" +
//                "                }," +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * /beshared测试3 查询条件beginTime和endTime
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(3)
    public void getBesharedTest3() throws Exception {
        token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?beginTime=2020-12-06 22:00:00&endTime=2020-12-07 22:00:00&page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 13,\n" +
                "        \"pages\": 13,\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434151,\n" +
//                "                \"sku\": {\n" +
//                "                \t\"id\":505,\n" +
//                "                \t\"name\":\"+\",\n" +
//                "                \t\"skuSn\":null,\n" +
//                "                \t\"imageUrl\":\"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
//                "                \t\"inventory\":100,\n" +
//                "                \t\"originalPrice\": 1399,\n" +
//                "\t\t\t        \"price\": 1399,\n" +
//                "\t\t\t        \"disable\": false\n" +
//                "                }," +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /beshared测试4 查询条件page和pageSize
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(4)
    public void getBesharedTest4() throws Exception {
        token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?page=2&pageSize=5").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 13,\n" +
                "        \"pages\": 3,\n" +
                "        \"pageSize\": 5,\n" +
                "        \"page\": 2,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434160,\n" +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 434171,\n" +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 434526,\n" +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 434542,\n" +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 434854,\n" +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * /beshared测试6 开始时间在结束时间之后,返回空
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(6)
    public void getBesharedTest6() throws Exception {
        token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?beginTime=2020-12-07 22:00:00&endTime=2019-12-07 22:00:00").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 0,\n" +
                "        \"pages\": 0,\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": []\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /beshared测试7 开始时间和结束时间格式错误,返回错误码503
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(7)
    public void getBesharedTest7() throws Exception {
        token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?beginTime=2020-12-22:00:00&endTime=2019-12-44 :00:00").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shares测试1
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(8)
    public void getSharesTest1() throws Exception {
        token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/shares/?page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 13,\n" +
                "        \"pages\": 13,\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 442316,\n" +
                "                \"sharerId\": 2,\n" +
//                "\"sku\": {\n" +
//                "                \"id\":501,\n" +
//                "                \"name\":\"+\",\n" +
//                "                \"skuSn\":null,\n" +
//                "                \"imageUrl\":\"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
//                "                \"inventory\":100,\n" +
//                "                \"originalPrice\": 1399,\n" +
//                "              \"price\": 1399,\n" +
//                "               \"disable\": false\n" +
//                "                }," +
                "                \"quantity\": 1,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shops/{shopid}/goods/{skuid}/shareactivities测试1
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(9)
    public void createShareActivity1() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/goods/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2021-11-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2021-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"shopId\": 0,\n" +
                "        \"skuId\": 501,\n" +
                "        \"beginTime\": \"2021-11-30T23:59\",\n" +
                "        \"endTime\": \"2021-12-15T23:23:23\",\n" +
                "        \"state\": 1\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shops/{shopid}/goods/{skuid}/shareactivities测试2 分享规则格式错误，返回614错误码
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(10)
    public void createShareActivity2() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/goods/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 614\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shops/{shopid}/goods/{skuid}/shareactivities测试3 开始时间为空，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(11)
    public void createShareActivity3() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/goods/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }



    /**
     * /shops/{shopid}/goods/{skuid}/shareactivities测试4 规则为空，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(12)
    public void createShareActivity4() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/goods/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shops/{shopid}/goods/{skuid}/shareactivities测试5 分享活动时段冲突，返回605错误码
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(13)
    public void createShareActivity5() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/goods/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2020-12-07 21:47:19\",\n" +
                        "\t\"endTime\":\"2021-10-10 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 605\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shops/{shopid}/goods/{skuid}/shareactivities测试6 开始时间格式错误，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(14)
    public void createShareActivity6() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/goods/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"1234-1321-321\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shops/{shopid}/goods/{skuid}/shareactivities测试7 结束时间格式错误，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(15)
    public void createShareActivity7() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/goods/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
                        "\t\"endTime\":\"23432fq\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shops/{shopid}/goods/{skuid}/shareactivities测试8 开始时间在结束时间之后，返回610
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(16)
    public void createShareActivity8() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/goods/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2021-12-10 21:47:19\",\n" +
                        "\t\"endTime\":\"2021-12-07 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 610\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试1
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(16)
    public void updateShareActivity1() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/shareactivities/307696").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
                        "\t\"endTime\":\"2001-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试2 分享规则格式错误，返回614错误码
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(17)
    public void updateShareActivity2() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/shareactivities/307696").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 614\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试3 开始时间为空，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(18)
    public void updateShareActivity3() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/shareactivities/307696").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }



    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试4 规则为空，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(19)
    public void updateShareActivity4() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/shareactivities/307696").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试5 分享活动时段冲突，返回605错误码
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(20)
    public void updateShareActivity5() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/shareactivities/307696").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2020-12-07 21:47:19\",\n" +
                        "\t\"endTime\":\"2021-10-10 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 605\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试6 开始时间格式错误，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(21)
    public void updateShareActivity6() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/shareactivities/307696").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"1234-1321-321\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试7 结束时间格式错误，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(22)
    public void updateShareActivity7() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/shareactivities/307696").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
                        "\t\"endTime\":\"23432fq\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试8 分享活动id不存在
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(23)
    public void updateShareActivity8() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/shareactivities/0").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
                        "\t\"endTime\":\"2001-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试9 开始时间在结束时间之后 返回610
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(23)
    public void updateShareActivity9() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/shareactivities/307696").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2021-12-10 21:47:19\",\n" +
                        "\t\"endTime\":\"2021-12-07 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 610\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}/online测试1 成功
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(24)
    public void onlineShareActivity1() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/shareactivities/307696/online").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * DELETE /shops/{shopid}/shareactivities/{id}测试1 分享活动id不存在
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(25)
    public void deleteShareActivity1() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/shareactivities/0").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * DELETE /shops/{shopid}/shareactivities/{id}测试2 分享活动不是该商铺的,返回505
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(26)
    public void deleteShareActivity2() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/1/shareactivities/303068").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 505\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * DELETE /shops/{shopid}/shareactivities/{id}测试3 成功
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(27)
    public void deleteShareActivity3() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/shareactivities/307696").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shops/{did}/skus/{id}/beshared测试1 成功
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(28)
    public void adminSelectBeshared1() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/501/beshared?page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 10,\n" +
                "        \"pages\": 10,\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434162,\n" +
//                "                \"sku\": {\n" +
//                "                \"id\":501,\n" +
//                "                \"name\":\"+\",\n" +
//                "                \"skuSn\":null,\n" +
//                "                \"imageUrl\":\"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
//                "                \"inventory\":100,\n" +
//                "                \"originalPrice\": 1399,\n" +
//                "               \"price\": 1399,\n" +
//                "               \"disable\": false\n" +
//                "                },\n" +
                "                \"sharerId\": 1912,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shops/{did}/skus/{id}/beshared测试2 时间条件限制 成功
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(29)
    public void adminSelectBeshared2() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/501/beshared?beginTime=2020-12-06 22:00:00&endTime=2020-12-07 22:00:00&page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 10,\n" +
                "        \"pages\": 10,\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434162,\n" +
//                "                \"sku\": {\n" +
//                "                \"id\":501,\n" +
//                "                \"name\":\"+\",\n" +
//                "                \"skuSn\":null,\n" +
//                "                \"imageUrl\":\"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
//                "                \"inventory\":100,\n" +
//                "                \"originalPrice\": 1399,\n" +
//                "               \"price\": 1399,\n" +
//                "               \"disable\": false\n" +
//                "                },\n" +
                "                \"sharerId\": 1912,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shops/{did}/skus/{id}/beshared测试3 开始时间在结束时间之后 返回空
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(30)
    public void adminSelectBeshared3() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/505/beshared?beginTime=2020-12-08 22:00:00&endTime=2020-12-07 22:00:00").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 0,\n" +
                "        \"pages\": 0,\n" +
                "        \"pageSize\": 10,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": []\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * GET /shops/{did}/skus/{id}/beshared测试4 开始时间格式错误 错误码610
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(31)
    public void adminSelectBeshared4() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/505/beshared?beginTime=2020-:00&endTime=2020-12-07 22:00:00").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * GET /shops/{did}/skus/{id}/beshared测试4 结束时间格式错误 错误码610
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(32)
    public void adminSelectBeshared5() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/505/beshared?beginTime=2020-12-07 22:00:00&endTime=202000:00").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shops/{did}/skus/{id}/shares测试1
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(32)
    public void adminSelectShares1() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/501/shares?page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 10,\n" +
                "        \"pages\": 10,\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 442327,\n" +
                "                \"sharerId\": 1912,\n" +
//                "                \"sku\": {\n" +
//                "                \"id\":501,\n" +
//                "                \"name\":\"+\",\n" +
//                "                \"skuSn\":null,\n" +
//                "                \"imageUrl\":\"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
//                "                \"inventory\":100,\n" +
//                "                \"originalPrice\": 1399,\n" +
//                "        \"price\": 1399,\n" +
//                "        \"disable\": false\n" +
//                "                }," +
                "                \"quantity\": 720,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shops/{did}/skus/{id}/shares测试2 时间段查询
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(33)
    public void adminSelectShares2() throws Exception {
        token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/501/shares?beginTime=2020-12-06 22:00:00&endTime=2020-12-07 22:00:00&page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 10,\n" +
                "        \"pages\": 10,\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 442327,\n" +
                "                \"sharerId\": 1912,\n" +
//                "                \"sku\": {\n" +
//                "                \"id\":501,\n" +
//                "                \"name\":\"+\",\n" +
//                "                \"skuSn\":null,\n" +
//                "                \"imageUrl\":\"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
//                "                \"inventory\":100,\n" +
//                "                \"originalPrice\": 1399,\n" +
//                "        \"price\": 1399,\n" +
//                "        \"disable\": false\n" +
//                "                }," +
                "                \"quantity\": 720,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * 用户登录
     *
     * @author 王显伟
     */
    public String userLogin(String userName, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString)).getString("data");
    }

    private String adminLogin(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

}
