package cn.edu.xmu.other.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dao.TimeSegmentDao;
import cn.edu.xmu.other.model.bo.TimeSegment;
import cn.edu.xmu.other.model.po.TimeSegmentPo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ChengYang Li
 */
@Service
public class TimeSegmentService
{
    @Autowired
    TimeSegmentDao timeSegmentDao;

    /**
     * 平台管理员新增时间段
     * @author ChengYang Li
     * @param timeSegment
     */
    public ReturnObject addTime(TimeSegment timeSegment)
    {
        ReturnObject<TimeSegment> retObj = timeSegmentDao.insertTime(timeSegment);
        return retObj;
    }

    /**
     * 平台管理员获取时间段列表
     * @author ChengYang Li
     */
    public ReturnObject<PageInfo<VoObject>> getAllAdTime(Integer page, Integer pagesize, byte type)
    {
        PageHelper.startPage(page, pagesize);
        PageInfo<TimeSegmentPo> timeSegmentPos = timeSegmentDao.getAllAdTime(page, pagesize, type);

        List<VoObject> timeSegments = timeSegmentPos.getList().stream().map(TimeSegment::new).collect(Collectors.toList());

        PageInfo<VoObject> returnObject = new PageInfo<>(timeSegments);
        returnObject.setPages(timeSegmentPos.getPages());
        returnObject.setPageNum(timeSegmentPos.getPageNum());
        returnObject.setPageSize(timeSegmentPos.getPageSize());
        returnObject.setTotal(timeSegmentPos.getTotal());

        return new ReturnObject<>(returnObject);
    }

    /**
     * 根据ID删除时间段
     *
     * @param segid
     * @author ChengYang Li
     */
    public ReturnObject deleteTimeSegmentById(Long segid, byte type)
    {
        return timeSegmentDao.deleteTimeSegmentById(segid, type);
    }

    /**
     * 根据当前时间获得广告时间段id
     *
     * @author ChengYang Li
     */
    public Long getSegIdByTimeNow()
    {
        return timeSegmentDao.getSegIdByTimeNow();
    }

    /**
     * 判断当前广告时段是否存在
     *
     * @author ChengYang Li
     */
    public boolean segIdExist(Long segId)
    {
        return timeSegmentDao.segIdExist(segId);
    }
}
