package cn.edu.xmu.shoppingcart.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.shoppingcart.dao.ShoppingCartDao;
import cn.edu.xmu.shoppingcart.model.bo.ShoppingCart;
import cn.edu.xmu.shoppingcart.model.po.ShoppingCartPo;
import cn.edu.xmu.shoppingcart.model.vo.ShoppingCartVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务类
 *
 * @author 24320182203309 杨浩然
 * createdBy yhr 2020/12/1 13:57
 * modifiedBy yhr 2020/12/1 19:20
 **/
@Service
public class ShoppingCartService {
    @Autowired
    ShoppingCartDao shoppingCartDao;

    /**
     * 买家将商品加入购物车
     * @param userId 用户id
     * @param vo 传入vo
     * createdBy 杨浩然 24320182203309
     */
    @Transactional
    public ReturnObject<VoObject> addToCart(ShoppingCartVo vo, Long userId){
        //新增
        ReturnObject<VoObject> ret = shoppingCartDao.addToShoppingCart(vo, userId);
        return ret;
    }

    /**
     * 获取所有用户信息
     * @author yhr
     * @param userId
     * @param page
     * @param pagesize
     * @return 购物车列表
     */
    public ReturnObject<PageInfo<VoObject>> getCart(Integer page, Integer pagesize,Long userId) {
        PageHelper.startPage(page, pagesize);
        PageInfo<ShoppingCartPo> cartPos = shoppingCartDao.getCartItem( page, pagesize,userId);
        List<VoObject> cartItems = cartPos.getList().stream().map(ShoppingCart::new).collect(Collectors.toList());
        PageInfo<VoObject> returnObject = new PageInfo<>(cartItems);
        returnObject.setPages(cartPos.getPages());
        returnObject.setPageNum(cartPos.getPageNum());
        returnObject.setPageSize(cartPos.getPageSize());
        returnObject.setTotal(cartPos.getTotal());
        return new ReturnObject<>(returnObject);
    }
    /**
     * 根据 ID 和 ShoppingCartVo 修改购物车信息
     * @param id 购物车明细 id
     * @param vo ShoppingCartVo 对象
     * @return 返回对象 ReturnObject
     * @author 24320182203309 yhr
     * Created at 2020/12/2 20:30
     * Modified by 24320182203309 yhr at 2020/12/2 22:42
     */
    @Transactional
    public ReturnObject<Object> modifyCart(Long customerId, Long id, ShoppingCartVo vo) {
        return shoppingCartDao.modifyCartByVo(customerId, id, vo);
    }

    /**
     *  根据 id 买家删除自己一条购物车记录
     * @param id 购物车明细 id
     * @return 返回对象 ReturnObject
     * @author 24320182203309 yhr
     */
    @Transactional
    public ReturnObject<Object> deleteItem(Long customerId, Long id) {
        // 注：逻辑删除
        return shoppingCartDao.deleteItem(customerId, id);
    }
    /**
     *  根据 id 买家清空购物车
     * @return 返回对象 ReturnObject
     * @author 24320182203309 yhr
     */
    @Transactional
    public ReturnObject deleteCart(Long userId) {
        return shoppingCartDao.deleteCart(userId);
    }
}
