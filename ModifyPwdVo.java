package cn.edu.xmu.other.model.vo.CustomerVo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@Data
@ApiModel(description = "修改密码对象")
public class ModifyPwdVo {

    private String captcha;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",message = "密码格式不正确，请包含大小写字母数字及特殊符号")
    @Length(min=6,message = "用户名密码长度至少为6位")
    private String newPassword;

}
