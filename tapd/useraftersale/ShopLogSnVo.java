package cn.edu.xmu.other.model.vo.AfterSaleVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "传入的卖家运单号")
public class ShopLogSnVo
{
    @NotBlank(message = "运单信息不能为空")
    @ApiModelProperty(name = "运单的信息", value = "shopLogSn")
    private String shopLogSn;
}
