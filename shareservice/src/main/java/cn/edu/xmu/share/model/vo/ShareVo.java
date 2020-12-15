package cn.edu.xmu.share.model.vo;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "分享者分享商品链接")
public class ShareVo {
    @NotNull(message = "shareId不能为空")
    @ApiModelProperty(name = "分享者Id", value = "shareId")
    private Long shareId;
    @NotNull(message = "goodSpuId不能为空")
    @ApiModelProperty(name = "商品SpuId", value = "goodsSpuId")
    private Long goodsSpuId;

}

