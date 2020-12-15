package cn.edu.xmu.share.model.vo;
import cn.edu.xmu.share.model.bo.Share;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "分享者生成分享链接")
public class ShareRetVo {
    @ApiModelProperty(name = "分享链接的URL", value = "data")
    private String data;

    /**
     * 用Share对象建立Vo对象
     * @param share
     * @return ShareRetVo
     */
    public ShareRetVo(Share share) {
        this.data="/share/"+share.getId()+"/spus/"+share.getGoodsSpuId();
    }
///share/{sid}/spus/{id}

}
