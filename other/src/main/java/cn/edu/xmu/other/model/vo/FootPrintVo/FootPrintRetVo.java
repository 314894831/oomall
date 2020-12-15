package cn.edu.xmu.other.model.vo.FootPrintVo;

import cn.edu.xmu.other.model.bo.FootPrint;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FootPrintRetVo {
    private Long customerId;
    private Long goodsSkuId;
    private LocalDateTime gmtCreate;



    public FootPrintRetVo(FootPrint footPrint)
    {
        this.customerId=footPrint.getCustomerId();
        this.goodsSkuId=footPrint.getGoodsSkuId();
        this.gmtCreate=footPrint.getGmtCreate();
    }
}
