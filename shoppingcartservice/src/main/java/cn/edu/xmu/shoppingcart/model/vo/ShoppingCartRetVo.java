package cn.edu.xmu.shoppingcart.model.vo;
import cn.edu.xmu.shoppingcart.model.bo.ShoppingCart;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "查询购物车返回信息")
public class ShoppingCartRetVo {

    @ApiModelProperty(name = "购物车明细id", value = "id")
    private Long id;

    @ApiModelProperty(name = "买家id", value = "customerId")
    private Long customerId;

    @ApiModelProperty(name = "商品SkuID", value = "goodSkuId")
    private Long goodSkuId;

    @ApiModelProperty(name = "商品规格名", value = "skuName")
    private String skuName;

    @ApiModelProperty(name = "商品名", value = "spuName")
    private String spuName;

    @ApiModelProperty(name = "添加数量", value = "quantity")
    private Integer quantity;

    @ApiModelProperty(name = "添加时间", value = "addTime")
    private String addTime;

    @ApiModelProperty(name = "加入单价", value = "price")
    private Long price;

    private CouponActivityRetVo couponActivity;

    /**
     * 用ShoppingCart对象建立Vo对象
     * @param cart cart
     * @return ShoppingCartRetVo
     */
    public ShoppingCartRetVo(ShoppingCart cart) {
        this.id = cart.getId();
        this.customerId = cart.getCustomerId();
        this.goodSkuId = cart.getGoodSkuId();
        this.skuName = cart.getSkuName();
        this.spuName = cart.getSpuName();
        this.quantity=cart.getQuantity();
        this.addTime=cart.getAddTime();
        this.price=cart.getPrice();
        this.couponActivity=cart.getCouponActivity();
    }
}