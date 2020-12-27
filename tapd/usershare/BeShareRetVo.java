package cn.edu.xmu.other.model.vo.ShareVo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.bo.BeShare;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.dubbo.config.annotation.DubboReference;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "查看所有分享成功记录")
public class BeShareRetVo implements VoObject {
    @ApiModelProperty(name = "分享成功明细id", value = "id")
    private Long id;

    SkuGoodsRetVo sku;

    @ApiModelProperty(name = "分享者id", value = "sharerId")
    private Long sharerId;

    @ApiModelProperty(name = "顾客id", value = "customerId")
    private Long customerId;

    @ApiModelProperty(name = "订单id", value = "orderId")
    private Long orderId;

    @ApiModelProperty(name = "返点", value = "rebate")
    private Integer rebate;

    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private LocalDateTime gmtCreate;

    public BeShareRetVo(){}
    /**
     * 用BeShare对象建立Vo对象
     * @param beShare
     * @return ShareRetVo
     */
    public BeShareRetVo(BeShare beShare) {
        this.customerId=beShare.getCustomerId();
        this.gmtCreate=beShare.getGmtCreate();
        this.id=beShare.getId();
        this.orderId=beShare.getOrderId();
        this.rebate=beShare.getRebate();
        this.sharerId=beShare.getSharerId();
        //用DTO 生成
        this.sku=beShare.getSkuGoodsRetVo();
    }

    @Override
    public Object createVo() {
        BeShareRetVo vo=new BeShareRetVo();
        vo.setId(this.id);
        vo.setSharerId(this.sharerId);
        vo.setGmtCreate(this.gmtCreate);
        vo.setCustomerId(this.customerId);
        vo.setOrderId(this.orderId);
        vo.setRebate(this.rebate);
        vo.setSku(this.sku);
        return vo;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
