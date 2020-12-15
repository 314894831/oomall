package cn.edu.xmu.other.model.vo.AfterSaleVo;

import cn.edu.xmu.other.model.bo.AfterSale;
import lombok.Data;

/**
 * 售后状态VO
 * @author Yhr3309
 * @date 2020/12/7 18:41
 */
@Data
public class StateVo {
    private Long Code;

    private String name;
    public StateVo(AfterSale.State state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
