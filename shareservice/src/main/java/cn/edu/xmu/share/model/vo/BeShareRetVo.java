package cn.edu.xmu.share.model.vo;
import cn.edu.xmu.share.model.bo.BeShare;
import cn.edu.xmu.share.model.bo.Share;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "买家查看所有分享成功")
public class BeShareRetVo {
    @ApiModelProperty(name = "分享成功明细id", value = "id")
    private Long id;

    @ApiModelProperty(name = "商品goodsSpuId", value = "goodsSpuId")
    private Long goodsSpuId;

    @ApiModelProperty(name = "分享明细id", value = "shareId")
    private Long shareId;

    @ApiModelProperty(name = "顾客id", value = "customerId")
    private Long customerId;

    @ApiModelProperty(name = "订单id", value = "orderId")
    private Long orderId;

    @ApiModelProperty(name = "返点", value = "rebate")
    private Integer rebate;

    @ApiModelProperty(name = "订单id", value = "gmtCreate")
    private LocalDateTime gmtCreate;
    /**
     * 用BeShare对象建立Vo对象
     * @param beShare
     * @return ShareRetVo
     */
    public BeShareRetVo(BeShare beShare) {
        this.customerId=beShare.getCustomerId();
        this.gmtCreate=beShare.getGmtCreate();
        this.goodsSpuId=beShare.getGoodsSpuId();
        this.id=beShare.getId();
        this.orderId=beShare.getOrderItemId();
        this.rebate=beShare.getRebate();
        this.shareId=beShare.getShareId();
    }

}
