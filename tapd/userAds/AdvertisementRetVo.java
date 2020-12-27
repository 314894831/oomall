package cn.edu.xmu.other.model.vo.AdvertisementVo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author hp
 */
@Data
@ApiModel(description = "广告返回视图对象")
public class AdvertisementRetVo
{
    private Integer id;
    private String link;
    private String imagePath;
    private String content;
    private Integer segId;
    private Integer state;
    private String weight;
    private boolean beDefault;
    private LocalDate beginDate;
    private LocalDate endDate;
    private boolean repeat;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
