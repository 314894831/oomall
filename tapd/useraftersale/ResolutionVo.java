package cn.edu.xmu.other.model.vo.AfterSaleVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "处理意见")
public class ResolutionVo
{
    @NotNull(message = "确认不能为空")
    @ApiModelProperty(name = "确认", value = "confirm")
    private Boolean confirm;

    @NotBlank(message = "结论不能为空")
    @ApiModelProperty(name = "结论", value = "conclusion")
    private String conclusion;

}
