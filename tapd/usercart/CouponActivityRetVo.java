package cn.edu.xmu.other.model.vo.ShoppingCartVo;

import cn.edu.xmu.goods.client.dubbo.CouponActivityDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public CouponActivityRetVo()
    {

    }

    public CouponActivityRetVo(CouponActivityDTO dto)
    {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.id=dto.getId();
        this.name=dto.getName();
        this.beginTime=dto.getBeginTime().format(df);
        this.endTime=dto.getEndTime().format(df);
    }
}
