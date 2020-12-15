package cn.edu.xmu.address;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ChengYang Li
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad", "cn.edu.xmu.address"})
@MapperScan("cn.edu.xmu.address.mapper")
public class AddressServiceApplication
{
    public static void main(String[] args) {
        SpringApplication.run(AddressServiceApplication.class, args);
    }
}
