package cn.edu.xmu.advertise.model.vo;

import cn.edu.xmu.advertise.model.bo.Advertisement;
import lombok.Data;

/**
 * @author ChengYang Li
 */
@Data
public class StateVo {
    private Long Code;

    private String name;
    public StateVo(Advertisement.State state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
