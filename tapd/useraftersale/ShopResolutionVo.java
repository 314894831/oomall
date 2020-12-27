package cn.edu.xmu.other.model.vo.AfterSaleVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "店家处理意见")
public class ShopResolutionVo
{
    @NotNull(message = "确认不能为空")
    @ApiModelProperty(name = "确认", value = "confirm")
    private Boolean confirm;

    @ApiModelProperty(name = "结论", value = "conclusion")
    private String conclusion;
    @NotNull(message = "退款金额不能为空")
    @ApiModelProperty(name = "退款支付金额", value = "price")
    private Long price;
    @NotNull(message = "售后方式不能为空")
    @ApiModelProperty(name = "售后方式", value = "type")
    private Byte type;
}