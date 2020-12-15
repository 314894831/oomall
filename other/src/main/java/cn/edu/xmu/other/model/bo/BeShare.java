package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.BeSharePo;
import cn.edu.xmu.other.model.vo.ShareVo.BeShareRetVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BeShare implements VoObject, Serializable
{
    private Long id;
    private Long sharerId;
    private Long shareId;
    private Long customerId;
    private Long orderId;
    private Integer rebate;
    private Long shareActivityId;
    private Long goodsSkuId;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    //构造函数
    public BeShare() {
    }
    public BeShare(BeSharePo po){
        this.id = po.getId();
        this.sharerId=po.getSharerId();
        this.customerId=po.getCustomerId();
        this.orderId=po.getOrderId();
        this.rebate=po.getRebate();
        this.shareActivityId=po.getShareActivityId();
        this.goodsSkuId=po.getGoodsSkuId();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new BeShareRetVo(this);
    }
    @Override
    public Object createSimpleVo() {
        return new BeShareRetVo(this);
    }
}
