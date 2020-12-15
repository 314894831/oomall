package cn.edu.xmu.shoppingcart.dao;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.shoppingcart.mapper.ShoppingCartPoMapper;
import cn.edu.xmu.shoppingcart.model.po.*;
import cn.edu.xmu.shoppingcart.model.bo.*;
import cn.edu.xmu.shoppingcart.model.vo.ShoppingCartVo;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 杨浩然
 * @date Created in 2020/12/1 17:08
 * Modified by 24320182203309 杨浩然 at 2020/12/1 18:00
 **/
@Repository
public class ShoppingCartDao {
    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartDao.class);

    @Autowired
    private ShoppingCartPoMapper shoppingcartPoMapper;

    /**
     * 由 vo和user Id 添加商品到购物车
     * @param  vo, userid
     * @return ShoppingCartRetVo
     * created by 杨浩然 24320182203309
     */
    public ReturnObject<VoObject> addToShoppingCart(ShoppingCartVo vo, Long userId) {
        //查找是否已经存在相同的userid与skuId的组合。
        ShoppingCartPoExample example = new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        criteria.andGoodsSkuIdEqualTo(vo.getGoodSkuID());
        List<ShoppingCartPo> shoppingCartPoList = shoppingcartPoMapper.selectByExample(example);
        logger.info("getCartItem: userId = " + userId + "skuId=" + vo.getGoodSkuID() + "is_exist = " + shoppingCartPoList.size());
        if (shoppingCartPoList.isEmpty()==false) {
        //修改库存操作
            shoppingCartPoList.get(0).setQuantity(shoppingCartPoList.get(0).getQuantity()+vo.getQuantity());
            shoppingCartPoList.get(0).setGmtModified(LocalDateTime.now());
            try {
                int ret = shoppingcartPoMapper.updateByPrimaryKeySelective(shoppingCartPoList.get(0));
                if (ret == 0) {
                    //插入失败
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                } else {
                    //插入成功
                }
            } catch (DataAccessException e) {
                // 数据库错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            } catch (Exception e) {
                // 其他Exception错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
            }
            AddToCart cart = new AddToCart(shoppingCartPoList.get(0));
            return new ReturnObject<VoObject>(cart);
        } else {
            ShoppingCartPo cartPo = new ShoppingCartPo();
            cartPo.setCustomerId(userId);
            cartPo.setGoodsSkuId(vo.getGoodSkuID());
            cartPo.setQuantity(vo.getQuantity());
            cartPo.setGmtCreate(LocalDateTime.now());

            //这里需要商品模块的接口来获取商品的价格和优惠券活动

            cartPo.setPrice((long) 10);
            try {
                int ret = shoppingcartPoMapper.insert(cartPo);
                if (ret == 0) {
                    //插入失败
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                } else {
                    //插入成功
                }
            } catch (DataAccessException e) {
                // 数据库错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            } catch (Exception e) {
                // 其他Exception错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
            }
            AddToCart cart = new AddToCart(cartPo);
            return new ReturnObject<VoObject>(cart);
        }
    }
    /**
     * 获取所有购物车明细信息
     * @author yhr
     * @return List<ShoppingCartPo> 购物车明细列表
     */
    public PageInfo<ShoppingCartPo> getCartItem(Integer page, Integer pagesize,Long userId) {
        ShoppingCartPoExample example = new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria = example.createCriteria();
        if(userId>0) {
            criteria.andCustomerIdEqualTo(userId);
        }
        List<ShoppingCartPo> cartItems = shoppingcartPoMapper.selectByExample(example);
        logger.debug("getCartById: retCartItems = "+cartItems);
        return new PageInfo<>(cartItems);
    }

    /**根据 id 修改购物车信息
     * @param vo 传入的 cartVo 对象
     * @return 返回对象 ReturnObj
     * @author 24320182203309 yhr
     * Created at 2020/12/2 20:30
     * Modified by 24320182203309 yhr at 2020/12/2 22:42
     */
    public ReturnObject<Object> modifyCartByVo(Long customerId, Long id, ShoppingCartVo vo) {
        ShoppingCartPoExample example = new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andIdEqualTo(id);
        //判断这条购物车记录是否是该买家的
        List<ShoppingCartPo> pos=shoppingcartPoMapper.selectByExample(example);
        if(pos.size()==0)
        {
            logger.info("该商品不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        ShoppingCartPo cartPo= pos.get(0);
        cartPo.setQuantity(vo.getQuantity());
        cartPo.setGoodsSkuId(vo.getGoodSkuID());
        cartPo.setGmtModified(LocalDateTime.now());
        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = shoppingcartPoMapper.updateByPrimaryKey(cartPo);
        } catch (DataAccessException e) {
                // 其他情况属未知错误
                logger.error("数据库错误：" + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                        String.format("发生了严重的数据库错误：%s", e.getMessage()));
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("用户不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("用户 id = " + id + " 的资料已更新");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
    (物理) 删除购物车明细
     * @param id 购物车明细 id
     * @return 返回对象 ReturnObj
     * @author 24320182203309 yhr
     */
    public ReturnObject<Object> deleteItem(Long customerId, Long id) {
        ReturnObject<Object> retObj;
        ShoppingCartPoExample example = new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andIdEqualTo(id);
        //判断这条购物车记录是否是该买家的
        List<ShoppingCartPo> pos=shoppingcartPoMapper.selectByExample(example);
        if(pos.size()==0)
        {
            logger.info("该商品不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        //这条购物车记录是该买家的再进行删除
        int ret = shoppingcartPoMapper.deleteByPrimaryKey(id);
        if (ret == 0) {
            logger.info("该商品不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("购物车明细id = " + id + " 已被成功删除");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /** (物理) 删除用户购物车
     * @param userId userId
     * @return 返回对象 ReturnObj
     * @author 24320182203309 yhr
     */
    public ReturnObject deleteCart(Long userId) {
        ReturnObject retObj = null;
        int ret = 0;
        ShoppingCartPoExample example = new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        List<ShoppingCartPo> shoppingCartPoList = shoppingcartPoMapper.selectByExample(example);
        for( ShoppingCartPo po:shoppingCartPoList){
            Long cartId=po.getId();
         ret = shoppingcartPoMapper.deleteByPrimaryKey(cartId);
            if (ret == 0) {
                logger.info("该用户购物车不存在或已被删除：id = " + userId);
                retObj = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
                return retObj;
            } else {
                logger.info("用户Id= " + userId + "购物车明细id为"+cartId+" 的购物车明细已被成功删除");
                retObj = new ReturnObject();
            }
        }
        return retObj;
    }
}
