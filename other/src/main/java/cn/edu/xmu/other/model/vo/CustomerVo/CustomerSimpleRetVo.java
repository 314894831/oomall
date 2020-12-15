package cn.edu.xmu.other.model.vo.CustomerVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户id与用户名
 * @author Shuhao Peng
 * Creat at 2020/11/30 15:37
 **/
@Data
@ApiModel
public class CustomerSimpleRetVo {
    @ApiModelProperty(name = "用户id", value = "id")
    private Long id;

    @ApiModelProperty(name = "用户名", value = "userName")
    private String userName;

    public CustomerSimpleRetVo() {
    }
}
