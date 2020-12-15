package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.CustomerPo;
import cn.edu.xmu.other.model.vo.CustomerVo.CustomerRetVo;
import cn.edu.xmu.other.model.vo.CustomerVo.CustomerSimpleRetVo;
import cn.edu.xmu.other.model.vo.CustomerVo.CustomerVo;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 前台用户
 *
 * @author Shuhao Peng
 * @date Created in 2020/11/30 14:08
 **/
@Data
public class Customer implements VoObject
{

    /**
     * 前台用户状态
     */
    public enum State {
        BACK(0, "后台用户"),
        NORM(4, "正常用户"),
        FORBID(6, "禁止用户");

        private static final Map<Integer, State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Customer.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Customer.State getTypeByCode(Integer code) {
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

    private String username;

    private String password;

    private String realname;

    private String gender;

    private LocalDate birthday;

    private Integer point;

    private State state = State.NORM;

    private String email;

    private String mobile;

    private Boolean be_deleted=false;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    //private Long creatorId;//
    public Customer(){
        this.point=0;
        this.state = State.NORM;
        this.be_deleted=false;
        this.gmtCreate=LocalDateTime.now();
        this.gmtModified=LocalDateTime.now();
    }

    /**
     * 构造函数
     * @param po Po对象
     */
    public Customer(CustomerPo po){
        this.id = po.getId();
        this.username = po.getUserName();
        this.password =po.getPassword();
        this.realname = po.getRealName();
        this.gender=po.getGender().toString();
        this.birthday=po.getBirthday();
        this.point=po.getPoint();
        if (null != po.getState()) {
            this.state = State.getTypeByCode(po.getState().intValue());
        }
        this.email = po.getEmail();
        this.mobile = po.getMobile();
        if (null != po.getBeDeleted()) {
            this.be_deleted = po.getBeDeleted() ==1;
        }
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }


    /**
     * Create return Vo object
     * @author Shuhao Peng
     * @return
     */
    @Override
    public CustomerRetVo createVo() {
        CustomerRetVo customerRetVo = new CustomerRetVo();
        customerRetVo.setId(id);
        customerRetVo.setUserName(username);
        customerRetVo.setRealName(realname);
        customerRetVo.setMobile(mobile);
        customerRetVo.setEmail(email);
        customerRetVo.setGender(gender);
        if(birthday==null){
            customerRetVo.setBirthday(null);
        }else{
            customerRetVo.setBirthday(birthday.toString());
        }
        customerRetVo.setGmtCreate(gmtCreate.toString());
        customerRetVo.setGmtModified(gmtModified.toString());
        return customerRetVo;
    }

    /**
     * 用 CustomerEditVo 对象创建 用来更新 Customer 的 Po 对象
     * @param  vo 对象
     * @return po 对象
     */
    public CustomerPo createUpdatePo(CustomerVo vo) {
        String realnameEnc = vo.getRealName() == null ? null : vo.getRealName();
        String genderEnc = vo.getGender();
        String birthdayEnc = vo.getBirthday();
        Byte state = (byte) this.state.code;
        CustomerPo po = new CustomerPo();
        po.setId(id);
        po.setRealName(realnameEnc);
        if(genderEnc==null||genderEnc.isEmpty()){
            po.setGender((byte)(this.gender.charAt(0)-48));
        }
        else {
            po.setGender((byte)(genderEnc.charAt(0)-48));
        }
        try {
            if(birthdayEnc==null||birthdayEnc.isEmpty()){
                po.setBirthday(this.getBirthday());
            }
            else {LocalDate birthdayLocalDate = LocalDate.parse(birthdayEnc, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                po.setBirthday(birthdayLocalDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        po.setState(state);

        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());

        return po;
    }

    /**
     * 创建SimpleVo
     * @return customerSimpleRetVo
     * @author Shuhao Peng
     */
    @Override
    public CustomerSimpleRetVo createSimpleVo() {
        CustomerSimpleRetVo customerSimpleRetVo = new CustomerSimpleRetVo();
        customerSimpleRetVo.setId(this.id);
        customerSimpleRetVo.setUserName(this.username);

        return customerSimpleRetVo;
    }

    /**
     * 用bo对象创建更新po对象
     *
     * @author Shuhao Peng
     * @return CustomerPo
     * created 2020/12/02 17:03
     */
    public CustomerPo gotCustomerPo() {
        CustomerPo po = new CustomerPo();
        po.setId(this.getId());
        po.setUserName(this.getUsername());
        po.setPassword(this.getPassword());
        po.setRealName(this.getRealname());
        po.setGender((byte)(this.getGender().charAt(0)-48));
        po.setBirthday(this.getBirthday());
        po.setPoint(this.getPoint());
        po.setState(this.getState().getCode().byteValue());
        po.setEmail(this.getEmail());
        po.setMobile(this.getMobile());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());

        if(this.getBe_deleted().equals(true)){
            po.setBeDeleted((byte) 1);
        }
        else {
            po.setBeDeleted((byte) 0);
        }
        return po;
    }
}
