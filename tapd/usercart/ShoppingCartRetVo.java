package cn.edu.xmu.other.model.vo.ShoppingCartVo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.ShoppingCartPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ChengYang Li
 */
@Data
@ApiModel(description = "查询购物车返回信息")
public class ShoppingCartRetVo implements VoObject
{

    @ApiModelProperty(name = "购物车明细id", value = "id")
    private Long id;

    @ApiModelProperty(name = "商品SkuID", value = "goodSkuId")
    private Long goodsSkuId;

    @ApiModelProperty(name = "商品规格名", value = "skuName")
    private String skuName;

    @ApiModelProperty(name = "添加数量", value = "quantity")
    private Integer quantity;

    @ApiModelProperty(name = "加入单价", value = "price")
    private Long price;

    @ApiModelProperty(name = "加入时间", value = "gmtCreate")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(name = "修改时间", value = "gmtModified")
    private LocalDateTime gmtModified;

    private List<CouponActivityRetVo> couponActivity;

    public ShoppingCartRetVo()
    {

    }

    public ShoppingCartRetVo(ShoppingCartPo po)
    {
        this.id=po.getId();
        this.goodsSkuId=po.getGoodsSkuId();
        this.quantity=po.getQuantity();
        this.price=po.getPrice();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo()
    {
        ShoppingCartRetVo vo=new ShoppingCartRetVo();
        vo.setId(this.id);
        vo.setGoodsSkuId(this.goodsSkuId);
        vo.setSkuName(this.skuName);
        vo.setQuantity(this.quantity);
        vo.setPrice(this.price);
        vo.setGmtCreate(this.gmtCreate);
        vo.setGmtModified(this.gmtModified);
        vo.setCouponActivity(this.couponActivity);
        return vo;
    }

    @Override
    public Object createSimpleVo()
    {
        return null;
    }
}
