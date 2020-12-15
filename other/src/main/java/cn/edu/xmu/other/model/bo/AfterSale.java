package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.AfterSalePo;
import cn.edu.xmu.other.model.vo.AfterSaleVo.AfterSaleRetVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 售后Bo类
 * @author 24320182203309 杨浩然
 * createdBy 杨浩然 2020/12/6 7:00
 * modifiedBy 杨浩然 2020/12/6 7:00
 **/
@Data
public class AfterSale implements VoObject, Serializable
{
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

    /**
     * 售后单状态
     */
    public enum State {
        ToAudit(0, "待管理员审核"),
        ToDeliverByCustomer(1, "待买家发货"),
        BeDeliveredByCustomer(2, "买家已发货"),
        ToRefundByShop(3, "待店家退款"),
        ToDeliverByShop(4, "待店家发货"),
        BeDeliveredByShop(5, "店家已发货"),
        DELET(6, "审核不通过"),
        DEL(7, "已取消"),
        DELE(8, "已结束");
        private static final Map<Integer, State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (AfterSale.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static AfterSale.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

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
