package cn.edu.xmu.footprint.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 角色视图
 *
 * @author 24320182203271 汤海蕴
 * createdBy 汤海蕴 2020/12/01 13:57
 * modifiedBy 汤海蕴 2020/12/01 19:20
 **/
@Data
@ApiModel(description = "角色视图对象")
public class FootPrintVo {
    @NotNull(message = "商品spu_id不能为空")
    @ApiModelProperty(value = "商品spu_id")
    private Long goodsSpuId;
}
