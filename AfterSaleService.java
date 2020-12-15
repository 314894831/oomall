package cn.edu.xmu.other.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dao.AfterSaleDao;
import cn.edu.xmu.other.model.bo.AfterSale;
import cn.edu.xmu.other.model.po.AfterSalePo;
import cn.edu.xmu.other.model.vo.AfterSaleVo.AfterSaleModifyVo;
import cn.edu.xmu.other.model.vo.AfterSaleVo.AfterSaleVo;
import cn.edu.xmu.other.model.vo.AfterSaleVo.LogSnVo;
import cn.edu.xmu.other.model.vo.AfterSaleVo.ResolutionVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**售后服务类
 * @author 24320182203309 杨浩然
 * createdBy yhr 2020/12/6 13:57
 * modifiedBy yhr 2020/12/6 19:20
 **/
@Service
public class AfterSaleService {

    @Autowired
    AfterSaleDao afterSaleDao;
    /**
     * 买家申请售后服务
     * @param userId 用户id
     * @param id 订单明细id
     * @param vo 传入vo
     * createdBy 杨浩然 24320182203309
     **/
    @Transactional
    public ReturnObject<VoObject> applyAfterSaleService(AfterSaleVo vo, Long id, Long userId){
        //TODO 根据订单明细id检验是否与userId对应
        ReturnObject<VoObject> ret = afterSaleDao.applyAfterSaleService(vo,id, userId);
        return ret;
    }

    /**
     * 买家获取所有售后单信息
     * @author yhr
     * @param userId
     * @param page
     * @param pagesize
     * @param spuId
     * @param skuId
     * @param beginTime
     * @param endTime
     * @param type
     * @param state
     * @return 售后单信息列表
     **/
    public ReturnObject<PageInfo<VoObject>> getAllAfterSale(Integer page, Integer pagesize, Long spuId, Long skuId, String beginTime, String endTime, Byte type, Byte state, Long userId) {
        PageHelper.startPage(page, pagesize);
        PageInfo<AfterSalePo> afterSalePos = afterSaleDao.getAllAfterSale(spuId,skuId,beginTime,endTime,type,state,userId);
        List<VoObject> afterSaleList = afterSalePos.getList().stream().map(AfterSale::new).collect(Collectors.toList());
        PageInfo<VoObject> returnObject = new PageInfo<>(afterSaleList);
        returnObject.setPages(afterSalePos.getPages());
        returnObject.setPageNum(afterSalePos.getPageNum());
        returnObject.setPageSize(afterSalePos.getPageSize());
        returnObject.setTotal(afterSalePos.getTotal());
        return new ReturnObject<>(returnObject);
    }

    /**
     * 买家获取所有售后单信息
     * @author yhr
     * @param id
     * @param page
     * @param pagesize
     * @param spuId
     * @param skuId
     * @param beginTime
     * @param endTime
     * @param type
     * @param state
     * @return 售后单信息列表
     **/
    public ReturnObject<PageInfo<VoObject>> adminGetAllAfterSales(Integer page, Integer pagesize, Long spuId, Long skuId, String beginTime, String endTime, Byte type, Byte state,Long id) {
        PageHelper.startPage(page, pagesize);
        PageInfo<AfterSalePo> afterSalePos = afterSaleDao.adminGetAllAfterSale(spuId,skuId,beginTime,endTime,type,state,id);
        List<VoObject> afterSaleList = afterSalePos.getList().stream().map(AfterSale::new).collect(Collectors.toList());
        PageInfo<VoObject> returnObject = new PageInfo<>(afterSaleList);
        returnObject.setPages(afterSalePos.getPages());
        returnObject.setPageNum(afterSalePos.getPageNum());
        returnObject.setPageSize(afterSalePos.getPageSize());
        returnObject.setTotal(afterSalePos.getTotal());
        return new ReturnObject<>(returnObject);
    }

    /**
     * 买家根据id获得售后单信息
     * @author yhr
     * @param id
     * @param userId
     * @return 售后单信息
     **/
    public ReturnObject<VoObject> getAfterSaleById(Long id, Long userId) {
        ReturnObject<VoObject> ret = afterSaleDao.getAfterSaleById(id, userId);
        return ret;
    }

    /**
     * 根据 id ,AfterSaleModifyVo 修改售后单信息
     * @param id 售后单id
     * @param vo AfterSaleModifyVo对象
     * @param userId
     * @return 返回对象 ReturnObject
     **/
    @Transactional
    public ReturnObject<Object> modifyAfterSaleById(Long id, AfterSaleModifyVo vo, Long userId) {
        return afterSaleDao.modifyAfterSaleById(id, vo,userId);
    }

    /**
     * 根据 id 取消售后单
     * @param id 售后单id
     * @param userId
     * @return 返回对象 ReturnObject
     **/
    @Transactional
    public ReturnObject<Object> deleteAfterSaleById(Long id, Long userId) {
        return afterSaleDao.deleteAfterSaleById(id,userId);
    }

    /**
     * author kejian shi
     * @param userId
     * @param shopId
     * @param afterSaleServiceId
     * @param vo
     * @return
     **/
    public ReturnObject ShopUploadLogSn(Long userId, Long shopId, Long afterSaleServiceId, LogSnVo vo)
    {
        return afterSaleDao.ShopUploadLogSn(userId, shopId, afterSaleServiceId, vo);
    }
    public ReturnObject ShopConfirmRecive(Long userId, Long shopId, Long afterSaleServiceId, ResolutionVo vo)
    {
        return afterSaleDao.ShopConfirmRecive(userId, shopId, afterSaleServiceId, vo);
    }

    public ReturnObject AdminCheckAfterSale(Long userId, Long shopId, Long afterSaleServiceId, ResolutionVo vo)
    {
        return afterSaleDao.AdminCheckAfterSale(userId, shopId, afterSaleServiceId, vo);
    }

    public ReturnObject<VoObject> GetAfterSaleInfoById(Long userId, Long shopId, Long afterSaleServiceId)
    {
        return afterSaleDao.GetAfterSaleInfoById(userId, shopId, afterSaleServiceId);
    }

    public ReturnObject CustomerConfirmAfterSale(Long userId, Long id)
    {
        return afterSaleDao.CustomerConfirmAfterSale(userId, id);
    }

    public ReturnObject CustomerUploadLogSn(Long userId, Long id, LogSnVo vo)
    {
        return afterSaleDao.CustomerUploadLogSn(userId, id, vo);
    }

    public ReturnObject DeleteAfterSaleInfoById(Long userId, Long id)
    {
        return afterSaleDao.DeleteAfterSaleInfoById(userId, id);
    }
}
