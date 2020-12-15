package cn.edu.xmu.share.model.vo;
import cn.edu.xmu.share.model.bo.GetShare;
import cn.edu.xmu.share.model.bo.ShareActivity;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
@Data
@ApiModel(description = "创建分享活动")
public class ShareActivityRetVo {
    @ApiModelProperty(name = "分享活动的id", value = "id")
    private Long id;
    @ApiModelProperty(name = "店铺的id", value = "shopId")
    private Long shopId;
    @ApiModelProperty(name = "分享商品的SpuId", value = "goodsSpuId")
    private Long goodsSpuId;
    @ApiModelProperty(name = "开始时间", value = "beginTime")
    private LocalDateTime beginTime;
    @ApiModelProperty(name = "结束时间", value = "endTime")
    private LocalDateTime endTime;
    @ApiModelProperty(name = "状态", value = "state")
    private Byte state;
    /**
     * 用ShareActivity对象建立Vo对象
     * @param shareActivity
     * @return ShareActivityRetVo
     */
    public ShareActivityRetVo(ShareActivity shareActivity) {
        this.id=shareActivity.getId();
        this.shopId=shareActivity.getShopId();
        this.goodsSpuId=shareActivity.getGoodsSpuId();
        this.beginTime=shareActivity.getBeginTime();
        this.endTime=shareActivity.getEndTime();
        this.state=shareActivity.getState();
    }
}
