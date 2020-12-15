package cn.edu.xmu.other.model.vo.ShareVo;

import cn.edu.xmu.other.model.bo.Share;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "分享者生成分享链接")
public class ShareRetVo {
    //@ApiModelProperty(name = "分享链接的URL", value = "data")
    //private String data;
    @ApiModelProperty(name = "分享链接的id", value = "id")
    private Long id;
    @ApiModelProperty(name = "分享者的id", value = "sharerId")
    private Long sharerId;
    @ApiModelProperty(name = "成功返点的商品件数", value = "quantity")
    private Integer quantity;
    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private LocalDateTime gmtCreate;

    private SkuIdRetVo skuVo;

    /**
     * 用Share对象建立Vo对象
     * @param share
     * @return ShareRetVo
     */
    public ShareRetVo(Share share) {
        this.id=share.getId();
        this.sharerId=share.getSharerId();
        //TODO 设置this.skuVo=;
        this.quantity=share.getQuantity();
        this.gmtCreate=share.getGmtCreate();
       // this.data="/share/"+share.getId()+"/spus/"+share.getGoodsSpuId();
    }

}
