package cn.edu.xmu.other.model.vo.ShareVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "分享者分享商品链接")
public class ShareVo {
    @NotNull(message = "shareId不能为空")
    @ApiModelProperty(name = "分享者Id", value = "shareId")
    private Long shareId;
    @NotNull(message = "goodSkuId不能为空")
    @ApiModelProperty(name = "商品SkuId", value = "goodskpuId")
    private Long goodsSkuId;

}
