package cn.edu.xmu.other.model.vo.AfterSaleVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@ApiModel(description = "买家提交售后单")
public class AfterSaleVo {
    @NotNull(message = "type不能为空")
    @ApiModelProperty(name = "售后类型", value = "type")
    private Byte type;

    @NotNull(message = "数量不能为空，需要大于0")
    @Min(1)
    @ApiModelProperty(name = "添加数量", value = "quantity")
    private Integer quantity;

    @ApiModelProperty(name = "售后申请理由", value = "reason")
    private String reason;

    @NotNull(message = "regionId不能为空")
    @ApiModelProperty(name = "地区Id", value = "regionId")
    private Long regionId;

    @NotBlank(message = "detail不能为空")
    @ApiModelProperty(name = "详细地址", value = "detail")
    private String detail;

    @NotBlank(message = "consignee不能为空")
    @ApiModelProperty(name = "收货人", value = "consignee")
    private String consignee;

    @NotBlank
    @Pattern(regexp = "[+]?1[0-9]{10}$", message = "手机号格式不正确")
    @ApiModelProperty(name = "手机号", value = "mobile")
    private String mobile;
}
