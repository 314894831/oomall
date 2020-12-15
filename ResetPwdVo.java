package cn.edu.xmu.other.model.vo.CustomerVo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "重置密码对象")
public class ResetPwdVo {
    private String userName;
    private String email;
}
