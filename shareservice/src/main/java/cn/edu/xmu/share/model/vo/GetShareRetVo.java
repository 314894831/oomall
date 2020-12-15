package cn.edu.xmu.share.model.vo;
import cn.edu.xmu.share.model.bo.GetShare;
import cn.edu.xmu.share.model.bo.Share;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "买家查询所有分享记录")
public class GetShareRetVo {
    @ApiModelProperty(name = "分享链接的id", value = "id")
    private Long id;
    @ApiModelProperty(name = "分享者的id", value = "shareId")
    private Long shareId;
    @ApiModelProperty(name = "分享商品的SpuId", value = "goodsSpuId")
    private Long goodsSpuId;
    @ApiModelProperty(name = "成功返点的商品件数", value = "quantity")
    private Integer quantity;
    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private LocalDateTime gmtCreate;
    /**
     * 用Share对象建立Vo对象
     * @param share
     * @return ShareRetVo
     */
    public GetShareRetVo(GetShare share) {
        this.id=share.getId();
        this.shareId=share.getSharerId();
        this.goodsSpuId=share.getGoodsSpuId();
        this.quantity=share.getQuantity();
        this.gmtCreate=share.getGmtCreate();
    }

}