package cn.edu.xmu.other.dao;

import cn.edu.xmu.goods.client.IActivityService;
import cn.edu.xmu.goods.client.IGoodsService;
import cn.edu.xmu.goods.client.dubbo.CouponActivityDTO;
import cn.edu.xmu.goods.client.dubbo.PriceDTO;
import cn.edu.xmu.goods.client.dubbo.SkuDTO;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.mapper.ShoppingCartPoMapper;
import cn.edu.xmu.other.model.bo.AddToCart;
import cn.edu.xmu.other.model.bo.ShoppingCart;
import cn.edu.xmu.other.model.po.ShoppingCartPo;
import cn.edu.xmu.other.model.po.ShoppingCartPoExample;
import cn.edu.xmu.other.model.vo.ShoppingCartVo.CouponActivityRetVo;
import cn.edu.xmu.other.model.vo.ShoppingCartVo.ShoppingCartRetVo;
import cn.edu.xmu.other.model.vo.ShoppingCartVo.ShoppingCartVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @DubboReference(version = "0.0.1-SNAPSHOT")
    IGoodsService iGoodsService;

    @DubboReference(version = "0.0.1-SNAPSHOT")
    IActivityService iActivityService;

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
        criteria.andGoodsSkuIdEqualTo(vo.getGoodsSkuId());
        List<ShoppingCartPo> shoppingCartPoList = shoppingcartPoMapper.selectByExample(example);
        logger.info("getCartItem: userId = " + userId + "skuId=" + vo.getGoodsSkuId() + "is_exist = " + shoppingCartPoList.size());
        if (shoppingCartPoList.isEmpty()==false) {
            //修改库存操作
            shoppingCartPoList.get(0).setQuantity(shoppingCartPoList.get(0).getQuantity()+vo.getQuantity());
            shoppingCartPoList.get(0).setGmtModified(LocalDateTime.now());
            try {
                SkuDTO dto=iGoodsService.getSku(shoppingCartPoList.get(0).getGoodsSkuId());
                if(dto==null)
                {
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                }
                PriceDTO priceDTO=iGoodsService.getPrice(dto.getId());
                if(priceDTO==null)
                {
                    shoppingCartPoList.get(0).setPrice(dto.getOriginalPrice());
                }
                else
                {
                    shoppingCartPoList.get(0).setPrice(priceDTO.getPrePrice());
                }
                int ret = shoppingcartPoMapper.updateByPrimaryKeySelective(shoppingCartPoList.get(0));
                if (ret == 0) {
                    //插入失败
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                } else {
                    //TODO 通过skuID获得商品的名称和优惠券列表
                    List<CouponActivityRetVo> coupon=new ArrayList<>();
                    ShoppingCartRetVo vo1=new ShoppingCartRetVo(shoppingCartPoList.get(0));
                    List<CouponActivityDTO> couponDtos=iActivityService.getSkuCouponActivity(vo.getGoodsSkuId());
                    List<CouponActivityRetVo> couponList=new ArrayList<>();
                    for(CouponActivityDTO couponDto:couponDtos)
                    {
                        CouponActivityRetVo retVo=new CouponActivityRetVo(couponDto);
                        couponList.add(retVo);
                    }
                    vo1.setSkuName(dto.getName());
                    vo1.setPrice(shoppingCartPoList.get(0).getPrice());
                    //vo1.setSkuName("+");
                    vo1.setCouponActivity(coupon);
                    return new ReturnObject<>(vo1);
                }
            } catch (DataAccessException e) {
                // 数据库错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            } catch (Exception e) {
                // 其他Exception错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
            }
        } else {
            ShoppingCartPo cartPo = new ShoppingCartPo();
            cartPo.setCustomerId(userId);
            cartPo.setGoodsSkuId(vo.getGoodsSkuId());
            cartPo.setQuantity(vo.getQuantity());
            cartPo.setGmtCreate(LocalDateTime.now());
            cartPo.setGmtModified(null);

            //TODO 调用商品模块接口获取商品的价格
            SkuDTO dto=iGoodsService.getSku(cartPo.getGoodsSkuId());
            if(dto==null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            PriceDTO priceDTO=iGoodsService.getPrice(dto.getId());
            if(priceDTO==null)
            {
                cartPo.setPrice(dto.getOriginalPrice());
            }
            else
            {
                cartPo.setPrice(priceDTO.getPrePrice());
            }
            //cartPo.setPrice((long)1999);
            try {
                int ret = shoppingcartPoMapper.insert(cartPo);
                if (ret == 0) {
                    //插入失败
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                } else {
                    List<ShoppingCartPo> pos=shoppingcartPoMapper.selectByExample(example);
                    cartPo=pos.get(0);
                    ShoppingCartRetVo vo1=new ShoppingCartRetVo(cartPo);
                    //TODO 这里需要商品模块的接口来获取商品的优惠券活动
                    List<CouponActivityDTO> couponDtos=iActivityService.getSkuCouponActivity(vo1.getGoodsSkuId());
                    List<CouponActivityRetVo> couponList=new ArrayList<>();
                    for(CouponActivityDTO coupon:couponDtos)
                    {
                        CouponActivityRetVo retVo=new CouponActivityRetVo(coupon);
                        couponList.add(retVo);
                    }
                    vo1.setCouponActivity(couponList);
                    vo1.setSkuName(dto.getName());
                    vo1.setPrice(cartPo.getPrice());
                    //vo1.setSkuName("+");
                    vo1.setCouponActivity(couponList);

                    return new ReturnObject<>(vo1);
                }
            } catch (DataAccessException e) {
                // 数据库错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            } catch (Exception e) {
                // 其他Exception错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
            }
        }
    }
    /**
     * 获取所有购物车明细信息
     * @author yhr
     * @return List<ShoppingCartPo> 购物车明细列表
     */
    public ReturnObject<PageInfo<VoObject>> getCartItem(Integer pageNum, Integer pageSize, Long userId) {
        ShoppingCartPoExample example = new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria = example.createCriteria();
        if(userId>0) {
            criteria.andCustomerIdEqualTo(userId);
        }
        PageHelper.startPage(pageNum, pageSize);
        //TODO 调用接口获取优惠券列表
        List<ShoppingCartPo> cartItems = shoppingcartPoMapper.selectByExample(example);
        List<VoObject> rets=new ArrayList<>(cartItems.size());

        for(ShoppingCartPo po:cartItems)
        {
            ShoppingCart shoppingCart=new ShoppingCart(po);
            ShoppingCartRetVo vo= (ShoppingCartRetVo) shoppingCart.createSimpleVo();
            List<CouponActivityRetVo> coupons=new ArrayList<>();
            SkuDTO dto=iGoodsService.getSku(vo.getGoodsSkuId());
            if(dto==null)
            {
                continue;
            }
            vo.setSkuName(dto.getName());
            vo.setPrice(po.getPrice());
            List<CouponActivityDTO> couponDto=iActivityService.getSkuCouponActivity(vo.getGoodsSkuId());
            for(CouponActivityDTO dto1:couponDto)
            {
                CouponActivityRetVo retVo=new CouponActivityRetVo(dto1);
                coupons.add(retVo);
            }
            vo.setCouponActivity(coupons);

            //vo.setSkuName("+");

            rets.add(vo);
        }
        PageInfo<VoObject> shoppingCartPage = PageInfo.of(rets);
        shoppingCartPage.setPageNum(pageNum);
        shoppingCartPage.setPageSize(pageSize);
        shoppingCartPage.setTotal(PageInfo.of(cartItems).getTotal());
        shoppingCartPage.setPages(PageInfo.of(cartItems).getPages());
        logger.debug("getCartById: retCartItems = "+rets);
        return new ReturnObject<>(shoppingCartPage);
    }

    /**
     * 根据 id 修改购物车信息
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
        SkuDTO dto=iGoodsService.getSku(vo.getGoodsSkuId());
        PriceDTO priceDTO=iGoodsService.getPrice(dto.getId());
        if(priceDTO==null)
        {
            cartPo.setPrice(dto.getOriginalPrice());
        }
        else
        {
            cartPo.setPrice(priceDTO.getPrePrice());
        }
        //如果不相等判断是否属于同一个spu
        if(!isOneSpuId(cartPo.getGoodsSkuId(), vo.getGoodsSkuId()))
        {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        else
        {
            //如果属于同一个spuId，检查是否存在相同的skuId，如果存在则替换，不存在就正常修改
            ShoppingCartPoExample example1 = new ShoppingCartPoExample();
            ShoppingCartPoExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andIdNotEqualTo(cartPo.getId());
            criteria1.andCustomerIdEqualTo(customerId);
            criteria1.andGoodsSkuIdEqualTo(vo.getGoodsSkuId());
            List<ShoppingCartPo> skuPos=shoppingcartPoMapper.selectByExample(example1);
            if(!skuPos.isEmpty())
            {
                shoppingcartPoMapper.deleteByPrimaryKey(id);
                ShoppingCartPo skuPo=skuPos.get(0);
                skuPo.setGmtModified(LocalDateTime.now());
                skuPo.setQuantity(skuPo.getQuantity()+vo.getQuantity());
                skuPo.setPrice(cartPo.getPrice());
                shoppingcartPoMapper.updateByPrimaryKeySelective(skuPo);
                return new ReturnObject<>();
            }
        }

        //cartPo.setPrice(iGoodsService.getSku(vo.getGoodsSkuId()).getPrice());
        cartPo.setQuantity(vo.getQuantity());
        cartPo.setGoodsSkuId(vo.getGoodsSkuId());
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
        // 检查更新是否成功
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
        criteria.andIdEqualTo(id);
        List<ShoppingCartPo> pos1=shoppingcartPoMapper.selectByExample(example);
        if(pos1.size()==0)
        {
            logger.info("该商品不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该购物车id不存在"));
        }
        criteria.andCustomerIdEqualTo(customerId);
        //判断这条购物车记录是否是该买家的
        List<ShoppingCartPo> pos=shoppingcartPoMapper.selectByExample(example);
        if(pos.size()==0)
        {
            logger.info("该商品不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("该购物车id所属买家与操作用户不一致"));
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
            }
        }
        retObj = new ReturnObject(ResponseCode.OK);
        return retObj;
    }

    public void deleteCartByCustomerIdAndSkuId(Long customerId, Long skuId)
    {
        ShoppingCartPoExample example=new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria= example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andGoodsSkuIdEqualTo(skuId);
        shoppingcartPoMapper.deleteByExample(example);
    }

    /**
     * 判断两个skuId是否属于同一个spuId
     */
    public boolean isOneSpuId(Long skuId1, Long skuId2)
    {

        SkuDTO skuDTO1=iGoodsService.getSku(skuId1);
        SkuDTO skuDTO2=iGoodsService.getSku(skuId2);
        if(!skuDTO1.getGoodsSpuId().equals(skuDTO2.getGoodsSpuId()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
