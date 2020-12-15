package cn.edu.xmu.other.model.vo.AddressVo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ChengYang Li
 */
@Data
@ApiModel(description = "地址返回视图对象")
public class AddressRetVo
{
    private Long id;
    private Long regionId;
    private String detail;
    private String consignee;
    private String mobile;
    private Byte beDefault;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
