package cn.edu.xmu.advertise.model.vo;

import cn.edu.xmu.advertise.model.bo.Advertisement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.swing.text.DateFormatter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hp
 */
@Data
@ApiModel(description = "在广告时段下新建广告视图对象")
public class AdvertisementVo
{
    @NotBlank(message = "必须输入广告内容")
    @ApiModelProperty(name = "广告内容", value = "content")
    private String content;

    @NotNull(message = "广告权重不能为空")
    @ApiModelProperty(name = "广告权重", value = "weight")
    private Integer weight;

    @NotBlank(message = "必须输入广告开始时间")
    @ApiModelProperty(name = "广告开始时间", value = "beginDate")
    private String beginDate;

    @NotBlank(message = "必须输入广告结束时间")
    @ApiModelProperty(name = "广告结束时间", value = "endDate")
    private String endDate;

    @NotNull(message = "必须为广告设定是否重复")
    @ApiModelProperty(name = "是否为每日重复的广告", value = "repeats")
    private Byte repeat;

    @NotBlank(message = "必须输入广告开始链接")
    @ApiModelProperty(name = "广告开始链接", value = "link")
    private String link;

    public Advertisement createAdvertisement()
    {
        LocalDate beginDate =
                LocalDate.parse(this.beginDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate =
                LocalDate.parse(this.endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Advertisement advertisement=new Advertisement();
        advertisement.setBeginDate(beginDate);
        advertisement.setEndDate(endDate);
        advertisement.setContent(this.content);
        advertisement.setWeight(this.weight);
        advertisement.setRepeats(this.repeat);
        advertisement.setLink(this.link);
        return advertisement;
    }

    //判断开始时间是否大于结束时间
    public boolean isBeginTimeBiggerThanEndTime()
    {
        LocalDate beginDate =
                LocalDate.parse(this.beginDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate =
                LocalDate.parse(this.endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(beginDate.isAfter(endDate))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
