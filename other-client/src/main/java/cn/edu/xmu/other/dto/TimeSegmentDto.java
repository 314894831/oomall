package cn.edu.xmu.other.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ChengYang Li
 */
@Data
public class TimeSegmentDto implements Serializable
{
    Long id;
    LocalDateTime beginTime;
    LocalDateTime endTime;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
}
