package cn.edu.xmu.other.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.mapper.AdvertisementPoMapper;
import cn.edu.xmu.other.model.bo.Advertisement;
import cn.edu.xmu.other.model.po.AdvertisementPo;
import cn.edu.xmu.other.model.po.AdvertisementPoExample;
import cn.edu.xmu.other.model.vo.AdvertisementVo.AdvertisementExamVo;
import cn.edu.xmu.other.model.vo.AdvertisementVo.AdvertisementModifyVo;
import cn.edu.xmu.other.service.TimeSegmentService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author ChengYang Li
 */
@Repository
public class AdvertisementDao
{
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementDao.class);

    @Autowired
    AdvertisementPoMapper advertisementPoMapper;

    @Autowired
    TimeSegmentService timeSegmentService;

    /**
     * 管理员在广告时段下新建广告
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject<Advertisement> addAdUnderTimeSeg(Advertisement advertisement)
    {
        AdvertisementPo advertisementPo=(AdvertisementPo) advertisement.createSimpleVo();
        ReturnObject<Advertisement> retObj = null;
        try{
            //如果时段不存在，直接返回
            if(!timeSegmentService.segIdExist(advertisement.getSegId()))
            {
                logger.debug("addAdvertisement: insert advertisement fail : 时段不存在");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }

            //新增地址前先检查该时段是否存在默认广告，如果不存在默认广告，则将该广告设为默认广告同时检查该广告时段下是否已有8个广告
            AdvertisementPoExample example = new AdvertisementPoExample();
            AdvertisementPoExample.Criteria criteria = example.createCriteria();
            criteria.andSegIdEqualTo(advertisement.getSegId());
            List<AdvertisementPo> advertisements=advertisementPoMapper.selectByExample(example);

            //判断该时段广告是否已经到达上限
            if(advertisements.size()==8)
            {
                logger.debug("addAdvertisement: insert advertisement fail : 达到时段广告上限");
                return new ReturnObject<>(ResponseCode.ADVERTISEMENT_OUTLIMIT, String.format("达到时段广告上限"));
            }

            advertisementPo.setBeDefault((byte)1);
            //查找该广告时段下是否存在默认广告
            for(int i=0; i<advertisements.size(); i++)
            {
                //存在默认广告，设置为非默认广告
                if(advertisements.get(i).getBeDefault().intValue()==1) {
                    advertisementPo.setBeDefault((byte) 0);
                    break;
                }
            }
            int ret = advertisementPoMapper.insertSelective(advertisementPo);
            if (ret == 0) {
                //插入失败
                logger.debug("addAdvertisement: insert advertisement fail " + advertisementPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                //插入成功
                logger.debug("addAdvertisement: insert advertisement = " + advertisementPo.toString());
                advertisement.setId(advertisementPo.getId());
                advertisement.setBeDefault(advertisementPo.getBeDefault());
                retObj = new ReturnObject<>(advertisement);
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
     * 管理员修改广告的时段
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject<Advertisement> updateAdTimeId(Long timeId, Long adId)
    {
        //先查询广告是否存在
        AdvertisementPo po=advertisementPoMapper.selectByPrimaryKey(adId);
        if(po==null)
        {
            logger.info("广告不存在：id = " + adId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        ReturnObject<Advertisement> retObj = null;
        try{
            //如果时段不存在，直接返回
            if(!timeSegmentService.segIdExist(timeId))
            {
                logger.debug("addAdvertisement: insert advertisement fail : 时段不存在");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }

            //新增地址前先检查该时段是否存在默认广告，如果不存在默认广告，则将该广告设为默认广告同时检查该广告时段下是否已有8个广告
            AdvertisementPoExample example = new AdvertisementPoExample();
            AdvertisementPoExample.Criteria criteria = example.createCriteria();
            criteria.andSegIdEqualTo(timeId);
            List<AdvertisementPo> advertisements=advertisementPoMapper.selectByExample(example);

            //判断该时段广告是否已经到达上限
            if(advertisements.size()==8)
            {
                logger.debug("addAddress: insert advertisement fail : 达到时段广告上限");
                return new ReturnObject<>(ResponseCode.ADVERTISEMENT_OUTLIMIT, String.format("达到时段广告上限"));
            }

            //更新修改时间，时段id
            po.setGmtModified(LocalDateTime.now());
            po.setSegId(timeId);
            int ret = advertisementPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                //更新失败
                logger.debug("addAdvertisement: insert advertisement fail " + po.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                //更新成功
                logger.debug("addAdvertisement: insert advertisement = " + po.toString());
                Advertisement advertisement=new Advertisement(po);
                retObj = new ReturnObject<>(advertisement);
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
     * 管理员查看某一个广告时间段的广告
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public PageInfo<AdvertisementPo> findAllAdByTimeId(String beginDate, String endDate, Long timeId, Integer page, Integer pagesize)
    {
        AdvertisementPoExample example = new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria = example.createCriteria();
        criteria.andSegIdEqualTo(timeId);
        if(beginDate!=null)
        {
            criteria.andBeginDateGreaterThanOrEqualTo(LocalDate.parse(beginDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        if(endDate!=null)
        {
            criteria.andEndDateGreaterThanOrEqualTo(LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        List<AdvertisementPo> advertisementPos = advertisementPoMapper.selectByExample(example);

        logger.debug("findAllAdByTimeId: retAddresses = "+advertisementPos);

        return new PageInfo<>(advertisementPos);
    }

    /**
     * 管理员审核广告
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject examAdvertisement(Long adId, AdvertisementExamVo vo)
    {
        ReturnObject<Object> retObj;
        AdvertisementPo po=advertisementPoMapper.selectByPrimaryKey(adId);
        //不审核不存在的广告
        if(po==null)
        {
            logger.info("广告不存在：id = " + adId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        //不能审核处于上架和下架状态的广告
        if(po.getState().intValue()!=0)
        {
            logger.info("广告不处于审核状态：id = " + adId);
            return new ReturnObject<>(ResponseCode.ADVERTISEMENT_STATENOTALLOW);
        }
        //根据管理员的审核意见修改广告的状态
        if(vo.getConclusion().equals("true"))
        {
            //审核通过
            po.setState((byte)6);
        }
        po.setMessage(vo.getMessage());
        po.setGmtModified(LocalDateTime.now());
        try{
            //更新数据库
            int ret = advertisementPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                //更新失败
                logger.debug("examAdvertisement: update advertisement fail " + po.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                //更新成功
                logger.debug("examAdvertisement: update advertisement = " + po.toString());
                retObj = new ReturnObject<>();
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
     * 管理员下架广告
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject offLineAdvertisement(Long adId)
    {
        ReturnObject<Object> retObj;
        AdvertisementPo po=advertisementPoMapper.selectByPrimaryKey(adId);
        //不下架不存在的广告
        if(po==null)
        {
            logger.info("广告不存在：id = " + adId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        //不能下架处于审核和下架状态的广告
        if(po.getState().intValue()!=4)
        {
            logger.info("广告不处于上架状态：id = " + adId);
            return new ReturnObject<>(ResponseCode.ADVERTISEMENT_STATENOTALLOW);
        }
        //将上架状态的广告置为下架状态，并更新数据库
        po.setState((byte)6);
        po.setGmtModified(LocalDateTime.now());
        try
        {
            //更新数据库
            int ret = advertisementPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                //更新失败
                logger.debug("offLineAdvertisement: offLine advertisement fail " + po.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                //更新成功
                logger.debug("offLineAdvertisement: offline advertisement = " + po.toString());
                retObj = new ReturnObject<>();
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
     * 管理员上架广告
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject onLineAdvertisement(Long adId)
    {
        ReturnObject<Object> retObj;
        AdvertisementPo po=advertisementPoMapper.selectByPrimaryKey(adId);
        //不上架不存在的广告
        if(po==null)
        {
            logger.info("广告不存在：id = " + adId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        //不能上架处于审核和上架状态的广告
        if(po.getState().intValue()!=6)
        {
            logger.info("广告不处于下架状态：id = " + adId);
            return new ReturnObject<>(ResponseCode.ADVERTISEMENT_STATENOTALLOW);
        }
        //将上架状态的广告置为下架状态，并更新数据库
        po.setState((byte)4);
        po.setGmtModified(LocalDateTime.now());
        try
        {
            //更新数据库
            int ret = advertisementPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                //更新失败
                logger.debug("onLineAdvertisement: onLine advertisement fail " + po.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                //更新成功
                logger.debug("onLineAdvertisement: online advertisement = " + po.toString());
                retObj = new ReturnObject<>();
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
     * 管理员设置默认广告
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject setDefaultAdvertisement(Long adId)
    {
        ReturnObject<Object> retObj;
        AdvertisementPo po=advertisementPoMapper.selectByPrimaryKey(adId);
        //不设置不存在的广告
        if(po==null)
        {
            logger.info("广告不存在：id = " + adId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        //检查该时段是否已经有默认广告了，如果有就替换掉
        AdvertisementPoExample example = new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria = example.createCriteria();
        criteria.andSegIdEqualTo(po.getSegId());
        List<AdvertisementPo> advertisements=advertisementPoMapper.selectByExample(example);
        for(int i=0;i<advertisements.size();i++)
        {
            if(advertisements.get(i).getBeDefault()==null||advertisements.get(i).getBeDefault().intValue()==1)
            {
                advertisements.get(i).setBeDefault((byte)0);
                advertisementPoMapper.updateByPrimaryKeySelective(advertisements.get(i));
            }
        }
        po.setBeDefault((byte)1);
        po.setGmtModified(LocalDateTime.now());
        try
        {
            //更新数据库
            int ret = advertisementPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                //更新失败
                logger.debug("setDefaultAdvertisement: setDefault advertisement fail " + po.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                //更新成功
                logger.debug("setDefaultAdvertisement: setDefault advertisement = " + po.toString());
                retObj = new ReturnObject<>();
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
     * 管理员删除广告
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject deleteAdvertisement(Long adId)
    {
        ReturnObject<Object> retObj;
        AdvertisementPo po=advertisementPoMapper.selectByPrimaryKey(adId);
        //不删除不存在的广告
        if(po==null)
        {
            logger.info("广告不存在：id = " + adId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        //不能删除处于上架状态的广告
        if(po.getState().intValue()==4)
        {
            logger.info("广告处于上架状态，不能删除：id = " + adId);
            return new ReturnObject<>(ResponseCode.ADVERTISEMENT_STATENOTALLOW);
        }
        //在数据库中物理删除该广告
        try
        {
            //更新数据库
            int ret = advertisementPoMapper.deleteByPrimaryKey(adId);
            if (ret == 0) {
                //更新失败
                logger.debug("deleteAdvertisement: delete advertisement fail " + po.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                //更新成功
                logger.debug("deleteAdvertisement: delete advertisement = " + po.toString());
                retObj = new ReturnObject<>();
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
     * 管理员修改广告信息
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject updateAdvertisementById(Long adId, AdvertisementModifyVo vo)
    {
        ReturnObject<Object> retObj;
        AdvertisementPo po=advertisementPoMapper.selectByPrimaryKey(adId);
        //不修改不存在的广告
        if(po==null)
        {
            logger.info("广告不存在：id = " + adId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        //修改广告时段
        ReturnObject<Advertisement> adReturnObject=updateAdTimeId(vo.getSegId(), adId);
        if(adReturnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            logger.debug("该广告时段不存在");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(adReturnObject.getCode()==ResponseCode.ADVERTISEMENT_OUTLIMIT)
        {
            logger.debug("addAddress: insert advertisement fail : 达到时段广告上限");
            return new ReturnObject<>(ResponseCode.ADVERTISEMENT_OUTLIMIT, String.format("达到时段广告上限"));
        }

        //修改内容，权重和链接
        po.setState((byte)0);
        po.setWeight(vo.getWeight());
        po.setContent(vo.getContent());
        po.setLink(vo.getLink());
        po.setGmtModified(LocalDateTime.now());
        //更新数据库
        try
        {
            //更新数据库
            int ret = advertisementPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                //更新失败
                logger.debug("deleteAdvertisement: delete advertisement fail " + po.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                //更新成功
                logger.debug("deleteAdvertisement: delete advertisement = " + po.toString());
                retObj = new ReturnObject<>();
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
     * 管理员获取广告
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject<Advertisement> getAdvertisementById(Long adId)
    {
        AdvertisementPo advertisementPo = advertisementPoMapper.selectByPrimaryKey(adId);
        if (advertisementPo == null) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Advertisement advertisement = new Advertisement(advertisementPo);

        return new ReturnObject<>(advertisement);
    }

    /**
     * 管理员更新图片
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject updateImageUrl(Advertisement advertisement)
    {
        ReturnObject returnObject = new ReturnObject();
        AdvertisementPo newAdvertisementPo = new AdvertisementPo();
        newAdvertisementPo.setId(advertisement.getId());
        newAdvertisementPo.setImageUrl(advertisement.getImageUrl());
        int ret = advertisementPoMapper.updateByPrimaryKeySelective(newAdvertisementPo);
        if (ret == 0) {
            logger.debug("updateAdvertisement: update fail. advertisement id: " + advertisement.getId());
            returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
        } else {
            logger.debug("updateAdvertisement: update advertisementImage success : " + advertisement.toString());
            returnObject = new ReturnObject();
        }
        return returnObject;
    }

    /**
     * 获取当前时段广告列表
     *
     * @author ChengYang Li
     */
    public ReturnObject getAllAdByTimeNow(Long segId)
    {
        AdvertisementPoExample example = new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria = example.createCriteria();
        criteria.andSegIdEqualTo(segId);
        criteria.andStateEqualTo((byte)6);

        List<AdvertisementPo> advertisementPos = advertisementPoMapper.selectByExample(example);
        List<Advertisement> advertisements=new ArrayList<>();
        LocalDate date=LocalDate.now();
        for(AdvertisementPo adPo:advertisementPos)
        {
            //如果当前时间不在广告的起止时间内，并且广告的repeats不为1，则不放入list中
            if(adPo.getRepeats().intValue()==1||adPo.getBeginDate().isBefore(date)&&adPo.getEndDate().isAfter(date)) {
                Advertisement ad = new Advertisement(adPo);
                advertisements.add(ad);
            }
        }
        //按照权重降序排序
        advertisements.sort(Comparator.comparing(Advertisement::getWeight).reversed());
        return new ReturnObject(advertisements);
    }

    /**
     * 将该广告时段下的广告的segId全部置为0
     *
     * @author ChengYang Li
     */
    public ReturnObject setAdSegIdToZero(Long segId)
    {
        AdvertisementPoExample example = new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria = example.createCriteria();
        criteria.andSegIdEqualTo(segId);
        List<AdvertisementPo> advertisementPos = advertisementPoMapper.selectByExample(example);
        for(int i=0;i<advertisementPos.size();i++)
        {
            AdvertisementPo po=advertisementPos.get(i);
            po.setSegId((long)0);
            po.setGmtModified(LocalDateTime.now());
            advertisementPoMapper.updateByPrimaryKeySelective(po);
        }
        return new ReturnObject();
    }
}
