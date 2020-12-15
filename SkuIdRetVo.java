package cn.edu.xmu.other.model.vo.ShareVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "skuId返回信息")
public class SkuIdRetVo {

    @ApiModelProperty(name = "skuId", value = "id")
    private Long id;

    @ApiModelProperty(name = "skuName", value = "name")
    private String name;

    @ApiModelProperty(name = "sku编号", value = "skuSn")
    private String skuSn;

    @ApiModelProperty(name = "图片Url", value = "imageUrl")
    private String imageUrl;

    @ApiModelProperty(name = "库存", value = "inventory")
    private Integer inventory;

    @ApiModelProperty(name = "原价", value = "originalPrice")
    private Long originalPrice;

    @ApiModelProperty(name = "现价", value = "price")
    private Long price;

    @ApiModelProperty(name = "禁止访问", value = "disable")
    private Byte disable;

}
