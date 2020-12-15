package cn.edu.xmu.other.model.vo.FootPrintVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "角色视图对象")
public class FootPrintVo {
    @NotNull(message = "商品spu_id不能为空")
    @ApiModelProperty(value = "商品spu_id")
    private Long goodsSpuId;
}
