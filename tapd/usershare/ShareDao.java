package cn.edu.xmu.other.dao;

import cn.edu.xmu.goods.client.IGoodsService;
import cn.edu.xmu.goods.client.dubbo.SkuDTO;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.dto.ShareDto;
import cn.edu.xmu.other.mapper.*;
import cn.edu.xmu.other.model.bo.*;
import cn.edu.xmu.other.model.po.*;
import cn.edu.xmu.other.model.vo.ShareVo.BeShareRetVo;
import cn.edu.xmu.other.model.vo.ShareVo.GetShareRetVo;
import cn.edu.xmu.other.model.vo.ShareVo.ShareActivityVo;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 杨浩然
 * @date Created in 2020/12/1 17:08
 * Modified by 24320182203309 杨浩然 at 2020/12/1 18:00
 **/
@Repository
@Component
public class ShareDao {
    private static final Logger logger = LoggerFactory.getLogger(ShareDao.class);
    @Autowired
    private SharePoMapper sharePoMapper;
    @Autowired
    private ShareActivityPoMapper shareActivityPoMapper;
    @Autowired
    private BeSharePoMapper beSharePoMapper;
    @Autowired
    private AfterSalePoMapper afterSalePoMapper;
    @Autowired
    private CustomerPoMapper customerPoMapper;

