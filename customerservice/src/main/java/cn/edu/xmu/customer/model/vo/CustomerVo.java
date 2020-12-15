package cn.edu.xmu.customer.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 用户信息 Vo
 * @author Shuhao Peng
 * Created at 2020/11/30 15:03
 **/
@Data
@ApiModel(description = "买家用户信息视图对象")
public class CustomerVo {

    @ApiModelProperty(value = "用户真实姓名")
    private String realName;

    @ApiModelProperty(value = "用户性别")
    private String gender;

    @ApiModelProperty(value = "用户生日")
    private String birthday;

}
