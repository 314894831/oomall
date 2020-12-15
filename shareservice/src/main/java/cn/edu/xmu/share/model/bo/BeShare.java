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
 * 分享成功Bo类
 * @author 24320182203309 杨浩然
 * createdBy 杨浩然 2020/12/5 17:00
 * modifiedBy 杨浩然 2020/12/5 17:00
 **/

@Data
public class BeShare implements VoObject, Serializable {
    private Long id;
    private Long sharerId;
    private Long shareId;
    private Long customerId;
    private Long orderItemId;
    private Integer rebate;
    private Long shareActivityId;
    private Long goodsSpuId;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    //构造函数
    public BeShare() {
    }
    public BeShare(BeSharePo po){
        this.id = po.getId();
        this.sharerId=po.getSharerId();
        this.customerId=po.getCustomerId();
        this.orderItemId=po.getOrderItemId();
        this.rebate=po.getRebate();
        this.shareActivityId=po.getShareActivityId();
        this.goodsSpuId=po.getGoodsSpuId();
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