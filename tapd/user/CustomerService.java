package cn.edu.xmu.other.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dao.CustomerDao;
import cn.edu.xmu.other.model.bo.Customer;
import cn.edu.xmu.other.model.po.CustomerPo;
import cn.edu.xmu.other.model.vo.CustomerVo.CustomerVo;
import cn.edu.xmu.other.model.vo.CustomerVo.ModifyPwdVo;
import cn.edu.xmu.other.model.vo.CustomerVo.ResetPwdVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 买家服务
 * @author Shuhao Peng
 * Modified at 2020/11/28 16:52
 **/
@Service
public class CustomerService{
    private Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    CustomerDao customerDao;

    @Value("${privilegeservice.login.jwtExpire}")
    private Integer jwtExpireTime;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Value("${privilegeservice.login.multiply}")
    private Boolean canMultiplyLogin;

    /**
     * 分布式锁的过期时间（秒）
     */
    @Value("${privilegeservice.lockerExpireTime}")
    private long lockerExpireTime;

    /**
     * @param customer 注册的bo对象
     * @return ReturnObject
     * @author Shuhao Peng
     */
    @Transactional
    public ReturnObject register(Customer customer) {
        return customerDao.createCustomer(customer);
    }

    /**
     * 用户登录
     * @param userName 用户名
     * @param password 密码
     * @return ReturnObject
     * @author Shuhao Peng
     * @creat at 2020/12/03 08：24
     */
    @Transactional
    public ReturnObject login(String userName, String password)
    {
        ReturnObject retObj = customerDao.getCustomerByName(userName);
        if (retObj.getCode() != ResponseCode.OK){
            return retObj;
        }

        Customer customer = (Customer) retObj.getData();

        if(customer == null || !password.equals(customer.getPassword())){
            retObj = new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
            return retObj;
        }
        if (customer.getState() != Customer.State.NORM){
            retObj = new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
            return retObj;
        }

        String key = "cus_" + customer.getId();
        logger.debug("login: key = "+ key);

        //创建新的token
        JwtHelper jwtHelper = new JwtHelper();
        String jwt = jwtHelper.createToken(customer.getId(),-2L,jwtExpireTime);

        redisTemplate.opsForSet().add(key,jwt);
        logger.debug("login: newJwt = "+ jwt);
        retObj = new ReturnObject<>(jwt);

        return retObj;
    }

    /**
     * 用户登出
     * @param customerId 用户Id
     * @return ReturnObject
     * @author Shuhao Peng
     * @creat at 2020/12/05 12：47
     */
    public ReturnObject<Boolean> logout(Long customerId)
    {
        redisTemplate.delete("cus_" + customerId);
        return new ReturnObject<>(true);
    }

    /**
     * 根据ID获取用户信息
     * @param id
     * @return 用户
     * createdBy: Shuhao Peng 2020-11-28 17:41
     */
    public ReturnObject<VoObject> findCustomerById(Long id) {
        ReturnObject<VoObject> returnObject = null;

        CustomerPo customerPo = customerDao.findCustomerById(id);
        if(customerPo != null) {
            logger.debug("findCustomerById : " + returnObject);
            returnObject = new ReturnObject<>(new Customer(customerPo));
        } else {
            logger.debug("findCustomerrById: Not Found");
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            //returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        return returnObject;
    }

    /**
     * 根据 ID 和 CustomerVo 修改任意用户信息
     * @param id 用户 id
     * @param vo CustomerVo 对象
     * @return 返回对象 ReturnObject
     * @author Shuhao Peng
     * Created at 2020/12/05 9:57
     */
    @Transactional
    public ReturnObject<Object> modifyCustomerInfo(Long id, CustomerVo vo) {
        return customerDao.modifyCustomerByVo(id, vo);
    }

    /**
     * 获取所有用户信息
     * @param userName
     * @param email
     * @param mobile
     * @param page
     * @param pagesize
     * @return 用户列表
     * @author Shuhao Peng
     * Created at 2020/12/05 13:18
     */
    public ReturnObject<PageInfo<VoObject>> findAllCustomers(String userName, String email, String mobile, Integer page, Integer pagesize) {

        String userNameFind = userName.isBlank() ? "" : userName;
        String emailFind = email.isBlank() ? "" : email;
        String mobileFind = mobile.isBlank() ? "" : mobile;

        PageHelper.startPage(page, pagesize);
        PageInfo<CustomerPo> customerPos = customerDao.findAllCustomers(userNameFind, emailFind,mobileFind, page, pagesize);

        List<VoObject> customers = customerPos.getList().stream().map(Customer::new).collect(Collectors.toList());

        PageInfo<VoObject> returnObject = new PageInfo<>(customers);
        returnObject.setPages(customerPos.getPages());
        returnObject.setPageNum(customerPos.getPageNum());
        returnObject.setPageSize(customerPos.getPageSize());
        returnObject.setTotal(customerPos.getTotal());

        return new ReturnObject<>(returnObject);
    }

    /**
     * 根据 id 禁止任意用户
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     * @author Shuhao Peng
     * Created at 2020/12/5 15:07
     */
    @Transactional
    public ReturnObject<Object> forbidCustomer(Long id) {
        return customerDao.changeCustomerState(id, Customer.State.FORBID);
    }

    /**
     * 根据 id 解禁任意用户
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     * @author Shuhao Peng
     * Created at 2020/12/5 16:14
     */
    @Transactional
    public ReturnObject<Object> releaseCustomer(Long id) {
        return customerDao.changeCustomerState(id, Customer.State.NORM);
    }

    /**
     * 用户重置密码
     * @param vo 重置密码对象
     * @param ip 请求ip地址
     * @author Shuhao Peng
     * Created at 2020/12/7 14:12
     */
    @Transactional
    public ReturnObject<Object> resetPassword(ResetPwdVo vo, String ip) {
        return customerDao.resetPassword(vo,ip);
    }

    /**
     * 用户修改密码
     * @param vo 修改密码对象
     * @author Shuhao Peng
     * Created at 2020/12/7 14:56
     */
    @Transactional
    public ReturnObject<Object> modifyPassword(ModifyPwdVo vo) {
        return customerDao.modifyPassword(vo);
    }
}
