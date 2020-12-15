package cn.edu.xmu.shoppingcart.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.shoppingcart.model.po.ShoppingCartPo;
import cn.edu.xmu.shoppingcart.model.vo.AddShoppingCartRetVo;
import cn.edu.xmu.shoppingcart.model.vo.CouponActivityRetVo;
import cn.edu.xmu.shoppingcart.model.vo.ShoppingCartRetVo;
import cn.edu.xmu.shoppingcart.model.vo.ShoppingCartVo;
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
public class AddToCart implements VoObject, Serializable {
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
    public AddToCart() {
    }
    public AddToCart(ShoppingCartPo po){
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
        return new AddShoppingCartRetVo(this);
    }
    @Override
    public Object createSimpleVo() {
        return new AddShoppingCartRetVo(this);
    }
}