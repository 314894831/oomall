package cn.edu.xmu.address.model.bo;

import cn.edu.xmu.address.model.po.AddressPo;
import cn.edu.xmu.address.model.po.RegionPo;
import cn.edu.xmu.address.model.vo.AddressRetVo;
import cn.edu.xmu.address.model.vo.AddressVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Address implements VoObject
{
    private Long id;
    private Long customerId;
    private Long regionId;
    private String detail;
    private String consignee;
    private String mobile;
    private Byte beDefault;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Address()
    {

    }

    public Address(AddressPo po)
    {
        this.id=po.getId();
        this.customerId=po.getCustomerId();
        this.regionId=po.getRegionId();
        this.detail=po.getDetail();
        this.consignee=po.getConsignee();
        this.mobile=po.getMobile();
        this.beDefault=po.getBeDefault();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }


    @Override
    public Object createVo()
    {
        AddressRetVo vo=new AddressRetVo();
        vo.setId(this.id);
        vo.setRegionId(this.regionId);
        vo.setDetail(this.detail);
        vo.setConsignee(this.consignee);
        vo.setMobile(this.mobile);
        vo.setBeDefault(this.beDefault);
        vo.setGmtCreate(this.gmtCreate);
        vo.setGmtModified(this.gmtModified);
        return vo;
    }

    @Override
    public Object createSimpleVo()
    {
        AddressPo po=new AddressPo();
        po.setId(this.id);
        po.setCustomerId(this.customerId);
        po.setRegionId(this.regionId);
        po.setDetail(this.detail);
        po.setConsignee(this.consignee);
        po.setMobile(this.mobile);
        po.setBeDefault(this.beDefault);
        po.setGmtCreate(this.gmtCreate);
        po.setGmtModified(this.gmtModified);

        return po;
    }

    public AddressPo createUpdatePo(AddressVo vo)
    {
        Long regionId=vo.getRegionId();
        String detail=vo.getDetail();
        String consignee=vo.getConsignee();
        String mobile=vo.getMobile();

        AddressPo po=new AddressPo();
        po.setId(this.id);
        po.setCustomerId(this.customerId);
        po.setRegionId(regionId);
        po.setDetail(detail);
        po.setConsignee(consignee);
        po.setMobile(mobile);
        po.setBeDefault(this.beDefault);
        po.setGmtCreate(this.gmtCreate);
        po.setGmtModified(LocalDateTime.now());

        return po;
    }
}
