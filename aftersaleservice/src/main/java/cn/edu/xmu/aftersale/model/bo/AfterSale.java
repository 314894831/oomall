package cn.edu.xmu.aftersale.model.bo;

import cn.edu.xmu.aftersale.model.po.AfterSalePo;
import cn.edu.xmu.aftersale.model.vo.AfterSaleRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 售后Bo类
 * @author 24320182203309 杨浩然
 * createdBy 杨浩然 2020/12/6 7:00
 * modifiedBy 杨浩然 2020/12/6 7:00
 **/
@Data
public class AfterSale implements VoObject, Serializable {
    private Long id;
    private Long orderItemId;
    private Long customerId;
    private Long shopId;
    private String serviceSn;
    private Byte type;
    private String reason;
    private String conclusion;
    private Long refund;
    private Integer quantity;
    private Long regionId;
    private String detail;
    private String consignee;
    private String mobile;
    private String customerLogSn;
    private String shopLogSn;
    private Byte state;
    private Byte beDeleted;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    //构造函数
    public AfterSale() {
    }
    public AfterSale(AfterSalePo po){
        this.id=po.getId();
        this.orderItemId=po.getOrderItemId();
        this.customerId=po.getCustomerId();
        this.shopId=po.getShopId();
        this.serviceSn=po.getServiceSn();
        this.type=po.getType();
        this.reason=po.getReason();
        this.conclusion=po.getConclusion();
        this.refund=po.getRefund();
        this.quantity=po.getQuantity();
        this.regionId=po.getRegionId();
        this.detail=po.getDetail();
        this.consignee=po.getConsignee();
        this.mobile=po.getMobile();
        this.customerLogSn=po.getCustomerLogSn();
        this.shopLogSn=po.getShopLogSn();
        this.state=po.getState();
        this.beDeleted=po.getBeDeleted();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }
    @Override
    public Object createVo() {
        return new AfterSaleRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return new AfterSaleRetVo(this);
    }
}
