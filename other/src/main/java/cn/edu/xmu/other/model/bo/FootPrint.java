package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.FootPrintPo;
import cn.edu.xmu.other.model.vo.FootPrintVo.FootPrintRetVo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 足迹Bo类
 *
 * @author 24320182203271 汤海蕴
 * createdBy 汤海蕴 2020/12/01 13:57
 * modifiedBy 汤海蕴 2020/12/01 19:20
 **/
@Data
public class FootPrint implements VoObject
{
    private Long id;
    private Long customerId;
    private Long goodsSkuId;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    public FootPrint(){
    }

    /**
     * 构造函数
     *
     * @author 24320182203271 汤海蕴
     * @param po 用PO构造
     * @return FootPrint
     * createdBy 汤海蕴 2020/12/01 13:57
     * modifiedBy 汤海蕴 2020/12/01 19:20
     */
    public FootPrint(FootPrintPo po) {
        this.id = po.getId();
        this.customerId = po.getCustomerId();
        this.goodsSkuId = po.getGoodsSkuId();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }


    @Override
    public Object createVo() {
        return new FootPrintRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
