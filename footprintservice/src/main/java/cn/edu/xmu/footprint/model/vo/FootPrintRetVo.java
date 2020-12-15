package cn.edu.xmu.footprint.model.vo;

import cn.edu.xmu.footprint.model.bo.FootPrint;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FootPrintRetVo {
    private Long customerId;
    private Long goodsSpuId;
    private LocalDateTime gmtCreate;



    public FootPrintRetVo(FootPrint footPrint)
    {
        this.customerId=footPrint.getCustomerId();
        this.goodsSpuId=footPrint.getGoodsSpuId();
        this.gmtCreate=footPrint.getGmtCreate();
    }
}
