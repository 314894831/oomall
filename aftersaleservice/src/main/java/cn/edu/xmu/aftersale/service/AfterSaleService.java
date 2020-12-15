package cn.edu.xmu.aftersale.service;

import cn.edu.xmu.aftersale.dao.AfterSaleDao;
import cn.edu.xmu.aftersale.model.vo.LogSnVo;
import cn.edu.xmu.aftersale.model.vo.ResolutionVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public class AfterSaleService
{
    private static final Logger logger = LoggerFactory.getLogger(AfterSaleService.class);
    @Autowired
    AfterSaleDao afterSaleDao;

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
