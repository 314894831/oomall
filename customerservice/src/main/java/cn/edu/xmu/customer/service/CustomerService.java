package cn.edu.xmu.customer.service;

import cn.edu.xmu.customer.dao.CustomerDao;
import cn.edu.xmu.customer.model.bo.Customer;

import cn.edu.xmu.customer.model.po.CustomerPo;
import cn.edu.xmu.customer.model.vo.CustomerVo;
import cn.edu.xmu.customer.model.vo.ModifyPwdVo;
import cn.edu.xmu.customer.model.vo.ResetPwdVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.AES;
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
import java.util.Set;
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
     * @param ipAddr IP地址
     * @return ReturnObject
     * @author Shuhao Peng
     * @creat at 2020/12/03 08：24
     */
    @Transactional
    public ReturnObject login(String userName, String password, String ipAddr)
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

        String key = "up_" + customer.getId();
        logger.debug("login: key = "+ key);
        /*
        if(redisTemplate.hasKey(key) && !canMultiplyLogin){
            logger.debug("login: multiply  login key ="+key);
            // 用户重复登录处理
            Set<Serializable> set = redisTemplate.opsForSet().members(key);
            redisTemplate.delete(key);

            // 将旧JWT加入需要踢出的集合
            String jwt = null;
            for (Serializable str : set) {
                // 找出JWT
                if((str.toString()).length() > 8){
                    jwt =  str.toString();
                    break;
                }
            }
            logger.debug("login: oldJwt" + jwt);
            this.banJwt(jwt);
        }
        */
        //创建新的token
        JwtHelper jwtHelper = new JwtHelper();
        String jwt = jwtHelper.createToken(customer.getId(),-2l,jwtExpireTime);
        /*
        if(!redisTemplate.hasKey(key)){
            redisTemplate.opsForSet().add(key,jwt);
        }
        */
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
        redisTemplate.delete("up_" + customerId);
        return new ReturnObject<>(true);
    }

    /**
     * 禁止持有特定令牌的用户登录
     * @param jwt JWT令牌
     */
    private void banJwt(String jwt){
        String[] banSetName = {"BanJwt_0", "BanJwt_1"};
        long bannIndex = 0;
        if (!redisTemplate.hasKey("banIndex")){
            redisTemplate.opsForValue().set("banIndex", Long.valueOf(0));
        } else {
            logger.debug("banJwt: banIndex = " +redisTemplate.opsForValue().get("banIndex"));
            bannIndex = Long.parseLong(redisTemplate.opsForValue().get("banIndex").toString());
        }
        logger.debug("banJwt: banIndex = " + bannIndex);
        String currentSetName = banSetName[(int) (bannIndex % banSetName.length)];
        logger.debug("banJwt: currentSetName = " + currentSetName);
        if(!redisTemplate.hasKey(currentSetName)) {
            // 新建
            logger.debug("banJwt: create ban set" + currentSetName);
            redisTemplate.opsForSet().add(currentSetName, jwt);
            redisTemplate.expire(currentSetName,jwtExpireTime * 2, TimeUnit.SECONDS);
        }else{
            //准备向其中添加元素
            if(redisTemplate.getExpire(currentSetName, TimeUnit.SECONDS) > jwtExpireTime) {
                // 有效期还长，直接加入
                logger.debug("banJwt: add to exist ban set" + currentSetName);
                redisTemplate.opsForSet().add(currentSetName, jwt);
            } else {
                // 有效期不够JWT的过期时间，准备用第二集合，让第一个集合自然过期
                // 分步式加锁
                logger.debug("banJwt: switch to next ban set" + currentSetName);
                long newBanIndex = bannIndex;
                while (newBanIndex == bannIndex &&
                        !redisTemplate.opsForValue().setIfAbsent("banIndexLocker","nouse", lockerExpireTime, TimeUnit.SECONDS)){
                    //如果BanIndex没被其他线程改变，且锁获取不到
                    try {
                        Thread.sleep(10);
                        //重新获得新的BanIndex
                        newBanIndex = (Long) redisTemplate.opsForValue().get("banIndex");
                    }catch (InterruptedException e){
                        logger.error("banJwt: 锁等待被打断");
                    }
                    catch (IllegalArgumentException e){

                    }
                }
                if (newBanIndex == bannIndex) {
                    //切换ban set
                    bannIndex = redisTemplate.opsForValue().increment("banIndex");
                }else{
                    //已经被其他线程改变
                    bannIndex = newBanIndex;
                }

                currentSetName = banSetName[(int) (bannIndex % banSetName.length)];
                //启用之前，不管有没有，先删除一下，应该是没有，保险起见
                redisTemplate.delete(currentSetName);
                logger.debug("banJwt: next ban set =" + currentSetName);
                redisTemplate.opsForSet().add(currentSetName, jwt);
                redisTemplate.expire(currentSetName,jwtExpireTime * 2,TimeUnit.SECONDS);
                // 解锁
                redisTemplate.delete("banIndexLocker");
            }
        }
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
    public ReturnObject<PageInfo<VoObject>> findAllCustomers(String userName,String email, String mobile, Integer page, Integer pagesize) {

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
