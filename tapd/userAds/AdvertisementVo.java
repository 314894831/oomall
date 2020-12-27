package cn.edu.xmu.other.model.vo.AdvertisementVo;

import cn.edu.xmu.other.model.bo.Advertisement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author hp
 */
@Data
@ApiModel(description = "广告视图对象")
public class AdvertisementVo
{
    @NotBlank
    @ApiModelProperty(name = "广告内容", value = "content")
    private String content;

    @NotNull
    @ApiModelProperty(name = "广告权重", value = "weight")
    @Min(0)
    private Integer weight;

    @NotBlank
    @Pattern(regexp = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$",message = "日期格式不正确")
    @ApiModelProperty(name = "广告开始时间", value = "beginDate")
    private String beginDate;

    @NotBlank
    @Pattern(regexp = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$",message = "日期格式不正确")
    @ApiModelProperty(name = "广告结束时间", value = "endDate")
    private String endDate;

    @NotBlank
    @ApiModelProperty(name = "是否为每日重复的广告", value = "repeats")
    private String repeat;

    @NotBlank
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
        if(this.repeat=="true") {
            advertisement.setRepeats((byte)1);
        }
        else if(this.repeat=="false")
        {
            advertisement.setRepeats((byte)0);
        }
        advertisement.setLink(this.link);
        return advertisement;
    }

    /**
     * 判断开始时间是否大于结束时间
     * @return
     */
    public boolean isBeginTimeBiggerThanEndTime()
    {
        if(this.beginDate==null||this.endDate==null)
        {
            return false;
        }
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
