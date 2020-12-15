package cn.edu.xmu.share.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.share.model.po.*;
import cn.edu.xmu.share.model.vo.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分享Bo类
 * @author 24320182203309 杨浩然
 * createdBy 杨浩然 2020/12/1 17:00
 * modifiedBy 杨浩然 2020/12/1 17:00
 **/

@Data
public class GetShare implements VoObject, Serializable {
    private Long id;
    private Long sharerId;
    private Long shareActivityId;
    private Long goodsSpuId;
    private Integer quantity;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    //构造函数
    public GetShare() {
    }
    public GetShare(SharePo po){
        this.id = po.getId();
        this.sharerId=po.getSharerId();
        this.shareActivityId=po.getShareActivityId();
        this.goodsSpuId=po.getGoodsSpuId();
        this.quantity=po.getQuantity();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new GetShareRetVo(this);
    }
    @Override
    public Object createSimpleVo() {
        return new GetShareRetVo(this);
    }
}
