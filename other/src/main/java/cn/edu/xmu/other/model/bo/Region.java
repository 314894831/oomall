package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.RegionPo;
import cn.edu.xmu.other.model.vo.AddressVo.RegionVo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Region implements VoObject
{
    private Long id;
    private Long pid;
    private String name;
    private Long postal_code;
    private Byte state;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;

    public Region()
    {

    }

    public Region(RegionPo po)
    {
        this.id=po.getId();
        this.pid=po.getPid();
        this.name=po.getName();
        this.postal_code=po.getPostalCode();
        this.state=po.getState();
        this.gmtCreated=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo()
    {
        RegionPo po=new RegionPo();
        po.setId(this.id);
        po.setName(this.name);
        po.setPid(this.pid);
        po.setPostalCode(this.postal_code);
        po.setState(this.state);
        po.setGmtCreate(this.gmtCreated);
        po.setGmtModified(this.gmtModified);
        return po;
    }

    @Override
    public Object createSimpleVo()
    {
        return null;
    }

    public RegionPo createUpdatePo(RegionVo vo)
    {
        String name = vo.getName();
        Long postal_code=vo.getPostalCode();

        RegionPo po=new RegionPo();
        po.setId(id);
        po.setName(name);
        po.setPid(pid);
        po.setPostalCode(postal_code);
        po.setState(this.state);
        po.setGmtCreate(this.gmtCreated);
        po.setGmtModified(LocalDateTime.now());

        return po;
    }

    public RegionPo createNewPo(RegionVo vo)
    {
        String name = vo.getName();
        Long postal_code=vo.getPostalCode();

        RegionPo po=new RegionPo();
        po.setName(name);
        po.setPid(this.id);
        po.setPostalCode(postal_code);
        po.setState(this.state);
        po.setGmtCreate(this.gmtCreated);

        return po;
    }
}
