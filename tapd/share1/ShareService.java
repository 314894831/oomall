package cn.edu.xmu.other.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dao.ShareDao;
import cn.edu.xmu.other.model.bo.BeShare;
import cn.edu.xmu.other.model.bo.Share;
import cn.edu.xmu.other.model.bo.ShareActivity;
import cn.edu.xmu.other.model.po.BeSharePo;
import cn.edu.xmu.other.model.po.ShareActivityPo;
import cn.edu.xmu.other.model.po.SharePo;
import cn.edu.xmu.other.model.vo.ShareVo.ShareActivityVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareService {
    @Autowired
    ShareDao shareDao;

    /**
     * 买家创建分享链接
     * @param userId 用户id
     * @param skuId
     * createdBy 杨浩然 24320182203309
     **/
    @Transactional
    public ReturnObject<VoObject> createShare(Long skuId, Long userId){
        ReturnObject<VoObject> ret = shareDao.createShare(skuId, userId);
        return ret;
    }

    /**
     * 买家获取所有share信息
     * @author yhr
     * @param userId
     * @param page
     * @param pagesize
     * @return share列表
     **/
    public ReturnObject<PageInfo<VoObject>> getShares(Integer page, Integer pagesize, Long goodsSkuId, String beginTime, String endTime, Long userId) {
        PageHelper.startPage(page, pagesize);
        PageInfo<SharePo> sharePos = shareDao.getShares(goodsSkuId,beginTime,endTime,userId);
        List<VoObject> shareItems = sharePos.getList().stream().map(Share::new).collect(Collectors.toList());
        PageInfo<VoObject> returnObject = new PageInfo<>(shareItems);
        returnObject.setPages(sharePos.getPages());
        returnObject.setPageNum(sharePos.getPageNum());
        returnObject.setPageSize(sharePos.getPageSize());
        returnObject.setTotal(sharePos.getTotal());
        return new ReturnObject<>(returnObject);
    }

    /**
     * 管理员获取所有share信息
     * @author yhr
     * @param page
     * @param pagesize
     * @return share列表
     **/
    public ReturnObject<PageInfo<VoObject>> adminGetShares(Integer page, Integer pagesize, Long goodsSkuId, Long id) {
        PageHelper.startPage(page, pagesize);
        PageInfo<SharePo> sharePos = shareDao.adminGetShares(goodsSkuId,id);
        List<VoObject> shareItems = sharePos.getList().stream().map(Share::new).collect(Collectors.toList());
        PageInfo<VoObject> returnObject = new PageInfo<>(shareItems);
        returnObject.setPages(sharePos.getPages());
        returnObject.setPageNum(sharePos.getPageNum());
        returnObject.setPageSize(sharePos.getPageSize());
        returnObject.setTotal(sharePos.getTotal());
        return new ReturnObject<>(returnObject);
    }

    /**
     * 买家获取所有beShared信息
     * @author yhr
     * @param userId
     * @param page
     * @param pagesize
     * @return share列表
     **/
    public ReturnObject<PageInfo<VoObject>> getBeShared(Integer page, Integer pagesize, Long goodsSkuId, String beginTime, String endTime, Long userId) {
        PageHelper.startPage(page, pagesize);
        PageInfo<BeSharePo> beSharePos = shareDao.getBeShared(goodsSkuId,beginTime,endTime,userId);
        List<VoObject> cartItems = beSharePos.getList().stream().map(BeShare::new).collect(Collectors.toList());
        PageInfo<VoObject> returnObject = new PageInfo<>(cartItems);
        returnObject.setPages(beSharePos.getPages());
        returnObject.setPageNum(beSharePos.getPageNum());
        returnObject.setPageSize(beSharePos.getPageSize());
        returnObject.setTotal(beSharePos.getTotal());
        return new ReturnObject<>(returnObject);
    }

    /*** 管理员获取所有beShared信息
     * @author yhr
     * @param page
     * @param pagesize
     * @return share列表
     **/
    public ReturnObject<PageInfo<VoObject>> adminGetBeShared(Long id,Integer page, Integer pagesize, Long goodsSkuId, String beginTime, String endTime) {
        PageHelper.startPage(page, pagesize);
        PageInfo<BeSharePo> beSharePos = shareDao.adminGetBeShared(id,goodsSkuId,beginTime,endTime);
        List<VoObject> cartItems = beSharePos.getList().stream().map(BeShare::new).collect(Collectors.toList());
        PageInfo<VoObject> returnObject = new PageInfo<>(cartItems);
        returnObject.setPages(beSharePos.getPages());
        returnObject.setPageNum(beSharePos.getPageNum());
        returnObject.setPageSize(beSharePos.getPageSize());
        returnObject.setTotal(beSharePos.getTotal());
        return new ReturnObject<>(returnObject);
    }

    /** 创建分享活动
     * createdBy 杨浩然 24320182203309
     **/
    @Transactional
    public ReturnObject<VoObject> createShareActivity(ShareActivityVo vo, Long shopId, Long skuId){
        ReturnObject<VoObject> ret = shareDao.createShareActivity(vo, shopId,skuId);
        return ret;
    }

    /**
     * 获取所有share活动信息
     * @author yhr
     * @param page
     * @param pagesize
     * @return share列表
     **/
    public ReturnObject<PageInfo<VoObject>> getShareActivities(Integer page, Integer pagesize, Long goodsSkuId, Long shopId) {
        PageHelper.startPage(page, pagesize);
        PageInfo<ShareActivityPo> shareActivityPos = shareDao.getShareActivities(goodsSkuId,shopId);
        List<VoObject> shareActivities = shareActivityPos.getList().stream().map(ShareActivity::new).collect(Collectors.toList());
        PageInfo<VoObject> returnObject = new PageInfo<>(shareActivities);
        returnObject.setPages(shareActivityPos.getPages());
        returnObject.setPageNum(shareActivityPos.getPageNum());
        returnObject.setPageSize(shareActivityPos.getPageSize());
        returnObject.setTotal(shareActivityPos.getTotal());
        return new ReturnObject<>(returnObject);
    }
    /**
     * 根据 ID ,shopId, ShareActivityVo 修改分享活动信息
     * @param id 分享活动 id
     * @param vo ShareActivityVo 对象
     * @return 返回对象 ReturnObject
     * @author 24320182203309 yhr
     * Created at 2020/12/5 20:30
     * Modified by 24320182203309 yhr at 2020/12/5 22:42
     **/
    @Transactional
    public ReturnObject<Object> modifyShareActivity(Long id, Long shopId, ShareActivityVo vo,Long userId) {
        return shareDao.modifyShareActivity(id,shopId, vo,userId);
    }

    /**
     * 管理员上线分享活动
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param Id
     * @return
     **/
    @Transactional
    public ReturnObject getShareOnline(Long userId, Long shopId, Long Id){
        ReturnObject ret = shareDao.getShareOnline(userId,shopId,Id);
        return ret;
    }
    /**
     * 管理员下线分享活动
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param Id
     * @return
     **/
    @Transactional
    public ReturnObject getShareOffline(Long userId, Long shopId, Long Id){
        ReturnObject ret = shareDao.getShareOffline(userId,shopId,Id);
        return ret;
    }
}
