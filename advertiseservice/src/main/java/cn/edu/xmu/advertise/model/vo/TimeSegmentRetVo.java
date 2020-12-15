package cn.edu.xmu.advertise.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "时间段返回视图对象")
public class TimeSegmentRetVo
{
    private Long id;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
