package cn.edu.xmu.other.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.mapper.BeSharePoMapper;
import cn.edu.xmu.other.mapper.ShareActivityPoMapper;
import cn.edu.xmu.other.mapper.SharePoMapper;
import cn.edu.xmu.other.model.bo.Share;
import cn.edu.xmu.other.model.bo.ShareActivity;
import cn.edu.xmu.other.model.po.*;
import cn.edu.xmu.other.model.vo.ShareVo.ShareActivityVo;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author 杨浩然
 * @date Created in 2020/12/1 17:08
 * Modified by 24320182203309 杨浩然 at 2020/12/1 18:00
 **/
@Repository
public class ShareDao {
    private static final Logger logger = LoggerFactory.getLogger(ShareDao.class);

    @Autowired
    private SharePoMapper sharePoMapper;
    @Autowired
    private ShareActivityPoMapper shareActivityPoMapper;
    @Autowired
    private BeSharePoMapper beSharePoMapper;

    //String转为LocalDateTime
    public static LocalDateTime parseStringToDateTime(String time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, df);
    }
    /**
     * 由 vo和user Id 生成分享链接
     * @param skuId, userId
     * @return ShareRetVo
     * created by 杨浩然 24320182203309
     **/
    public ReturnObject<VoObject> createShare(Long skuId, Long userId) {
        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        SharePo sharePo = new SharePo();
        sharePo.setSharerId(userId);
        sharePo.setGoodsSkuId(skuId);
        sharePo.setQuantity(0);
        sharePo.setGmtCreate(localDateTime);
        sharePo.setGmtModified(localDateTime);
        sharePo.setShareActivityId(0L);
        try {
            int ret = sharePoMapper.insert(sharePo);
            if (ret == 0) {
                //插入失败
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                //插入成功
            }
        } catch (DataAccessException e) {
            // 数据库错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
        }
        Share share = new Share(sharePo);
        return new ReturnObject<VoObject>(share);

    }

    /**
     * 买家获取所有分享信息
     * @author yhr
     * @return List<SharePo> 分享列表
     **/
    public PageInfo<SharePo> getShares(Long goodsSkuId, String beginTime, String endTime, Long userId) {
        SharePoExample example = new SharePoExample();
        SharePoExample.Criteria criteria = example.createCriteria();
        criteria.andSharerIdEqualTo(userId);
        if(goodsSkuId!=null) {
            criteria.andGoodsSkuIdEqualTo(goodsSkuId);
        }
        if(beginTime.length()!=0&&endTime.length()!=0) {
            criteria.andGmtCreateBetween(parseStringToDateTime(beginTime),parseStringToDateTime(endTime));
        }
        List<SharePo> shareItems = sharePoMapper.selectByExample(example);
        logger.debug("getShareItems: shareItems = "+shareItems);
        return new PageInfo<>(shareItems);
    }

    /**
     * 管理员获取所有分享信息
     * @author yhr
     * @return List<SharePo> 分享列表
     **/
    public PageInfo<SharePo> adminGetShares(Long goodsSkuId, Long id) {
        SharePoExample example = new SharePoExample();
        SharePoExample.Criteria criteria = example.createCriteria();
        //TODO 根据店铺id查，缺少接口
        if(goodsSkuId!=null) {
            criteria.andGoodsSkuIdEqualTo(goodsSkuId);
        }
        List<SharePo> shareItems = sharePoMapper.selectByExample(example);
        logger.debug("getShareById: shareItems = "+shareItems);
        return new PageInfo<>(shareItems);
    }
    /**
     * 买家获取所有分享成功信息
     * @author yhr
     * @return List<BeSharePo> 分享列表
     **/
    public PageInfo<BeSharePo> getBeShared(Long goodsSkuId, String beginTime, String endTime, Long userId) {
        BeSharePoExample example = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = example.createCriteria();
        criteria.andSharerIdEqualTo(userId);
        if(goodsSkuId!=null) {
            criteria.andGoodsSkuIdEqualTo(goodsSkuId);
        }
        if(beginTime.length()!=0&&endTime.length()!=0) {
            criteria.andGmtCreateBetween(parseStringToDateTime(beginTime),parseStringToDateTime(endTime));
        }
        List<BeSharePo> beShareItems = beSharePoMapper.selectByExample(example);
        logger.debug("getShareById: shareItems = "+beShareItems);
        return new PageInfo<>(beShareItems);
    }
    /**
     * 管理员获取所有分享成功信息
     * @author yhr
     * @return List<BeSharePo> 分享列表
     **/
    public PageInfo<BeSharePo> adminGetBeShared(Long id,Long goodsSkuId, String beginTime, String endTime) {
        BeSharePoExample example = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = example.createCriteria();
        //TODO 根据店铺Id查 需要接口
        if(goodsSkuId!=null) {
            criteria.andGoodsSkuIdEqualTo(goodsSkuId);
        }
        if(beginTime.length()!=0&&endTime.length()!=0) {
            criteria.andGmtCreateBetween(parseStringToDateTime(beginTime),parseStringToDateTime(endTime));
        }
        List<BeSharePo> beShareItems = beSharePoMapper.selectByExample(example);
        logger.debug("adminGetShareById: shareItems = "+beShareItems);
        return new PageInfo<>(beShareItems);
    }

    /**
     * 创建分享活动
     * @return ShareActivityRetVo
     * created by 杨浩然 24320182203309
     **/
    public ReturnObject<VoObject> createShareActivity(ShareActivityVo vo, Long shopId, Long skuId) {
        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        ShareActivityPo shareActivityPo = new ShareActivityPo();
        shareActivityPo.setShopId(shopId);
        shareActivityPo.setBeginTime(parseStringToDateTime(vo.getBeginTime()));
        shareActivityPo.setEndTime(parseStringToDateTime(vo.getEndTime()));
        shareActivityPo.setGmtCreate(localDateTime);
        shareActivityPo.setGmtModified(localDateTime);
        shareActivityPo.setStrategy(vo.getStrategy());
        shareActivityPo.setGoodsSkuId(skuId);
        shareActivityPo.setState((byte)0);
        try {
            int ret = shareActivityPoMapper.insert(shareActivityPo);
            if (ret == 0) {
                //插入失败
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                //插入成功
            }
        } catch (DataAccessException e) {
            // 数据库错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
        }
        ShareActivity shareActivity = new ShareActivity(shareActivityPo);
        return new ReturnObject<VoObject>(shareActivity);

    }

    /**
     * 获取所有分享活动信息（所有用户皆可）
     * @author yhr
     * @return List<SharePo> 分享列表
     **/
    public PageInfo<ShareActivityPo> getShareActivities(Long goodsSkuId, Long shopId) {
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();
        if(shopId!=null) {
            criteria.andShopIdEqualTo(shopId);
        }
        if(goodsSkuId!=null) {
            criteria.andGoodsSkuIdEqualTo(goodsSkuId);
        }
        List<ShareActivityPo> shareActivities = shareActivityPoMapper.selectByExample(example);
        logger.debug("getShareById: shareActivities = "+shareActivities);
        return new PageInfo<>(shareActivities);
    }

    /**根据 id 修改分享活动信息
     * @param vo 传入的ShareActivityVo对象
     * @return 返回对象 ReturnObj
     * @author 24320182203309 yhr
     * Created at 2020/12/5 20:30
     * Modified by 24320182203309 yhr at 2020/12/5 22:42
     **/
    public ReturnObject<Object> modifyShareActivity(Long id, Long shopId, ShareActivityVo vo,Long userId) {
        ShareActivityPo shareActivityPo=shareActivityPoMapper.selectByPrimaryKey(id);
        if(shareActivityPo!=null){
            shareActivityPo.setShopId(shopId);
            shareActivityPo.setBeginTime(parseStringToDateTime(vo.getBeginTime()));
            shareActivityPo.setEndTime(parseStringToDateTime(vo.getEndTime()));
            shareActivityPo.setStrategy(vo.getStrategy());
        }
        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = shareActivityPoMapper.updateByPrimaryKey(shareActivityPo);
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
            logger.info("分享活动不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("分享活动 id = " + id + " 的资料已更新");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * 管理员下线分享活动
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param Id
     * @return
     **/
    public ReturnObject getShareOnline(Long userId, Long shopId, Long Id)
    {
        /**
         * @// TODO: 2020/12/5 与商品模块做接口:通过店铺id获得其所有管理员的信息 确认管理权限
         **/
        ShareActivityPo ret = null;
        try {
            ShareActivityPoExample example = new ShareActivityPoExample();
            ShareActivityPoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(Id);
            criteria.andShopIdEqualTo(shopId);
            List<ShareActivityPo> rets = shareActivityPoMapper.selectByExample(example);
            ret = rets==null? rets.get(0) : null;
            if (ret == null)
            {
                System.out.println("ShareDao -> getShareOffline 未找到指定的分享活动");
                logger.debug("ShareDao -> getShareOffline 未找到指定的分享活动");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            else {
                //插入成功
            }
        } catch (DataAccessException e) {
            // 数据库错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
        }
        ret.setState((byte)1);
        shareActivityPoMapper.updateByPrimaryKeySelective(ret);
        return new ReturnObject<>();
    }

    /**
     * 管理员下线分享活动
     * @param userId
     * @param shopId
     * @param Id
     * @return
     **/
    public ReturnObject getShareOffline(Long userId, Long shopId, Long Id)
    {
        /**
         * @// TODO: 2020/12/5 与商品模块做接口:通过店铺id获得其所有管理员的信息 确认管理权限
         */
        ShareActivityPo ret = null;
        try {
            ShareActivityPoExample example = new ShareActivityPoExample();
            ShareActivityPoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(Id);
            criteria.andShopIdEqualTo(shopId);
            List<ShareActivityPo> rets = shareActivityPoMapper.selectByExample(example);
            ret = rets==null? rets.get(0) : null;
            if (ret == null)
            {
                System.out.println("ShareDao -> getShareOffline 未找到指定的分享活动");
                logger.debug("ShareDao -> getShareOffline 未找到指定的分享活动");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            else {
                //插入成功
            }
        } catch (DataAccessException e) {
            // 数据库错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
        }
        ret.setState((byte)0);
        shareActivityPoMapper.updateByPrimaryKeySelective(ret);
        return new ReturnObject<>();
    }
}
