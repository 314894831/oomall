package cn.edu.xmu.advertise.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ChengYang Li
 */
@Data
@ApiModel(description = "管理员修改广告视图对象")
public class AdvertisementModifyVo
{
    @NotBlank(message = "必须输入广告内容")
    @ApiModelProperty(name = "审核结果", value = "conclusion")
    private String content;

    @NotNull(message = "必须输入时段ID")
    @ApiModelProperty(name = "审核附言", value = "message")
    private Long segId;

    @NotNull(message = "广告权重不能为空")
    @ApiModelProperty(name = "广告权重", value = "weight")
    private Integer weight;

    @NotBlank(message = "必须输入广告开始链接")
    @ApiModelProperty(name = "广告开始链接", value = "link")
    private String link;
}
