package cn.edu.xmu.other.model.vo.AfterSaleVo;

import cn.edu.xmu.other.model.bo.AfterSale;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "售后单返回信息")
public class AfterSaleRetVo {
    @ApiModelProperty(name = "售后单id", value = "id")
    private Long id;

    @ApiModelProperty(name = "订单id", value = "orderId")
    private Long orderId;

    @ApiModelProperty(name = "订单编号", value = "orderSn")
    private String orderSn;

    @ApiModelProperty(name = "订单明细Id", value = "orderItemId")
    private Long orderItemId;

    @ApiModelProperty(name = "商品规格id", value = "skuId")
    private Long skuId;

    @ApiModelProperty(name = "商品规格名", value = "skuName")
    private String skuName;

    @ApiModelProperty(name = "买家id", value = "customerId")
    private Long customerId;

    @ApiModelProperty(name = "店铺id", value = "shopId")
    private Long shopId;

    @ApiModelProperty(name = "服务编号", value = "serviceSn")
    private String serviceSn;

    @ApiModelProperty(name = "售后类型", value = "type")
    private Byte type;

    @ApiModelProperty(name = "售后申请理由", value = "reason")
    private String reason;

    @ApiModelProperty(name = "退款", value = "refund")
    private Long refund;

    @ApiModelProperty(name = "数量", value = "quantity")
    private Integer quantity;

    @ApiModelProperty(name = "地区id", value = "regionId")
    private Long regionId;

    @ApiModelProperty(name = "详细地址", value = "detail")
    private String detail;

    @ApiModelProperty(name = "收货人", value = "consignee")
    private String consignee;

    @ApiModelProperty(name = "手机号", value = "mobile")
    private String mobile;

    @ApiModelProperty(name = "买家Log编号", value = "customerLogSn")
    private String customerLogSn;

    @ApiModelProperty(name = "店铺Log编号", value = "shopLogSn")
    private String shopLogSn;

    @ApiModelProperty(name = "售后单状态", value = "state")
    private Byte state;
    /**
     * 用AfterSale对象建立Vo对象
     * @param afterSale
     * @return AfterSaleRetVo
     */
    public AfterSaleRetVo(AfterSale afterSale) {
        this.id = afterSale.getId();
        this.orderItemId=afterSale.getOrderItemId();
        this.customerId=afterSale.getCustomerId();
        this.shopId=afterSale.getShopId();
        this.serviceSn=afterSale.getServiceSn();
        this.type=afterSale.getType();
        this.reason=afterSale.getReason();
        this.refund=afterSale.getRefund();
        this.quantity=afterSale.getQuantity();
        this.regionId=afterSale.getRegionId();
        this.detail=afterSale.getDetail();
        this.consignee=afterSale.getConsignee();
        this.mobile=afterSale.getMobile();
        this.customerLogSn=afterSale.getCustomerLogSn();
        this.shopLogSn=afterSale.getShopLogSn();
        this.state=afterSale.getState();
        this.orderSn="12345";
        this.orderId=0L;
        this.skuId=0L;
        this.skuName="?";
        this.shopId=0L;
        this.serviceSn="12345";
        this.refund=1L;
        this.customerLogSn="12345";
        this.shopLogSn="12345";
        this.state=0;
    }
}
