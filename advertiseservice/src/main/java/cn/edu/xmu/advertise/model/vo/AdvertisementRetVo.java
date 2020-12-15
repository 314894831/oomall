package cn.edu.xmu.advertise.model.vo;

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
    private Long id;
    private String link;
    private String imagePath;
    private String content;
    private Long segId;
    private Byte state;
    private Integer weight;
    private Byte beDefault;
    private LocalDate beginDate;
    private LocalDate endDate;
    private Byte repeats;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