    @DubboReference(version = "1.1.1-SNAPSHOT")
    IGoodsService iGoodsService;

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
        SkuDTO dto=iGoodsService.getSku(skuId);
        if(dto==null)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("找不到该skuId的商品"));
        }
        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        SharePo sharePo = new SharePo();
        sharePo.setSharerId(userId);
        sharePo.setGoodsSkuId(skuId);
        sharePo.setQuantity(0);
        sharePo.setGmtCreate(localDateTime);
        sharePo.setGmtModified(localDateTime);
        ShareActivityPoExample shareActivityPoExample=new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria=shareActivityPoExample.createCriteria();
        criteria.andBeginTimeLessThanOrEqualTo(localDateTime);
        criteria.andEndTimeGreaterThan(localDateTime);
        criteria.andGoodsSkuIdEqualTo(skuId);
        List<ShareActivityPo> shareActivityPos=shareActivityPoMapper.selectByExample(shareActivityPoExample);
        if(shareActivityPos.size()!=0) {
            //获取分享活动id
            sharePo.setShareActivityId(shareActivityPos.get(0).getId());
        }
        //检查重复
        SharePoExample sharePoExample=new SharePoExample();
        SharePoExample.Criteria criteria1=sharePoExample.createCriteria();
        criteria1.andGoodsSkuIdEqualTo(skuId);
        criteria1.andSharerIdEqualTo(userId);
        List<SharePo> sPos=sharePoMapper.selectByExample(sharePoExample);
        if(sPos.size()!=0){
            sharePo.setId(sPos.get(0).getId());
            sharePoMapper.updateByPrimaryKey(sharePo);
        }
        else{
            try {
            int ret = sharePoMapper.insert(sharePo);
            if (ret == 0) {
                //插入失败
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("插入share失败"));
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
        }
        Share share = new Share(sharePo);
        //从商品模块获得this.skuVo=;传入参数：skuid


        share.getSkuGoodsRetVo().setId(dto.getId());
        share.getSkuGoodsRetVo().setName(dto.getName());
        share.getSkuGoodsRetVo().setSkuSn(dto.getSkuSn());
        share.getSkuGoodsRetVo().setImageUrl(dto.getImageUrl());
        share.getSkuGoodsRetVo().setInventory(dto.getInventory());
        share.getSkuGoodsRetVo().setOriginalPrice(dto.getOriginalPrice());
        share.getSkuGoodsRetVo().setPrice(dto.getPrice());
        share.getSkuGoodsRetVo().setDisable(dto.getDisable());
        return new ReturnObject<VoObject>(share);

    }

    /**
     * 买家获取所有分享信息
     * @author yhr
     * @return List<SharePo> 分享列表
     **/

    public ReturnObject<PageInfo<VoObject>> getShares(Integer pageNum,Integer pageSize,Long goodsSkuId, String beginTime, String endTime, Long userId)  {
        SharePoExample example = new SharePoExample();
        SharePoExample.Criteria criteria = example.createCriteria();
        criteria.andSharerIdEqualTo(userId);
        if(goodsSkuId!=null) {
            criteria.andGoodsSkuIdEqualTo(goodsSkuId);
        }
        if(beginTime.length()!=0&&endTime.length()!=0){
            try {
                criteria.andGmtCreateBetween(parseStringToDateTime(beginTime), parseStringToDateTime(endTime));
            }
            catch (Exception ex){
                List<VoObject> empty=new ArrayList<>();
                PageInfo<VoObject> getSharePage = new PageInfo<>(empty);
                getSharePage.setPageNum(pageNum);
                getSharePage.setPageSize(pageSize);
                return new ReturnObject<>(getSharePage);
            }

        }
        PageHelper.startPage(pageNum, pageSize);

        List<SharePo> sharePos = sharePoMapper.selectByExample(example);
        logger.debug("getShareItems: sharePos = "+sharePos);
        List<VoObject> rets=new ArrayList<>(sharePos.size());
        for(int i=0;i<sharePos.size();i++)
        {
            GetShare getShare=new GetShare(sharePos.get(i));
            GetShareRetVo vo= (GetShareRetVo)getShare.createSimpleVo();
            SkuDTO dto=iGoodsService.getSku(getShare.getGoodsSkuId());
            if(dto==null)
            {
                continue;
            }
            vo.getSku().setOriginalPrice(dto.getOriginalPrice());
            vo.getSku().setInventory(dto.getInventory());
            vo.getSku().setImageUrl(dto.getImageUrl());
            vo.getSku().setSkuSn(dto.getSkuSn());
            vo.getSku().setName(dto.getName());
            vo.getSku().setId(dto.getId());
            vo.getSku().setPrice(dto.getPrice());
            vo.getSku().setDisable(dto.getDisable());
            rets.add(vo);
        }
        PageInfo<VoObject> getSharePage = PageInfo.of(rets);
        getSharePage.setTotal(PageInfo.of(sharePos).getTotal());
        getSharePage.setPages(PageInfo.of(sharePos).getPages());
        getSharePage.setPageNum(pageNum);
        getSharePage.setPageSize(pageSize);
        return new ReturnObject<>(getSharePage);
    }
    /**
     * 管理员获取所有分享信息
     * @author yhr
     * @return List<SharePo> 分享列表
     **/
    public ReturnObject<PageInfo<VoObject>> adminGetShares(Integer pageNum,Integer pageSize,Long goodsSkuId, Long id) {
        SharePoExample example = new SharePoExample();
        SharePoExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsSkuIdEqualTo(goodsSkuId);

        PageHelper.startPage(pageNum, pageSize);

        List<SharePo> sharePos = sharePoMapper.selectByExample(example);
        logger.debug("getShareItems: sharePos = "+sharePos);
        List<VoObject> rets=new ArrayList<>(sharePos.size());
        System.out.println(sharePos.size());
        for(int i=0;i<sharePos.size();i++)
        {
            GetShare getShare=new GetShare(sharePos.get(i));
            GetShareRetVo vo= (GetShareRetVo)getShare.createSimpleVo();
            SkuDTO dto=iGoodsService.getSku(getShare.getGoodsSkuId());
            if(dto==null)
            {
                continue;
            }
            vo.getSku().setOriginalPrice(dto.getOriginalPrice());
            vo.getSku().setInventory(dto.getInventory());
            vo.getSku().setImageUrl(dto.getImageUrl());
            vo.getSku().setSkuSn(dto.getSkuSn());
            vo.getSku().setName(dto.getName());
            vo.getSku().setId(dto.getId());
            vo.getSku().setPrice(dto.getPrice());
            vo.getSku().setDisable(dto.getDisable());
            rets.add(vo);
        }
        PageInfo<VoObject> getSharePage = PageInfo.of(rets);
        getSharePage.setPageNum(pageNum);
        getSharePage.setPageSize(pageSize);
        getSharePage.setTotal(PageInfo.of(sharePos).getTotal());
        getSharePage.setPages(PageInfo.of(sharePos).getPages());
        return new ReturnObject<>(getSharePage);
    }
    /**
     * 买家获取所有分享成功信息
     * @author yhr
     * @return List<BeSharePo> 分享列表
     **/
    public ReturnObject<PageInfo<VoObject>> getBeShared(Integer pageNum, Integer pageSize,Long goodsSkuId, String beginTime, String endTime, Long userId) {
        BeSharePoExample example = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = example.createCriteria();
        criteria.andSharerIdEqualTo(userId);
        if(goodsSkuId!=null) {
            criteria.andGoodsSkuIdEqualTo(goodsSkuId);
        }
        if(beginTime.length()!=0&&endTime.length()!=0){
            try {
                criteria.andGmtCreateBetween(parseStringToDateTime(beginTime), parseStringToDateTime(endTime));
            }
            catch (Exception ex){
                List<VoObject> empty=new ArrayList<>();
                PageInfo<VoObject> getSharePage = new PageInfo<>(empty);
                getSharePage.setPageNum(pageNum);
                getSharePage.setPageSize(pageSize);
                return new ReturnObject<>(getSharePage);
            }

        }
        PageHelper.startPage(pageNum, pageSize);

        List<BeSharePo> beSharePos = beSharePoMapper.selectByExample(example);
        logger.debug("getBeShareItems: beSharePos = "+beSharePos);
        List<VoObject> rets=new ArrayList<>(beSharePos.size());

        for(int i=0;i<beSharePos.size();i++)
        {
            BeShare beShare=new BeShare(beSharePos.get(i));
            BeShareRetVo vo= (BeShareRetVo)beShare.createSimpleVo();
            SkuDTO dto=iGoodsService.getSku(beShare.getGoodsSkuId());
            if(dto==null)
            {
                continue;
            }
            vo.getSku().setOriginalPrice(dto.getOriginalPrice());
            vo.getSku().setInventory(dto.getInventory());
            vo.getSku().setImageUrl(dto.getImageUrl());
            vo.getSku().setSkuSn(dto.getSkuSn());
            vo.getSku().setName(dto.getName());
            vo.getSku().setId(dto.getId());
            vo.getSku().setPrice(dto.getPrice());
            vo.getSku().setDisable(dto.getDisable());
            rets.add(vo);
        }
        PageInfo<VoObject> beSharePage = PageInfo.of(rets);
        beSharePage.setPageNum(pageNum);
        beSharePage.setPageSize(pageSize);
        beSharePage.setTotal(PageInfo.of(beSharePos).getTotal());
        beSharePage.setPages(PageInfo.of(beSharePos).getPages());
        return new ReturnObject<>(beSharePage);
    }
    /**
     * 管理员获取所有分享成功信息
     * @author yhr
     * @return List<BeSharePo> 分享列表
     *
     **/
    public ReturnObject<PageInfo<VoObject>> adminGetBeShared(Integer pageNum, Integer pageSize,Long id,Long goodsSkuId, String beginTime, String endTime) {
        BeSharePoExample example = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsSkuIdEqualTo(goodsSkuId);

        if(beginTime.length()!=0&&endTime.length()!=0){
            try {
                criteria.andGmtCreateBetween(parseStringToDateTime(beginTime), parseStringToDateTime(endTime));
            }
            catch (Exception ex){
                List<VoObject> empty=new ArrayList<>();
                PageInfo<VoObject> getSharePage = new PageInfo<>(empty);
                getSharePage.setPageNum(pageNum);
                getSharePage.setPageSize(pageSize);
                return new ReturnObject<>(getSharePage);
            }
        }
        PageHelper.startPage(pageNum, pageSize);

        List<BeSharePo> beSharePos = beSharePoMapper.selectByExample(example);
        logger.debug("adminGetBeShareItems: beSharePos = "+beSharePos);
        List<VoObject> rets=new ArrayList<>(beSharePos.size());

        for(int i=0;i<beSharePos.size();i++)
        {
            BeShare beShare=new BeShare(beSharePos.get(i));
            BeShareRetVo vo= (BeShareRetVo)beShare.createSimpleVo();
            SkuDTO dto=iGoodsService.getSku(beShare.getGoodsSkuId());
            if(dto==null)
            {
                continue;
            }
            vo.getSku().setOriginalPrice(dto.getOriginalPrice());
            vo.getSku().setInventory(dto.getInventory());
            vo.getSku().setImageUrl(dto.getImageUrl());
            vo.getSku().setSkuSn(dto.getSkuSn());
            vo.getSku().setName(dto.getName());
            vo.getSku().setId(dto.getId());
            vo.getSku().setPrice(dto.getPrice());
            vo.getSku().setDisable(dto.getDisable());
            rets.add(vo);
        }
        PageInfo<VoObject> beSharePage = PageInfo.of(rets);
        beSharePage.setPageNum(pageNum);
        beSharePage.setPageSize(pageSize);
        beSharePage.setTotal(PageInfo.of(beSharePos).getTotal());
        beSharePage.setPages(PageInfo.of(beSharePos).getPages());
        return new ReturnObject<>(beSharePage);
    }

    /**
     * 创建分享活动
     * @return ShareActivityRetVo
     * created by 杨浩然 24320182203309
     **/
    public ReturnObject<VoObject> createShareActivity(ShareActivityVo vo, Long shopId, Long skuId) {
        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        //检查重复
        ShareActivityPoExample exmaple=new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria=exmaple.createCriteria();
        criteria.andGoodsSkuIdEqualTo(skuId);
        criteria.andShopIdEqualTo(shopId);
        List<ShareActivityPo> shareActivityPos=shareActivityPoMapper.selectByExample(exmaple);
        for(int i=0;i<shareActivityPos.size();i++){
            if(parseStringToDateTime(vo.getBeginTime()).isBefore(shareActivityPos.get(i).getEndTime())&&
                    parseStringToDateTime(vo.getEndTime()).isAfter(shareActivityPos.get(i).getBeginTime())){
                return new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT);
            }
        }

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
       if(shareActivityPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        shareActivityPo.setShopId(shopId);
        shareActivityPo.setBeginTime(parseStringToDateTime(vo.getBeginTime()));
        shareActivityPo.setEndTime(parseStringToDateTime(vo.getEndTime()));
        shareActivityPo.setStrategy(vo.getStrategy());

        //检查重复
        ShareActivityPoExample exmaple=new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria=exmaple.createCriteria();
        criteria.andGoodsSkuIdEqualTo(shareActivityPo.getGoodsSkuId());
        criteria.andShopIdEqualTo(shopId);
        List<ShareActivityPo> shareActivityPos=shareActivityPoMapper.selectByExample(exmaple);
        for(int i=0;i<shareActivityPos.size();i++){
            if(parseStringToDateTime(vo.getBeginTime()).isBefore(shareActivityPos.get(i).getEndTime())&&
                    parseStringToDateTime(vo.getEndTime()).isAfter(shareActivityPos.get(i).getBeginTime())){
                return new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT);
            }
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
     * 管理员上线分享活动
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param Id
     * @return
     **/
    public ReturnObject getShareOnline(Long userId, Long shopId, Long Id)
    {
        ShareActivityPo shareActivityPo = null;
        try {
            shareActivityPo=shareActivityPoMapper.selectByPrimaryKey(Id);
            if (shareActivityPo == null)
            {
                System.out.println("ShareDao -> getShareOffline 未找到指定的分享活动");
                logger.debug("ShareDao -> getShareOffline 未找到指定的分享活动");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("未找到活动无法上线"));
            }
            else { }
        } catch (DataAccessException e) {
            // 数据库错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
        }
        shareActivityPo.setState((byte)1);
        if(shareActivityPo.getShopId()!=shopId){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("操作活动不属于该店铺"));
        }
        shareActivityPoMapper.updateByPrimaryKeySelective(shareActivityPo);
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
        ShareActivityPo shareActivityPo = null;
        try {
             shareActivityPo=shareActivityPoMapper.selectByPrimaryKey(Id);
            if (shareActivityPo == null)
            {
                System.out.println("ShareDao -> getShareOffline 未找到指定的分享活动");
                logger.debug("ShareDao -> getShareOffline 未找到指定的分享活动");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("未找到活动无法下线"));
            }
            else {
                if(shareActivityPo.getShopId()!=shopId)
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("操作活动不属于该店铺"));
            }
        } catch (DataAccessException e) {
            // 数据库错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
        }
        shareActivityPo.setState((byte)0);
        shareActivityPoMapper.updateByPrimaryKeySelective(shareActivityPo);
        return new ReturnObject<>();
    }

    public boolean createBeShare(Long goodsSkuId, Long shareId, Long customerId) {
        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        BeSharePo beSharePo = new BeSharePo();
        beSharePo.setCustomerId(customerId);
        beSharePo.setGmtCreate(localDateTime);
        beSharePo.setGoodsSkuId(goodsSkuId);
        beSharePo.setShareId(shareId);
        SharePo sharePo=sharePoMapper.selectByPrimaryKey(shareId);
        if(sharePo==null) {
            return false;
        }
        beSharePo.setSharerId(sharePo.getSharerId());
        beSharePo.setShareActivityId(sharePo.getShareActivityId());
        int ret = beSharePoMapper.insert(beSharePo);
        if(ret==0) {
            return false;
        }
        return true;
    }

    public Integer rebate(Long orderItemId,Long beShareId, Integer quantity,Long price) {
        if (quantity <= 0) {
            return 0;
        }
        Integer rb = 0, ret = 0;
        BeSharePo beSharePo = new BeSharePo();
        SharePo sharePo = new SharePo();
        ShareActivityPo shareActivityPo = new ShareActivityPo();
        AfterSalePoExample exmaple = new AfterSalePoExample();
        AfterSalePoExample.Criteria criteria = exmaple.createCriteria();
        criteria.andOrderItemIdEqualTo(orderItemId);

        List<AfterSalePo> rets = afterSalePoMapper.selectByExample(exmaple);
        beSharePo = beSharePoMapper.selectByPrimaryKey(beShareId);
        //beShareId不存在
        if (beSharePo == null) {
            return 0;
        }
        sharePo = sharePoMapper.selectByPrimaryKey(beSharePo.getId());
        shareActivityPo = shareActivityPoMapper.selectByPrimaryKey(sharePo.getShareActivityId());
        CustomerPo customerPo = customerPoMapper.selectByPrimaryKey(beSharePo.getSharerId());
        //Beshare创建时间在活动结束之后，不计算返点
        if (beSharePo.getGmtCreate().isAfter(shareActivityPo.getEndTime())) {
            return 0;
        }
        //获取七天内无无理由退换的成功分享商品件数quantity
        for (int i = 0; i < rets.size(); i++) {
            Byte retCode = rets.get(i).getState();
            if (retCode != 6 && retCode != 7) {
                ret += rets.get(i).getQuantity();
            }
        }
        quantity -= ret;
        /**根据规则计算返点
         json：{“rule":[{"num":0,"rate":1}],"firstOrAvg":0,}
         rule:分享提成规则数组。若分享件数达到 `num`件，则获得 `rate` 的返点。其中，`num`的单位为`0.01`件，
         `num`取值为`200`时，表示`2`件。`rate`为返点，为整数。用户得到的返点为`件数 * 价格 * rate*0.01`
         **/
        Integer quantityBefore = sharePo.getQuantity();
        Integer quantityNow = quantityBefore + quantity;
        //Json格式字符串反序列化
        String strategyJson = shareActivityPo.getStrategy();
        if(strategyJson==null) {
            return 0;
        }
        Strategy strategy= JSON.parseObject(strategyJson,new TypeReference<Strategy>() {});
        System.out.println(strategy.getRule().toString());
        Integer limitLow = 0;
        int ruleNum = strategy.getRule().size();
        //找到quantityBefore对应的下限limitLow
        for (int i = 0; i < ruleNum; i++) {
            if (quantityBefore < strategy.getRule().get(i).getNum() / 100) {
                limitLow = i - 1;
                break;
            }
        }
        //已达到最高规则下限
        if (quantityBefore >= strategy.getRule().get(ruleNum - 1).getNum() / 100) {
            limitLow = ruleNum - 1;
        }
        int leftQuantity = quantity;
        int flag = 0;//判断是否已经减去quantityBefore
        while (leftQuantity > 0) {
            //表示还未达到需要达到最低的返点规则
            if (limitLow == -1) {
                leftQuantity -= (strategy.getRule().get(0).getNum() / 100 - quantityBefore);
                limitLow++;
                flag = 1;
            }
            //limitLow从0起
            else {
                //0,1,2<3 已经减去quantityBefore
                if (limitLow < ruleNum - 1 && flag == 1) {
                    leftQuantity -= (strategy.getRule().get(limitLow + 1).getNum() / 100 - strategy.getRule().get(limitLow).getNum() / 100);
                    if (leftQuantity < 0) {
                        leftQuantity += (strategy.getRule().get(limitLow + 1).getNum() / 100 - strategy.getRule().get(limitLow).getNum() / 100);
                        rb += (int) (leftQuantity * price * strategy.getRule().get(limitLow).getRate() * 0.01);
                        System.out.println(rb+"----1");
                        leftQuantity = 0;
                    } else {
                        rb += (int) ((strategy.getRule().get(limitLow + 1).getNum() / 100 - strategy.getRule().get(limitLow).getNum() / 100)  * price * strategy.getRule().get(limitLow).getRate() * 0.01);
                        System.out.println(rb+"----2");
                    }
                }
                //还没减去quantityBefore
                else if (limitLow < ruleNum - 1 && flag == 0) {
                    leftQuantity -= strategy.getRule().get(limitLow + 1).getNum() / 100 - quantityBefore;
                    if (leftQuantity < 0) {
                        leftQuantity += strategy.getRule().get(limitLow + 1).getNum() / 100 - quantityBefore;
                        rb += (int) (leftQuantity * price * strategy.getRule().get(limitLow).getRate() * 0.01);
                        System.out.println(rb+"----3");
                        leftQuantity = 0;
                    } else {
                        rb += (int) ((strategy.getRule().get(limitLow + 1).getNum() / 100 - quantityBefore)  * price * strategy.getRule().get(limitLow).getRate() * 0.01);
                        System.out.println(quantityBefore+"----4");
                        System.out.println(strategy.getRule().get(limitLow + 1).getNum()+"----4");
                        System.out.println(strategy.getRule().get(limitLow).getRate()+"----4");
                        System.out.println(quantityBefore+"----4");
                        System.out.println(rb+"----4");
                    }
                    flag = 1;
                }
                //达到最大分享返点率，limitLow=ruleNum-1,是最后一条规则下标
                else {
                    if (flag == 1) {
                        ;
                    } else {
                        leftQuantity -= quantityBefore;
                    }
                    rb += (int) (leftQuantity * price * strategy.getRule().get(limitLow).getRate() * 0.01);
                    System.out.println(leftQuantity+"----5");
                    System.out.println(strategy.getRule().get(limitLow).getRate()+"----5");
                    System.out.println(quantityBefore+"----5");
                    System.out.println(rb+"----5");
                    leftQuantity = 0;
                }
                limitLow++;
            }
        }
        //加入share表中成功返点件数
        sharePo.setQuantity(quantityNow);
        sharePo.setGmtModified(LocalDateTime.now());
        sharePoMapper.updateByPrimaryKey(sharePo);
        //更新beShare表
        beSharePo.setGmtModified(LocalDateTime.now());
        beSharePo.setRebate(rb);
        beSharePo.setOrderId(orderItemId);
        beSharePoMapper.updateByPrimaryKey(beSharePo);
        //更新customer表
        customerPo.setGmtModified(LocalDateTime.now());
        customerPo.setPoint(customerPo.getPoint() + rb);
        customerPoMapper.updateByPrimaryKey(customerPo);
        return rb;
    }

    public ShareDto updateBeShare(Long customerId, Long skuId, Long orderItemId) {
        BeSharePoExample example=new BeSharePoExample();
        BeSharePoExample.Criteria criteria=example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andGoodsSkuIdEqualTo(skuId);
        //criteria.andOrderIdEqualTo(null);
        criteria.andOrderIdIsNull();
        List<BeSharePo> beSharePos=beSharePoMapper.selectByExample(example);
        if(beSharePos.size()==0) {
            return new ShareDto(orderItemId,customerId,skuId,null);
        }
        BeSharePo po=beSharePos.get(0);
        po.setOrderId(orderItemId);
        beSharePoMapper.updateByPrimaryKey(po);
        return new ShareDto(po.getOrderId(),po.getCustomerId(),po.getGoodsSkuId(),po.getId());
    }
}
