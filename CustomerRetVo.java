package cn.edu.xmu.other.model.vo.CustomerVo;

import lombok.Data;

@Data
public class CustomerRetVo {
    private Long id;
    private String userName;
    private String realName;
    private String mobile;
    private String email;
    private String gender;
    private String birthday;
    private String gmtCreate;
    private String gmtModified;
}
