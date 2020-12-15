package cn.edu.xmu.advertise.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ChangYang Li
 */
@Data
@ApiModel(description = "管理员审核意见视图对象")
public class AdvertisementExamVo
{
    @NotBlank(message = "必须输入审核结果")
    @ApiModelProperty(name = "审核结果", value = "conclusion")
    private String conclusion;

    @NotBlank(message = "必须输入审核附言")
    @ApiModelProperty(name = "审核附言", value = "message")
    private String message;
}
