package cn.edu.xmu.other.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.mapper.AfterSalePoMapper;
import cn.edu.xmu.other.model.bo.AfterSale;
import cn.edu.xmu.other.model.po.AfterSalePo;
import cn.edu.xmu.other.model.po.AfterSalePoExample;
import cn.edu.xmu.other.model.vo.AfterSaleVo.*;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** @author 杨浩然
 * @date Created in 2020/12/6 14:08
 * Modified by 24320182203309 杨浩然 at 2020/12/6 15:00
 **/
@Repository
public class AfterSaleDao {

    private static final Logger logger = LoggerFactory.getLogger(AfterSaleDao.class);

    @Autowired
    private AfterSalePoMapper afterSalePoMapper;

    //String转为LocalDateTime
    public static LocalDateTime parseStringToDateTime(String time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, df);
    }
    /**
     * 由 vo和id和user Id,订单id 申请售后服务
     * @param  vo, id, userid
     * @return AfterSaleRetVo
     * created by 杨浩然 24320182203309
     **/
    public ReturnObject<VoObject> applyAfterSaleService(AfterSaleVo vo, Long id, Long userId) {
        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        AfterSalePo afterSalePo = new AfterSalePo();
        afterSalePo.setOrderItemId(id);
        afterSalePo.setCustomerId(userId);
        afterSalePo.setQuantity(vo.getQuantity());
        afterSalePo.setType(vo.getType());
        afterSalePo.setReason(vo.getReason());
        afterSalePo.setRegionId(vo.getRegionId());
        afterSalePo.setDetail(vo.getDetail());
        afterSalePo.setConsignee(vo.getConsignee());
        afterSalePo.setMobile(vo.getMobile());
        afterSalePo.setGmtCreate(localDateTime);
        afterSalePo.setGmtModified(localDateTime);
        try {
            System.out.println("准备mapper");
            int ret = afterSalePoMapper.insert(afterSalePo);
            System.out.println("mapper成功");
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
        AfterSale afterSale = new AfterSale(afterSalePo);
        return new ReturnObject<VoObject>(afterSale);
    }

    /**买家获取所有售后单信息
     * @author yhr
     * @return List<AfterSalePo> 售后单列表
     **/
    public PageInfo<AfterSalePo> getAllAfterSale(Long spuId, Long skuId, String beginTime, String endTime, Byte type, Byte state, Long userId) {
        AfterSalePoExample example = new AfterSalePoExample();
        AfterSalePoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        //TODO spuId获取到哪
        if(spuId!=null)
            //criteria.and(spuId)
        {
            ;
        }
        //TODO spuId获取到哪
        if(skuId!=null)
            //criteria.and(spuId);
        {
            ;
        }
        //TODO skuId获取到哪
        if(beginTime.length()!=0&&endTime.length()!=0) {
            criteria.andGmtCreateBetween(parseStringToDateTime(beginTime),parseStringToDateTime(endTime));
        }
        if(type!=null) {
            criteria.andTypeEqualTo(type);
        }
        if(state!=null) {
            criteria.andStateEqualTo(state);
        }
        List<AfterSalePo> afterSales = afterSalePoMapper.selectByExample(example);
        logger.debug("getAllAfterSale: AfterSales = "+afterSales);
        return new PageInfo<>(afterSales);
    }

    /**管理员获取所有售后单信息
     * @author yhr
     * @return List<AfterSalePo> 售后单列表
     **/
    public PageInfo<AfterSalePo> adminGetAllAfterSale(Long spuId, Long skuId, String beginTime, String endTime, Byte type, Byte state,Long id) {
        AfterSalePoExample example = new AfterSalePoExample();
        AfterSalePoExample.Criteria criteria = example.createCriteria();
        criteria.andShopIdEqualTo(id);
        //TODO spuId获取到哪
        if(spuId!=null)
            //criteria.and(spuId)
        {
            ;
        }
        //TODO spuId获取到哪
        if(skuId!=null)
            //criteria.and(spuId);
        {
            ;
        }
        //TODO skuId获取到哪
        if(beginTime.length()!=0&&endTime.length()!=0) {
            criteria.andGmtCreateBetween(parseStringToDateTime(beginTime),parseStringToDateTime(endTime));
        }
        if(type!=null) {
            criteria.andTypeEqualTo(type);
        }
        if(state!=null) {
            criteria.andStateEqualTo(state);
        }
        List<AfterSalePo> afterSales = afterSalePoMapper.selectByExample(example);
        logger.debug("getAllAfterSale: AfterSales = "+afterSales);
        return new PageInfo<>(afterSales);
    }

    /**买家根据id获取售后单信息
     * @author yhr
     * @return AfterSalePo 售后单信息
     **/
    public ReturnObject<VoObject> getAfterSaleById(Long id, Long userId) {
        AfterSalePo afterSalePo = afterSalePoMapper.selectByPrimaryKey(id);
        if(afterSalePo==null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("找不到该订单"));
        }
        if(afterSalePo.getCustomerId().equals(userId))
        {
            AfterSale afterSale = new AfterSale(afterSalePo);
            return new ReturnObject<VoObject>(afterSale);
        }
        //查询的订单编号不对应该买家
        else {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("发生了错误：查询的订单编号不对应该买家"));
        }

    }

    /**根据 订单id 修改售后单信息
     * @param vo 传入的AfterSaleModifyVo对象
     * @return 返回对象 ReturnObj
     * @author 24320182203309 yhr
     * Created at 2020/12/7 20:30
     * Modified by 24320182203309 yhr at 2020/12/7 22:42
     **/
    public ReturnObject<Object> modifyAfterSaleById(Long id, AfterSaleModifyVo vo, Long userId) {
        AfterSalePo afterSalePo = afterSalePoMapper.selectByPrimaryKey(id);
        if(afterSalePo==null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("找不到该订单"));
        }
        if(afterSalePo.getCustomerId().equals(userId))
        {
            afterSalePo.setConsignee(vo.getConsignee());
            afterSalePo.setMobile(vo.getMobile());
            afterSalePo.setRegionId(vo.getRegionId());
            afterSalePo.setDetail(vo.getDetail());
            afterSalePo.setQuantity(vo.getQuantity());
            afterSalePo.setReason(vo.getReason());
            // 更新数据库
            ReturnObject<Object> retObj = null;
            int ret;
            try {
                ret = afterSalePoMapper.updateByPrimaryKey(afterSalePo);
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
            return retObj;
        }
        else {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("发生了错误：修改的订单编号不对应该买家"));
        }
    }

    /**根据 订单id 取消售后单
     * @return 返回对象 ReturnObj
     * @author 24320182203309 yhr
     * Created at 2020/12/7 20:30
     * Modified by 24320182203309 yhr at 2020/12/7 22:42
     **/
    public ReturnObject<Object> deleteAfterSaleById(Long id, Long userId) {
        AfterSalePo afterSalePo = afterSalePoMapper.selectByPrimaryKey(id);
        if(afterSalePo==null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("找不到该订单"));
        }
        if(afterSalePo.getCustomerId().equals(userId))
        {
            afterSalePo.setState((byte)7);
            // 更新数据库
            ReturnObject<Object> retObj = null;
            int ret;
            try {
                ret = afterSalePoMapper.updateByPrimaryKey(afterSalePo);
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
            return retObj;
        }
        else {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("发生了错误：取消的订单编号不对应该买家"));
        }
    }

    /**
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param afterServiceId
     * @param vo
     * @return
     */
    @Transactional
    public ReturnObject UploadLogSn(Long userId, Long shopId, Long afterServiceId, LogSnVo vo)
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
            logger.debug("AfterSaleDao -> UploadLogSn 售后单的状态有无 为"+ret.getState());
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
     * @param afterServiceId
     * @param vo
     * @return
     */

    @Transactional
    public ReturnObject confirmAfterSale(Long userId, Long shopId, Long afterServiceId, ResolutionVo vo)
    {
        /**
         * @// TODO: 2020/12/7 校验权限 确认该userId为该店铺的管理员的id
         */
        //String logSn = vo.getLogsn();
        AfterSalePo ret = null;
        try {
            //System.out.println("准备mapper");
            AfterSalePoExample example = new AfterSalePoExample();
            AfterSalePoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(afterServiceId);
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
     * @param afterServiceId
     * @param vo
     * @return
     */
    @Transactional
    public ReturnObject AdminConfirmAfterSale(Long userId, Long shopId, Long afterServiceId, ResolutionVo vo)
    {

        AfterSalePo ret = null;
        try {
            //System.out.println("准备mapper");
            AfterSalePoExample example = new AfterSalePoExample();
            AfterSalePoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(afterServiceId);
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
