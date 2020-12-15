package cn.edu.xmu.other.model.vo.AfterSaleVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "传入的运单号")
public class LogSnVo
{
    @ApiModelProperty(name = "运单的信息", value = "logsn")
    private String logsn;
}
