package cn.edu.xmu.advertise;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ChengYang Li
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad", "cn.edu.xmu.advertise"})
@MapperScan("cn.edu.xmu.advertise.mapper")
public class AdvertisementServiceApplication
{
    public static void main(String[] args) {
        SpringApplication.run(AdvertisementServiceApplication.class, args);
    }
}
