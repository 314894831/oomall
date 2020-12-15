package cn.edu.xmu.other.model.vo.FavouriteVo;

import cn.edu.xmu.other.model.bo.AddFavourite;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddFavouriteRetVo {
    private Long Id;
    private AddFavouriteSkuGoodRetVo addFavouriteSkuGoodRetVo;
    private LocalDateTime gmtCreate;
}
