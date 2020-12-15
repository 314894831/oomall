package cn.edu.xmu.aftersale;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad", "cn.edu.xmu.aftersale"})
@MapperScan("cn.edu.xmu.aftersale.mapper")
@EnableDiscoveryClient
@EnableDubbo(scanBasePackages = "cn.edu.xmu.aftersale.service.impl")
public class AfterSaleServiceApplication {
    public static void main(String[] args){

        SpringApplication.run(AfterSaleServiceApplication.class,args);
    }
}
