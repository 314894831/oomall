package cn.edu.xmu.shoppingcart.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.shoppingcart.ShoppingCartServiceApplication;
import cn.edu.xmu.shoppingcart.dao.ShoppingCartDao;
import cn.edu.xmu.shoppingcart.model.po.ShoppingCartPo;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ShoppingCartServiceApplication.class)   //标识本类是一个SpringBootTest
@Transactional
public class ShoppingCartServiceTest {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Test
    public void getCart() {
        ReturnObject<PageInfo<VoObject>> returnObject = shoppingCartService.getCart(1, 10, 1L);

        System.out.print(returnObject.getData().toString());

    }
}
