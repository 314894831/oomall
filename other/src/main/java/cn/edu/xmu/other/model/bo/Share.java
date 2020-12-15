package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.SharePo;
import cn.edu.xmu.other.model.vo.ShareVo.ShareRetVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Share implements VoObject, Serializable
{
    private Long id;
    private Long sharerId;
    private Long shareActivityId;
    private Long goodsSkuId;
    private Integer quantity;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    //构造函数
    public Share() {
    }
    public Share(SharePo po){
        this.id = po.getId();
        this.sharerId=po.getSharerId();
        this.goodsSkuId=po.getGoodsSkuId();
        this.shareActivityId=po.getShareActivityId();
        this.quantity=po.getQuantity();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new ShareRetVo(this);
    }
    @Override
    public Object createSimpleVo() {
        return new ShareRetVo(this);
    }
}
