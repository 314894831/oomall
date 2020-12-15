package cn.edu.xmu.other.model.vo.TimeSegmentVo;

import cn.edu.xmu.other.model.bo.TimeSegment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@ApiModel(description = "时间段视图对象")
public class TimeSegmentVo
{
    @NotBlank(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @NotBlank(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    public TimeSegment createTimeSegment()
    {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TimeSegment timeSegment=new TimeSegment();
        timeSegment.setBeginTime(LocalDateTime.parse(this.beginTime,df));
        timeSegment.setEndTime(LocalDateTime.parse(this.endTime,df));
        return timeSegment;
    }

    //判断开始时间是否大于结束时间
    public boolean isBeginTimeBiggerThanEndTime()
    {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime=LocalDateTime.parse(this.beginTime,df);
        LocalDateTime endTime=LocalDateTime.parse(this.endTime,df);
        if(beginTime.isAfter(endTime))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
