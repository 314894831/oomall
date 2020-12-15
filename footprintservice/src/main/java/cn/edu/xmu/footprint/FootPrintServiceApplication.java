package cn.edu.xmu.footprint;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.footprint"})
@MapperScan("cn.edu.xmu.footprint.mapper")
public class FootPrintServiceApplication {
    public  static void main(String[] args){
        SpringApplication.run(FootPrintServiceApplication.class,args);
    }
}
