package cn.edu.xmu.address.model.vo;

import cn.edu.xmu.address.model.bo.Region;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 地区视图
 *
 * @author ChengYang Li
 **/
@Data
@ApiModel(description = "地区视图对象")
public class RegionVo
{
    @NotBlank(message = "地区名不能为空")
    @ApiModelProperty(value = "地区名称")
    private String name;

    @NotNull(message = "地区邮政编码不能为空")
    @ApiModelProperty(value = "地区邮政编码")
    private Long postalCode;
}
