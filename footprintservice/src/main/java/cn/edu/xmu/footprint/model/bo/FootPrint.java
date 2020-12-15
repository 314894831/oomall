package cn.edu.xmu.footprint.model.bo;

import cn.edu.xmu.footprint.model.po.FootPrintPo;
import cn.edu.xmu.footprint.model.vo.FootPrintRetVo;
import cn.edu.xmu.ooad.model.VoObject;
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
public class FootPrint implements VoObject {
    private Long id;
    private Long customerId;
    private Long goodsSpuId;
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
        this.goodsSpuId = po.getGoodsSpuId();
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
