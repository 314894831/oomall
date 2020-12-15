package cn.edu.xmu.other.model.vo.FavouriteVo;

import lombok.Data;

@Data
public class FindFavouriteSkuGoodRetVo
{
    private Long id;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Integer inventory;
    private Long originalPrice;
    private Long price;
    private Byte disable;
}
