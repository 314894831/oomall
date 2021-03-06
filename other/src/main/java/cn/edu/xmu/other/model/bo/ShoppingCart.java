package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.ShoppingCartPo;
import cn.edu.xmu.other.model.vo.ShoppingCartVo.CouponActivityRetVo;
import cn.edu.xmu.other.model.vo.ShoppingCartVo.ShoppingCartRetVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 购物车Bo类
 *
 * @author 24320182203309 杨浩然
 * createdBy 杨浩然 2020/12/1 17:00
 * modifiedBy 杨浩然 2020/12/1 17:00
 **/

@Data
public class ShoppingCart implements VoObject, Serializable
{
    private Long id;
    private Long customerId;
    private Long goodSkuId;
    private String skuName;
    private String spuName;
    private Integer quantity;
    private String addTime;
    private Long price;
    private CouponActivityRetVo couponActivity;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    //构造函数
    public ShoppingCart() {
    }
    public ShoppingCart(ShoppingCartPo po){
        this.id = po.getId();
        this.customerId=po.getCustomerId();
        this.goodSkuId = po.getGoodsSkuId();
        //this.skuName = po.getSkuName();?
        //this.spuName = po.getSpuName();?
        this.quantity = po.getQuantity();
        this.addTime = po.getGmtCreate().toString();
        this.price = po.getPrice();
        //this.couponActivity=po.getcouponActivity();?
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new ShoppingCartRetVo(this);
    }
    @Override
    public Object createSimpleVo() {
        return new ShoppingCartRetVo(this);
    }
}
