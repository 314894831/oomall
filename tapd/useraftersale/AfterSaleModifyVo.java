package cn.edu.xmu.other.model.vo.AfterSaleVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@ApiModel(description = "买家修改售后单")
public class AfterSaleModifyVo {

    @Min(1)
    @ApiModelProperty(name = "添加数量", value = "quantity")
    private Integer quantity;

    @ApiModelProperty(name = "售后申请理由", value = "reason")
    private String reason;

    @ApiModelProperty(name = "地区Id", value = "regionId")
    private Long regionId;

    @ApiModelProperty(name = "详细地址", value = "detail")
    private String detail;

    @ApiModelProperty(name = "收货人", value = "consignee")
    private String consignee;

    @Pattern(regexp = "[+]?1[0-9]{10}$", message = "手机号格式不正确")
    @ApiModelProperty(name = "手机号", value = "mobile")
    private String mobile;
}
