package cn.edu.xmu.other.model.vo.CustomerVo;

import cn.edu.xmu.other.model.bo.Customer;
import lombok.Data;

/**
 * 用户状态VO
 * @author Shuhao Peng
 * @date 2020/12/03 17:15
 */
@Data
public class StateVo {
    private Long Code;

    private String name;
    public StateVo(Customer.State state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
