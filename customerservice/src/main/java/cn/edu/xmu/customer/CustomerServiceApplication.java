package cn.edu.xmu.customer;

import cn.edu.xmu.customer.dao.CustomerDao;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Shuhao Peng
 **/
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.customer"})
@MapperScan("cn.edu.xmu.customer.mapper")
@EnableDiscoveryClient
@EnableDubbo(scanBasePackages = "cn.edu.xmu.customer.service.impl")
public class CustomerServiceApplication {

    private  static  final Logger logger = LoggerFactory.getLogger(CustomerServiceApplication.class);
    /**
     * 是否初始化，加密
     */

    @Autowired
    private CustomerDao customerDao;


    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }


}
