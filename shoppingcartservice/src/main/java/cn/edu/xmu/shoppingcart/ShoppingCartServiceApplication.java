package cn.edu.xmu.shoppingcart;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad", "cn.edu.xmu.shoppingcart"})
@MapperScan("cn.edu.xmu.shoppingcart.mapper")
public class ShoppingCartServiceApplication {
    public static void main(String[] args){
        SpringApplication.run(ShoppingCartServiceApplication.class,args);
    }
}
