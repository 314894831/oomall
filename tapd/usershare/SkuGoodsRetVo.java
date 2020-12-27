package cn.edu.xmu.other.model.vo.ShareVo;

import lombok.Data;

@Data
public class SkuGoodsRetVo {
    Long id;
    String name;
    String skuSn;
    String imageUrl;
    Integer inventory;
    Long originalPrice;
    Long price;
    Boolean disable;
}
