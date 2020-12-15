package cn.edu.xmu.advertise.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.advertise.model.po.TimeSegmentPo;
import cn.edu.xmu.advertise.model.vo.TimeSegmentRetVo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeSegment implements VoObject
{
    private Long id;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Byte type;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public TimeSegment()
    {

    }

    public TimeSegment(TimeSegmentPo po)
    {
        this.id=po.getId();
        this.beginTime=po.getBeginTime();
        this.endTime=po.getEndTime();
        this.type=po.getType();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo()
    {
        TimeSegmentRetVo vo=new TimeSegmentRetVo();
        vo.setId(this.id);
        vo.setBeginTime(this.beginTime);
        vo.setEndTime(this.endTime);
        vo.setGmtCreate(this.gmtCreate);
        vo.setGmtModified(this.gmtModified);
        return vo;
    }

    @Override
    public Object createSimpleVo()
    {
        TimeSegmentPo po=new TimeSegmentPo();
        po.setId(this.id);
        po.setBeginTime(this.beginTime);
        po.setEndTime(this.endTime);
        po.setType(this.type);
        po.setGmtCreate(this.gmtCreate);
        po.setGmtModified(this.gmtModified);

        return po;
    }
}
