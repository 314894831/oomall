package cn.edu.xmu.other.model.vo.AddressVo;

import cn.edu.xmu.other.model.bo.Address;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 地址视图
 *
 * @author ChengYang Li
 **/
@Data
@ApiModel(description = "地址视图对象")
public class AddressVo
{
    @NotNull(message = "地区id不能为空")
    @ApiModelProperty(value = "地区id")
    private Long regionId;

    @NotBlank(message = "地址详情不能为空")
    @ApiModelProperty(value = "地址详情")
    private String detail;

    @NotBlank(message = "联系人姓名不能为空")
    @ApiModelProperty(value = "联系人姓名")
    private String consignee;

    @NotBlank(message = "联系人电话不能为空")
    @ApiModelProperty(value = "联系人电话")
    @Pattern(regexp="[+]?[0-9*#]+",message="手机号格式不正确")
    private String mobile;

    public Address createAddress()
    {
        Address address=new Address();
        address.setDetail(this.detail);
        address.setRegionId(this.regionId);
        address.setConsignee(this.consignee);
        address.setMobile(this.mobile);
        return address;
    }
}
