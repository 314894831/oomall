package cn.edu.xmu.share.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.share.model.po.ShareActivityPo;
import cn.edu.xmu.share.model.po.SharePo;
import cn.edu.xmu.share.model.vo.ShareActivityRetVo;
import cn.edu.xmu.share.model.vo.ShareRetVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ShareActivity implements VoObject, Serializable {
    private Long id;
    private Long shopId;
    private Long goodsSpuId;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String strategy;
    private Byte beDeleted;
    private Byte state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    //构造函数
    public ShareActivity() {
    }
    public ShareActivity(ShareActivityPo po){
        this.id = po.getId();
        this.shopId=po.getShopId();
        this.goodsSpuId=po.getGoodsSpuId();
        this.beginTime=po.getBeginTime();
        this.endTime=po.getEndTime();
        this.strategy=po.getStrategy();
        this.beDeleted=po.getBeDeleted();
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
