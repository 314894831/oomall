package cn.edu.xmu.other.model.vo.ShareVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "管理员或店家创建分享活动")
public class ShareActivityVo {
    @NotNull(message = "beginTime不能为空")
    @ApiModelProperty(name = "开始时间", value = "beginTime")
    private String beginTime;
    @NotNull(message = "endTime不能为空")
    @ApiModelProperty(name = "结束时间", value = "endTime")
    private String endTime;
    @NotNull(message = "strategy不能为空")
    @ApiModelProperty(name = "分享活动规则", value = "strategy")
    private String strategy;
}
