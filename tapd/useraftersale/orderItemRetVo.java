package cn.edu.xmu.other.model.vo.AfterSaleVo;

import lombok.Data;

@Data
public class orderItemRetVo {
    private String orderSn;
    private Long orderId;
    private Long skuId;
    private String skuName;
    private String shopId;
}
