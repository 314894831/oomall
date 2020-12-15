package cn.edu.xmu.advertise.model.bo;

import cn.edu.xmu.advertise.model.po.AdvertisementPo;
import cn.edu.xmu.advertise.model.vo.AdvertisementRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ChengYang Li
 */
@Data
public class Advertisement implements VoObject
{
    /**
     * 广告状态
     */
    public enum State {
        NEW(0, "审核"),
        ONLINE(4, "上架"),
        OFFLINE(6, "下架");

        private static final Map<Integer, Advertisement.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Advertisement.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Advertisement.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private Long id;
    private Long segId;
    private String link;
    private String content;
    private String imageUrl;
    private Byte state;
    private Integer weight;
    private LocalDate beginDate;
    private LocalDate endDate;
    private Byte repeats;
    private String message;
    private Byte beDefault;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Advertisement()
    {

    }


    public Advertisement(AdvertisementPo po)
    {
        this.id=po.getId();
        this.segId=po.getSegId();
        this.link=po.getLink();
        this.content=po.getContent();
        this.imageUrl=po.getImageUrl();
        this.state=po.getState();
        this.weight=po.getWeight();
        this.beginDate=po.getBeginDate();
        this.endDate=po.getEndDate();
        this.repeats=po.getRepeats();
        this.message=po.getMessage();
        this.beDefault=po.getBeDefault();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified= po.getGmtModified();
    }

    @Override
    public Object createVo()
    {
        AdvertisementRetVo vo=new AdvertisementRetVo();
        vo.setId(this.id);
        vo.setSegId(this.segId);
        vo.setLink(this.link);
        vo.setContent(this.content);
        vo.setImagePath(this.imageUrl);
        vo.setState(this.state);
        vo.setWeight(this.weight);
        vo.setBeginDate(this.beginDate);
        vo.setEndDate(this.endDate);
        vo.setRepeats(this.repeats);
        vo.setBeDefault(this.beDefault);
        vo.setGmtCreate(this.gmtCreate);
        vo.setGmtModified(this.gmtModified);

        return vo;
    }

    @Override
    public Object createSimpleVo()
    {
        AdvertisementPo po=new AdvertisementPo();
        po.setId(this.id);
        po.setSegId(this.segId);
        po.setLink(this.link);
        po.setContent(this.content);
        po.setImageUrl(this.imageUrl);
        po.setState(this.state);
        po.setWeight(this.weight);
        po.setBeginDate(this.beginDate);
        po.setEndDate(this.endDate);
        po.setRepeats(this.repeats);
        po.setMessage(this.message);
        po.setBeDefault(this.beDefault);
        po.setGmtCreate(this.gmtCreate);
        po.setGmtModified(this.gmtModified);

        return po;
    }
}
