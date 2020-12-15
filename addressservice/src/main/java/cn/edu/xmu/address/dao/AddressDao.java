package cn.edu.xmu.address.dao;

import cn.edu.xmu.address.mapper.AddressPoMapper;
import cn.edu.xmu.address.model.bo.Address;
import cn.edu.xmu.address.model.bo.Region;
import cn.edu.xmu.address.model.po.AddressPo;
import cn.edu.xmu.address.model.po.AddressPoExample;
import cn.edu.xmu.address.model.po.RegionPo;
import cn.edu.xmu.address.model.vo.AddressVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author ChengYang Li
 */
@Repository
public class AddressDao
{

    private static final Logger logger = LoggerFactory.getLogger(AddressDao.class);

    @Autowired
    AddressPoMapper addressPoMapper;

    /**
     * 买家新增收货地址
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject<Address> insertAddress(Address address)
    {
        AddressPo addressPo= (AddressPo) address.createSimpleVo();
        ReturnObject<Address> retObj = null;
        try{
            //新增地址前先检查该用户是否存在默认地址，如果不存在默认地址，则将改地址设为默认地址同时检查该用户是否已有20个地址
            AddressPoExample example = new AddressPoExample();
            AddressPoExample.Criteria criteria = example.createCriteria();
            criteria.andCustomerIdEqualTo(address.getCustomerId());
            List<AddressPo> addresses=addressPoMapper.selectByExample(example);
            //判断地址簿是否已经到达上限
            if(addresses.size()==20)
            {
                logger.debug("addAddress: insert address fail : 买家 id:"+address.getCustomerId()+" 地址簿达到上限");
                return new ReturnObject<>(ResponseCode.ADDRESS_OUTLIMIT, String.format("达到地址簿上限"));
            }

            addressPo.setBeDefault((byte)1);
            //查找该用户是否已经设置了默认地址
            for(int i=0; i<addresses.size(); i++)
            {
                //存在默认地址，设置为非默认地址
                if(addresses.get(i).getBeDefault().intValue()==1) {
                    addressPo.setBeDefault((byte) 0);
                    break;
                }
            }
            int ret = addressPoMapper.insertSelective(addressPo);
            if (ret == 0) {
                //插入失败
                logger.debug("addAddress: insert address fail " + addressPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + addressPo.getConsignee()));
            } else {
                //插入成功
                logger.debug("addAddress: insert address = " + addressPo.toString());
                address.setId(addressPo.getId());
                address.setBeDefault(addressPo.getBeDefault());
                retObj = new ReturnObject<>(address);
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
     * 根据 id 设置默认地址
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject setDefaultAddressById(Long custumerId, Long id)
    {
        AddressPoExample example = new AddressPoExample();
        AddressPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(custumerId);
        criteria.andIdEqualTo(id);
        List<AddressPo> pos=addressPoMapper.selectByExample(example);
        //不修改不存在的地址和其他人的地址
        if (pos.size()==0) {
            logger.info("地区不存在：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        AddressPo orig=pos.get(0);
        if(orig.getBeDefault().intValue()==0)
        {
            orig.setBeDefault((byte)1);
        }

        //检查该用户是否存在默认地址，如果存在，就将该默认地址设置为普通地址
        AddressPoExample example1 = new AddressPoExample();
        AddressPoExample.Criteria criteria1 = example1.createCriteria();
        criteria1.andCustomerIdEqualTo(custumerId);
        criteria1.andBeDefaultEqualTo((byte)1);
        List<AddressPo> addresses=addressPoMapper.selectByExample(example1);
        if(addresses.size()!=0)
        {
            AddressPo po=addresses.get(0);
            po.setBeDefault((byte)0);
            addressPoMapper.updateByPrimaryKeySelective(po);
        }

        //将修改为默认地址的地址存入数据库，更新数据库

        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = addressPoMapper.updateByPrimaryKeySelective(orig);
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
            logger.info("地址不存在：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("地址 id = " + id + " 已被设置为默认地址");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * 根据 id 修改地址信息
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject updateAddressById(Long custumerId, Long id, AddressVo vo)
    {
        AddressPoExample example = new AddressPoExample();
        AddressPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(custumerId);
        criteria.andIdEqualTo(id);
        List<AddressPo> pos=addressPoMapper.selectByExample(example);
        //不修改不存在的地址和他人的地址
        if (pos.size()==0) {
            logger.info("地址不存在：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        Address address=new Address(pos.get(0));
        AddressPo po=address.createUpdatePo(vo);

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret =addressPoMapper.updateByPrimaryKeySelective(po);
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
            logger.info("地址修改失败：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("地区 id = " + id + " 的信息已更新");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * 根据 id 删除地址
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public ReturnObject deleteAddressById(Long custumerId, Long id)
    {
        AddressPoExample example = new AddressPoExample();
        AddressPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(custumerId);
        criteria.andIdEqualTo(id);
        List<AddressPo> pos=addressPoMapper.selectByExample(example);
        //不删除不存在的地址和其他人的地址
        if (pos.size()==0) {
            logger.info("地址不存在：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret =addressPoMapper.deleteByExample(example);
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
            logger.info("地址删除失败：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("地址 id = " + id + " 已删除");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * 买家获取所有的收货地址
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     */
    public PageInfo<AddressPo> getAllAddressesById(Long custumerId, Integer page, Integer pagesize)
    {
        AddressPoExample example = new AddressPoExample();
        AddressPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(custumerId);

        List<AddressPo> addressPos = addressPoMapper.selectByExample(example);

        logger.debug("getAllAddressesById: retAddresses = "+addressPos);

        return new PageInfo<>(addressPos);
    }
}
