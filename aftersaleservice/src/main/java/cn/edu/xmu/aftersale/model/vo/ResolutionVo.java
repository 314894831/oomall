package cn.edu.xmu.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@ApiModel(description = "传入的运单号")
public class ResolutionVo
{

    @ApiModelProperty(name = "确认", value = "confirm")
    private boolean confirm;

    @ApiModelProperty(name = "结论", value = "conclusion")
    private String conclusion;
}
