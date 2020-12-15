package cn.edu.xmu.customer.model.vo;

import cn.edu.xmu.customer.model.bo.Customer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 新用户VO
 * @author Shuhao Peng
 * @date 2020/11/28 17:11
 */
@Data
public class NewCustomerVo {

    @Pattern(regexp = "[+]?[0-9*#]+",
            message = "手机号格式不正确")
    @ApiModelProperty(value = "用户电话")
    private String mobile;

    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "Email格式不正确")
    @ApiModelProperty(value = "用户邮箱")
    private String email;

    @ApiModelProperty(value = "用户昵称")
    private String username;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",message = "密码格式不正确，请包含大小写字母数字及特殊符号")
    @Length(min=6,message = "用户名密码长度至少为6位")
    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "用户真实姓名")
    private String realname;

    @ApiModelProperty(value = "用户性别")
    private String gender;

    @ApiModelProperty(value = "用户出生日期")
    private String birthday;

    /**
     * 构造函数
     *
     * @author Shuhao Peng
     * @return Customer
     * created  2020/12/02 16:43
     */
    public Customer createCustomer() {
        Customer customer = new Customer();
        customer.setMobile(this.mobile);
        customer.setEmail(this.email);
        customer.setUsername(this.username);
        customer.setPassword(this.password);
        customer.setRealname(this.realname);
        customer.setGender(this.gender);
        try {
            LocalDate birthdayLocalDate = LocalDate.parse(this.birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            customer.setBirthday(birthdayLocalDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return customer;
    }
}
