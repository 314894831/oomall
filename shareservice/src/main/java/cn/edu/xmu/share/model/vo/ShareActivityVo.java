package cn.edu.xmu.share.model.vo;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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