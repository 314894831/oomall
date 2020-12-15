package cn.edu.xmu.share;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad", "cn.edu.xmu.share"})
@MapperScan("cn.edu.xmu.share.mapper")
public class ShareServiceApplication {
    public static void main(String[] args){
        SpringApplication.run(ShareServiceApplication.class,args);
    }
}
