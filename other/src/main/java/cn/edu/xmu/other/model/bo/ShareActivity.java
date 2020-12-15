package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.ShareActivityPo;
import cn.edu.xmu.other.model.vo.ShareVo.ShareActivityRetVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ShareActivity implements VoObject, Serializable
{
    private Long id;
    private Long shopId;
    private Long goodsSkuId;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String strategy;
    private Byte state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    //构造函数
    public ShareActivity() {
    }
    public ShareActivity(ShareActivityPo po){
        this.id = po.getId();
        this.shopId=po.getShopId();
        this.goodsSkuId=po.getGoodsSkuId();
        this.beginTime=po.getBeginTime();
        this.endTime=po.getEndTime();
        this.strategy=po.getStrategy();
        this.state=po.getState();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new ShareActivityRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return new ShareActivityRetVo(this);
    }
}
