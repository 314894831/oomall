package cn.edu.xmu.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@ApiModel(description = "传入的运单号")
public class LogSnVo
{
    @ApiModelProperty(name = "运单的信息", value = "logsn")
    private String logsn;
}
