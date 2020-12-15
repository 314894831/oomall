package cn.edu.xmu.other.impl;

import cn.edu.xmu.other.dto.TimeSegmentDto;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * @author ChengYang Li
 */
@DubboService(version = "1.0.0")
public interface ITimeSegmentService
{
    /**
     * 获取所有秒杀活动的时段
     */
    public List<TimeSegmentDto> getAllFlashTimeSegment();
}
