package cn.edu.xmu.shoppingcart.model.vo;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@ApiModel(description = "优惠券返回信息")
public class CouponActivityRetVo {

    @ApiModelProperty(name = "优惠券id", value = "id")
    private Long id;

    @ApiModelProperty(name = "优惠券名称", value = "name")
    private String name;

    @ApiModelProperty(name = "优惠券开始时间", value = "beginTime")
    private String beginTime;

    @ApiModelProperty(name = "优惠券结束时间", value = "endTime")
    private String endTime;
}
