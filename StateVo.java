package cn.edu.xmu.other.model.vo.AdvertisementVo;

import cn.edu.xmu.other.model.bo.Advertisement;
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
