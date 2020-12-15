package cn.edu.xmu.other.model.vo.FavouriteVo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FindFavouriteRetVo {
    private Long Id;
    private FindFavouriteSkuGoodRetVo findFavouriteSkuGoodRetVo;
    private LocalDateTime gmtCreate;
}
