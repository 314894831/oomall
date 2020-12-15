package cn.edu.xmu.other.service.impl;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dao.CustomerDao;
import cn.edu.xmu.other.model.bo.Customer;
import cn.edu.xmu.other.service.CustomerService;
import cn.edu.xmu.other.service.CustomerServiceInterface;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

@DubboService
public class CustomerServiceImpl implements CustomerServiceInterface
{
    private Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    CustomerDao customerDao;

    @Value("${privilegeservice.login.jwtExpire}")
    private Integer jwtExpireTime;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Override
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

        //创建新的token
        JwtHelper jwtHelper = new JwtHelper();
        String jwt = jwtHelper.createToken(customer.getId(),-2l,jwtExpireTime);

        redisTemplate.opsForSet().add(key,jwt);
        logger.debug("login: newJwt = "+ jwt);
        retObj = new ReturnObject<>(jwt);

        return retObj;
    }
}
