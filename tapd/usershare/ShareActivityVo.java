package cn.edu.xmu.other.model.vo.ShareVo;

import cn.edu.xmu.other.model.bo.ShareActivity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "管理员或店家创建分享活动")
public class ShareActivityVo {
    @NotBlank(message = "beginTime不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(name = "开始时间", value = "beginTime")
    private String beginTime;
    @NotBlank(message = "endTime不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(name = "结束时间", value = "endTime")
    private String endTime;
    @NotBlank(message = "strategy不能为空")
    @ApiModelProperty(name = "分享活动规则", value = "strategy")
    private String strategy;

}
