package cn.edu.xmu.other.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.mapper.TimeSegmentPoMapper;
import cn.edu.xmu.other.model.bo.TimeSegment;
import cn.edu.xmu.other.model.po.TimeSegmentPo;
import cn.edu.xmu.other.model.po.TimeSegmentPoExample;
import cn.edu.xmu.other.service.AdvertisementService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ChengYang Li
 */
@Repository
public class TimeSegmentDao
{
    private static final Logger logger = LoggerFactory.getLogger(cn.edu.xmu.other.dao.TimeSegmentDao.class);

    @Autowired
    TimeSegmentPoMapper timeSegmentPoMapper;

    @Autowired
    AdvertisementService advertisementService;

    /**
     * 平台管理员新增时间段
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject<TimeSegment> insertTime(TimeSegment timeSegment)
    {
        TimeSegmentPo timeSegmentPo= (TimeSegmentPo) timeSegment.createSimpleVo();
        ReturnObject<TimeSegment> retObj = null;
        try{
            //新增时间段之前先检查同类时间段是否有时间冲突
            TimeSegmentPoExample example1 = new TimeSegmentPoExample();
            TimeSegmentPoExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andTypeEqualTo(timeSegment.getType());
            //要增加的时间段的开始时间大于已存在的某时间段的开始时间并小于结束时间或者等于开始时间时，则时间冲突
            criteria1.andBeginTimeLessThanOrEqualTo(timeSegment.getBeginTime());
            criteria1.andEndTimeGreaterThan(timeSegment.getBeginTime());
            List<TimeSegmentPo> timeSegments1=timeSegmentPoMapper.selectByExample(example1);
            if(timeSegments1.size()!=0)
            {
                logger.debug("addTime: insert time fail : 时间段冲突");
                return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT);
            }

            TimeSegmentPoExample example2 = new TimeSegmentPoExample();
            TimeSegmentPoExample.Criteria criteria2 = example2.createCriteria();
            criteria2.andTypeEqualTo(timeSegment.getType());
            //要增加的时间段的结束时间大于已存在的某时间段的开始时间并小于结束时间，则时间冲突
            criteria2.andBeginTimeLessThan(timeSegment.getEndTime());
            criteria2.andEndTimeGreaterThanOrEqualTo(timeSegment.getEndTime());
            List<TimeSegmentPo> timeSegments2=timeSegmentPoMapper.selectByExample(example2);
            if(timeSegments2.size()!=0)
            {
                logger.debug("addTime: insert time fail : 时间段冲突");
                return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT, String.format("时段冲突"));
            }
            //更新数据库
            int ret = timeSegmentPoMapper.insertSelective(timeSegmentPo);
            if (ret == 0) {
                //插入失败
                logger.debug("addTime: insert time fail " + timeSegmentPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增时间段失败" ));
            } else {
                //插入成功
                logger.debug("addTime: insert time = " + timeSegmentPo.toString());
                timeSegment.setId(timeSegmentPo.getId());
                retObj = new ReturnObject<>(timeSegment);
            }
        }
        catch (DataAccessException e) {
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 平台管理员获取广告时间段列表
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public PageInfo<TimeSegmentPo> getAllAdTime(Integer page, Integer pagesize, Byte type)
    {
        TimeSegmentPoExample example = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(type);

        List<TimeSegmentPo> timeSegmentPos = timeSegmentPoMapper.selectByExample(example);

        logger.debug("getAllAddressesById: retAddresses = "+timeSegmentPos);

        return new PageInfo<>(timeSegmentPos);
    }

    /**
     * 平台管理员获取删除时段
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject deleteTimeSegmentById(Long segid, byte type)
    {
        TimeSegmentPoExample example = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(type);
        criteria.andIdEqualTo(segid);
        List<TimeSegmentPo> orig=timeSegmentPoMapper.selectByExample(example);
        //不删除不存在的
        if (orig == null ) {
            logger.info("该类型时段不存在：id = " + segid);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = timeSegmentPoMapper.deleteByExample(example);
            //同时将该时段下的广告（秒杀）的segid置为0
            if(type==(byte)0)
            {
                advertisementService.setSegIdToZero(segid);
            }
            if(type==(byte)1)
            {

            }

        } catch (DataAccessException e) {
            // 其他情况属未知错误
            logger.error("数据库错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("时段删除失败：id = " + segid);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("时段 id = " + segid + " 已删除");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * 根据当前时间获得广告时间段id
     *
     * @author ChengYang Li
     */
    public Long getSegIdByTimeNow()
    {
        LocalDateTime timeNow = LocalDateTime.now();
        TimeSegmentPoExample example = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo((byte)0);
        criteria.andEndTimeGreaterThanOrEqualTo(timeNow);
        criteria.andBeginTimeLessThanOrEqualTo(timeNow);
        List<TimeSegmentPo> pos=timeSegmentPoMapper.selectByExample(example);
        if(pos.size()==0) {
            return (long)-1;
        }
        return pos.get(0).getId();
    }

    /**
     * 判断该广告时段是否存在
     *
     * @author ChengYang Li
     */
    public boolean segIdExist(Long segId)
    {
        TimeSegmentPoExample example = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo((byte)0);
        criteria.andIdEqualTo(segId);
        List<TimeSegmentPo> po=timeSegmentPoMapper.selectByExample(example);
        if(po.size()==0) {
            return false;
        }
        else {
            return true;
        }
    }
}
