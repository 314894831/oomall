package cn.edu.xmu.other.model.vo.ShoppingCartVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "添加购物车信息")
public class ShoppingCartVo {
    @NotNull(message = "SkuID不能为空")
    @ApiModelProperty(name = "商品SkuID", value = "goodSkuId")
    private Long goodSkuID;

    @NotNull(message = "数量不能为空")
    @ApiModelProperty(name = "添加数量", value = "quantity")
    private Integer quantity;


}
