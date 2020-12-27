package cn.edu.xmu.other.model.vo.ShareVo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.bo.GetShare;
import cn.edu.xmu.other.model.bo.Share;
import cn.edu.xmu.other.model.vo.ShoppingCartVo.ShoppingCartRetVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.dubbo.config.annotation.DubboReference;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "买家查询所有分享记录")
public class GetShareRetVo implements VoObject {
    @ApiModelProperty(name = "分享链接的id", value = "id")
    private Long id;
    @ApiModelProperty(name = "分享者的id", value = "sharerId")
    private Long sharerId;
    @ApiModelProperty(name = "成功返点的商品件数", value = "quantity")
    private Integer quantity;
    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private LocalDateTime gmtCreate;

    SkuGoodsRetVo sku;

    public GetShareRetVo(){}
    /**
     * 用Share对象建立Vo对象
     * @param share
     * @return ShareRetVo
     */
    public GetShareRetVo(GetShare share) {
        this.id=share.getId();
        this.sharerId=share.getSharerId();
        this.quantity=share.getQuantity();
        this.gmtCreate=share.getGmtCreate();
        //用DTO 生成
        this.sku=share.getSkuGoodsRetVo();

    }

    @Override
    public Object createVo() {
        GetShareRetVo vo=new GetShareRetVo();
        vo.setId(this.id);
        vo.setSharerId(this.sharerId);
        vo.setGmtCreate(this.gmtCreate);
        vo.setQuantity(this.quantity);
        vo.setSharerId(this.sharerId);
        vo.setSku(this.sku);
        return vo;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
