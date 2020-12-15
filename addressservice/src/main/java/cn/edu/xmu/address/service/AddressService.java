package cn.edu.xmu.address.service;

import cn.edu.xmu.address.dao.AddressDao;
import cn.edu.xmu.address.model.bo.Address;
import cn.edu.xmu.address.model.po.AddressPo;
import cn.edu.xmu.address.model.vo.AddressVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.AES;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService
{

    @Autowired
    AddressDao addressDao;

    /**
     * 买家新增收货地址
     *
     * @param address
     * @author ChengYang Li
     */
    public ReturnObject addAddress(Address address)
    {
        ReturnObject<Address> retObj = addressDao.insertAddress(address);
        return retObj;
    }

    /**
     * 根据ID设置默认地址
     *
     * @param id
     * @author ChengYang Li
     */
    public ReturnObject setDefaultAddressById(Long custumerId, Long id)
    {
        return addressDao.setDefaultAddressById(custumerId, id);
    }

    /**
     * 根据ID修改地址信息
     *
     * @param id
     * @author ChengYang Li
     */
    public ReturnObject updateAddressById(Long custumerId, Long id, AddressVo vo)
    {
        return addressDao.updateAddressById(custumerId, id, vo);
    }

    /**
     * 根据ID删除地址
     *
     * @param id
     * @author ChengYang Li
     */
    public ReturnObject deleteAddressById(Long custumerId, Long id)
    {
        return addressDao.deleteAddressById(custumerId, id);
    }

    /**
     * 买家获取自己的所有地址
     *
     * @param custumerId
     * @author ChengYang Li
     */
    public ReturnObject<PageInfo<VoObject>> getAllAddressesById(Long custumerId, Integer page, Integer pagesize)
    {
        PageHelper.startPage(page, pagesize);
        PageInfo<AddressPo> addressPos = addressDao.getAllAddressesById(custumerId, page, pagesize);

        List<VoObject> addresses = addressPos.getList().stream().map(Address::new).collect(Collectors.toList());

        PageInfo<VoObject> returnObject = new PageInfo<>(addresses);
        returnObject.setPages(addressPos.getPages());
        returnObject.setPageNum(addressPos.getPageNum());
        returnObject.setPageSize(addressPos.getPageSize());
        returnObject.setTotal(addressPos.getTotal());

        return new ReturnObject<>(returnObject);
    }
}
