package cn.edu.xmu.aftersale.dao;

import cn.edu.xmu.aftersale.mapper.AfterSalePoMapper;
import cn.edu.xmu.aftersale.model.bo.AfterSale;
import cn.edu.xmu.aftersale.model.po.AfterSalePo;
import cn.edu.xmu.aftersale.model.po.AfterSalePoExample;
import cn.edu.xmu.aftersale.model.vo.AfterSaleRetVo;
import cn.edu.xmu.aftersale.model.vo.LogSnVo;
import cn.edu.xmu.aftersale.model.vo.ResolutionVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Repository
public class AfterSaleDao
{
    private static final Logger logger = LoggerFactory.getLogger(AfterSaleDao.class);

    @Autowired
    AfterSalePoMapper afterSalePoMapper;

    /**
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param afterServiceId
     * @param vo
     * @return
     * 店家上传运单号
     * 店家寄出维修好的货物
     */
    @Transactional
    public ReturnObject ShopUploadLogSn(Long userId, Long shopId, Long afterServiceId, LogSnVo vo)
    {
        String logSn = vo.getLogsn();
        AfterSalePo ret = null;
        try {
            //System.out.println("准备mapper");
            AfterSalePoExample example = new AfterSalePoExample();
            AfterSalePoExample.Criteria criteria = example.createCriteria();
            criteria.andCustomerIdEqualTo(userId);
            criteria.andIdEqualTo(afterServiceId);
            criteria.andShopIdEqualTo(shopId);
            List<AfterSalePo> rets = afterSalePoMapper.selectByExample(example);
            ret = rets==null? rets.get(0) : null;
            System.out.println("mapper成功");
            if (ret == null)
            {
                //System.out.println("AfterSaleDao -> UploadLogSn 未找到指定的售后单或者ShopId有误");
                logger.debug("AfterSaleDao -> UploadLogSn 未找到指定的售后单或者ShopId有误");
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
        Byte state = ret.getState();
        if(ret.getType()!=2)
        {
            logger.debug("AfterSaleDao -> UploadLogSn 售后单的类型错误 为"+ret.getType());
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }
        if(state != (byte)4)
        {
            logger.debug("AfterSaleDao -> UploadLogSn 售后单的状态有误 为"+ret.getState());
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }
        ret.setShopLogSn(logSn);
        ret.setGmtModified(LocalDateTime.now());
        ret.setState((byte)5);
        afterSalePoMapper.updateByPrimaryKeySelective(ret);
        return new ReturnObject<>();
    }

    /**
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param afterSaleServiceId
     * @param vo
     * @return
     * 店家确认收货
     */
    @Transactional
    public ReturnObject ShopConfirmRecive(Long userId, Long shopId, Long afterSaleServiceId, ResolutionVo vo)
    {
        /**
         * @// TODO: 2020/12/7 与商品模块集成 校验权限 确认该userId为该店铺的管理员的id
         */

        //String logSn = vo.getLogsn();
        AfterSalePo ret = null;
        try {
            //System.out.println("准备mapper");
            AfterSalePoExample example = new AfterSalePoExample();
            AfterSalePoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(afterSaleServiceId);
            criteria.andShopIdEqualTo(shopId);
            List<AfterSalePo> rets = afterSalePoMapper.selectByExample(example);
            ret = rets==null? rets.get(0) : null;
            System.out.println("mapper成功");
            if (ret == null)
            {
                System.out.println("AfterSaleDao -> confirmAfterSale 未找到指定的售后单或者ShopId有误");
                logger.debug("AfterSaleDao -> confirmAfterSale 未找到指定的售后单或者ShopId有误");
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

        ret.setGmtModified(LocalDateTime.now());
        Byte state = ret.getState();
        Byte type = ret.getType();
        if(vo.isConfirm())
        {
            if(type==0)  //0换货
            {
                /**
                 * @// TODO: 2020/12/7 与订单模块集成 创建新的订单
                 */

            }
            else if (type==1)  //1退货
            {
                /**
                 * @// TODO: 2020/12/7 与订单模块集成 给与退款 同时进行测试
                 */
            }
            else if (type==2) //维修
            {

            }
        }
        ret.setConclusion(vo.getConclusion());
        afterSalePoMapper.updateByPrimaryKeySelective(ret);
        return new ReturnObject<>();
    }

    /**
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param afterSaleServiceId
     * @param vo
     * @return
     * 管理员审核售后
     */
    @Transactional
    public ReturnObject AdminCheckAfterSale(Long userId, Long shopId, Long afterSaleServiceId, ResolutionVo vo)
    {

        AfterSalePo ret = null;
        try {
            //System.out.println("准备mapper");
            AfterSalePoExample example = new AfterSalePoExample();
            AfterSalePoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(afterSaleServiceId);
            criteria.andShopIdEqualTo(shopId);
            List<AfterSalePo> rets = afterSalePoMapper.selectByExample(example);
            ret = rets==null? rets.get(0) : null;
            //System.out.println("mapper成功");
            if (ret == null)
            {
                //System.out.println("AfterSaleDao -> confirmAfterSale 未找到指定的售后单或者ShopId有误");
                logger.debug("AfterSaleDao -> confirmAfterSale 未找到指定的售后单或者ShopId有误");
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

        ret.setGmtModified(LocalDateTime.now());
        Byte state = ret.getState();
        Byte type = ret.getType();
        if(state!=0)
        {
            logger.debug("AfterSaleDao -> confirmAfterSale 该订单状态有误 state 不为0");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(vo.isConfirm())
        {
            ret.setState((byte)1);
        }
        else
        {
            ret.setState((byte)7);
        }
        ret.setConclusion(vo.getConclusion());
        afterSalePoMapper.updateByPrimaryKeySelective(ret);
        return new ReturnObject<>();
    }

    /**
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param afterSaleServiceId
     * @return
     * 买家根据售后单号查询售后
     */
    @Transactional
    public ReturnObject GetAfterSaleInfoById(Long userId, Long shopId, Long afterSaleServiceId)
    {

        AfterSalePo ret = null;
        try {
            AfterSalePoExample example = new AfterSalePoExample();
            AfterSalePoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(afterSaleServiceId);
            criteria.andShopIdEqualTo(shopId);
            criteria.andCustomerIdEqualTo(userId);
            List<AfterSalePo> rets = afterSalePoMapper.selectByExample(example);
            ret = rets==null? rets.get(0) : null;
            if (ret == null)
            {
                logger.debug("AfterSaleDao -> confirmAfterSale 未找到指定的售后单或者ShopId有误");
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
        ret.setGmtModified(LocalDateTime.now());
        AfterSaleRetVo vo = new AfterSaleRetVo(new AfterSale(ret));
        return new ReturnObject<>(vo);
    }

    /**
     * @author Kejian Shi
     * @param userId
     * @param Id
     * @return
     * 买家确认售后单结束
     */
    @Transactional
    public ReturnObject CustomerConfirmAfterSale(Long userId, Long Id)
    {

        AfterSalePo ret = null;
        try {
            //System.out.println("准备mapper");
            AfterSalePoExample example = new AfterSalePoExample();
            AfterSalePoExample.Criteria criteria = example.createCriteria();
            criteria.andCustomerIdEqualTo(userId);
            criteria.andIdEqualTo(Id);
            List<AfterSalePo> rets = afterSalePoMapper.selectByExample(example);
            ret = rets==null? rets.get(0) : null;
            //System.out.println("mapper成功");
            if (ret == null)
            {
                //System.out.println("AfterSaleDao -> confirmAfterSale 未找到指定的售后单或者ShopId有误");
                logger.debug("AfterSaleDao -> confirmAfterSale 未找到指定的售后单");
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

        ret.setGmtModified(LocalDateTime.now());
        ret.setState((byte)8);
        afterSalePoMapper.updateByPrimaryKeySelective(ret);
        return new ReturnObject<>();
    }

    /**
     * @author Kejian Shi
     * @param userId
     * @param afterSaleServiceId
     * @param vo
     * @return
     * 买家上传运单号
     */
    @Transactional
    public ReturnObject CustomerUploadLogSn(Long userId,Long afterSaleServiceId, LogSnVo vo)
    {
        String logSn = vo.getLogsn();
        AfterSalePo ret = null;
        try {
            //System.out.println("准备mapper");
            AfterSalePoExample example = new AfterSalePoExample();
            AfterSalePoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(afterSaleServiceId);
            criteria.andCustomerIdEqualTo(userId);
            List<AfterSalePo> rets = afterSalePoMapper.selectByExample(example);
            ret = rets==null? rets.get(0) : null;
            //System.out.println("mapper成功");
            if (ret == null)
            {
                //System.out.println("AfterSaleDao -> UploadLogSn 未找到指定的售后单或者ShopId有误");
                logger.debug("AfterSaleDao -> CustomerUploadLogSn 未找到指定的售后单或者ShopId有误");
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
        Byte state = ret.getState();
        if(ret.getType()==0)
        {
            logger.debug("AfterSaleDao -> CustomerUploadLogSn 售后单的类型错误 为"+ret.getType());
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }
        if(state != (byte)1)
        {
            logger.debug("AfterSaleDao -> CustomerUploadLogSn 售后单的状态有误 为"+ret.getState());
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }
        ret.setCustomerLogSn(vo.getLogsn());
        ret.setGmtModified(LocalDateTime.now());
        ret.setState((byte)2);
        afterSalePoMapper.updateByPrimaryKeySelective(ret);
        return new ReturnObject<>();
    }

    /**
     * @author Kejian Shi
     * @param userId
     * @param afterSaleServiceId
     * @return
     * 删除或者逻辑删除售后单
     */
    @Transactional
    public ReturnObject DeleteAfterSaleInfoById(Long userId,Long afterSaleServiceId)
    {
        AfterSalePo ret = null;
        try {
            //System.out.println("准备mapper");
            AfterSalePoExample example = new AfterSalePoExample();
            AfterSalePoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(afterSaleServiceId);
            criteria.andCustomerIdEqualTo(userId);
            List<AfterSalePo> rets = afterSalePoMapper.selectByExample(example);
            ret = rets==null? rets.get(0) : null;
            //System.out.println("mapper成功");
            if (ret == null)
            {
                //System.out.println("AfterSaleDao -> UploadLogSn 未找到指定的售后单或者ShopId有误");
                logger.debug("AfterSaleDao -> DeleteAfterSaleInfoById 未找到指定的售后单或者ShopId有误");
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
        Byte state = ret.getState();
        if(state==8)
        {
            ret.setBeDeleted((byte)1);
        }
        else
        {
            ret.setState((byte)7);
        }
        ret.setGmtModified(LocalDateTime.now());
        afterSalePoMapper.updateByPrimaryKeySelective(ret);
        return new ReturnObject<>();
    }


}
