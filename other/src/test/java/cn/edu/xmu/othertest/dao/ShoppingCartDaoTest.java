package cn.edu.xmu.othertest.dao;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.OtherApplication;
import cn.edu.xmu.other.dao.ShoppingCartDao;
import cn.edu.xmu.other.model.po.ShoppingCartPo;
import cn.edu.xmu.other.model.vo.ShoppingCartVo.ShoppingCartVo;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = OtherApplication.class)   //标识本类是一个SpringBootTest
@Transactional
public class ShoppingCartDaoTest {

    @Autowired
    private ShoppingCartDao shoppingCartDao;

    @Test
    public void getCart() {
        PageInfo<ShoppingCartPo> cartPos = shoppingCartDao.getCartItem(1, 10, 1L);

        assertEquals(cartPos.getList().size(), 1);
    }

    @Test
    public void modifyCart() {
        ShoppingCartVo vo = null;
        vo.setGoodSkuID(20L);
        vo.setQuantity(20);
        ReturnObject<Object> ret = shoppingCartDao.modifyCartByVo(1L,1L, vo);

        System.out.print(ret);
    }
}
