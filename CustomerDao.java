package cn.edu.xmu.other.dao;

import cn.edu.xmu.ooad.util.RandomCaptcha;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.mapper.CustomerPoMapper;
import cn.edu.xmu.other.model.bo.Customer;
import cn.edu.xmu.other.model.po.CustomerPo;
import cn.edu.xmu.other.model.po.CustomerPoExample;
import cn.edu.xmu.other.model.vo.CustomerVo.CustomerVo;
import cn.edu.xmu.other.model.vo.CustomerVo.ModifyPwdVo;
import cn.edu.xmu.other.model.vo.CustomerVo.ResetPwdVo;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 买家Dao
 * @author Shuhao Peng
 * @date 2020/11/28 18:31
 */
@Repository
public class CustomerDao {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDao.class);

    @Autowired
    CustomerPoMapper customerPoMapper;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 由bo创建customer后插入
     *
     * @param customer bo对象
     * @return ReturnObject
     * createdBy: Shuhao Peng 2020-11-28 18:34
     */
    public ReturnObject<Customer> createCustomer(Customer customer) {
        CustomerPo customerPo = customer.gotCustomerPo();
        ReturnObject<Customer> returnObject = null;

        customerPo.setPoint(0);
        customerPo.setState((byte) 4);
        customerPo.setBeDeleted((byte) 0);
        customerPo.setGmtCreate(LocalDateTime.now());
        customerPo.setGmtModified(LocalDateTime.now());

        try {
            int ret = customerPoMapper.insert(customerPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertRole: insert role fail " + customerPo.toString());
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + customerPo.getUserName()));
            } else {
                //插入成功
                logger.debug("insertRole: insert role = " + customerPo.toString());
                customer.setId(customerPo.getId());
                returnObject = new ReturnObject<>(customer);
            }
        } catch (DataAccessException e) {
            logger.debug("other sql exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;
    }

    /**
     * 由用户名获得用户
     * @param userName
     * @return ReturnObject
     * createdBy: Shuhao Peng 2020-12-03 08:33
     */
    public ReturnObject<Customer> getCustomerByName(String userName) {
        CustomerPoExample example = new CustomerPoExample();
        CustomerPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<CustomerPo> customers = null;
        try {
            customers = customerPoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            StringBuilder message = new StringBuilder().append("getCustomerByName: ").append(e.getMessage());
            logger.error(message.toString());
        }

        if (null == customers || customers.isEmpty()) {
            return new ReturnObject<>();
        } else {
            Customer customer = new Customer(customers.get(0));
            return new ReturnObject<>(customer);
        }
    }

    /**
     * 根据ID获取CustomerPo
     * @author Shuhao Peng
     * @param Id
     * @return CustomerPo
     */
    public CustomerPo findCustomerById(Long Id) {

        logger.debug("findCustomerById: Id =" + Id);
        CustomerPo customerPo = customerPoMapper.selectByPrimaryKey(Id);

        return customerPo;
    }

    /**
     * 根据 id 修改用户信息
     * @param customerVo 传入的 CustomerVo 对象
     * @return 返回对象 ReturnObj
     * @author Shuhao Peng
     * Created at 2020/12/5 10:20
     */
    public ReturnObject<Object> modifyCustomerByVo(Long id, CustomerVo customerVo) {

        CustomerPo orig = customerPoMapper.selectByPrimaryKey(id);
        // 不修改已被逻辑废弃的账户
        if (orig == null || (orig.getState() != null && Customer.State.getTypeByCode(orig.getState().intValue()) == Customer.State.FORBID)) {
            logger.info("用户不存在或已被封禁：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        // 构造 User 对象以计算签名
        Customer customer = new Customer(orig);
        CustomerPo po = customer.createUpdatePo(customerVo);

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = customerPoMapper.updateByPrimaryKeySelective(po);
        } catch (DataAccessException e) {
            // 如果发生 Exception
            logger.error("数据库错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("用户不存在或已被禁止：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("用户 id = " + id + " 的资料已更新");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * 获取所有用户信息
     * @author Shuhao Peng
     * @return List<CustomerPo> 用户列表
     * creat at 2020/12/5 13:26
     */
    public PageInfo<CustomerPo> findAllCustomers(String userNameFind, String emailFind, String mobileFind, int page, int pageSize) {
        CustomerPoExample example = new CustomerPoExample();
        CustomerPoExample.Criteria criteria = example.createCriteria();
        if(!userNameFind.isBlank()) {
            criteria.andUserNameEqualTo(userNameFind);
        }
        if(!emailFind.isBlank()) {
            criteria.andEmailEqualTo(emailFind);
        }
        if(!mobileFind.isBlank()) {
            criteria.andMobileEqualTo(mobileFind);
        }

        List<CustomerPo> customers = customerPoMapper.selectByExample(example);

        logger.debug("findCustomerById: retCustomers = "+customers);

        return new PageInfo<>(customers);
    }

    /**
     * 改变用户状态
     * @param id    用户 id
     * @param state 目标状态
     * @return 返回对象 ReturnObj
     * @author Shuhao Peng
     * Created at 2020/12/05 15:09
     */
    public ReturnObject<Object> changeCustomerState(Long id, Customer.State state) {
        CustomerPo po = createCustomerStateModPo(id, state);
        if (po == null) {
            logger.info("用户不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = customerPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                logger.info("用户不存在或已被删除：id = " + id);
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("用户 id = " + id + " 的状态修改为 " + state.getDescription());
                retObj = new ReturnObject<>();
            }
        } catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 创建可改变目标用户状态的 Po
     * @param id    用户 id
     * @param state 用户目标状态
     * @return CustomerPo 对象
     * @author Shuhao Peng
     * Created at 2020/12/5 15:11
     */
    private CustomerPo createCustomerStateModPo(Long id, Customer.State state) {
        // 查询密码等资料以计算新签名
        CustomerPo orig = customerPoMapper.selectByPrimaryKey(id);
        // 不修改已被逻辑废弃的账户的状态
        if (orig == null || orig.getBeDeleted().equals((byte) 1)) {
            return null;
        }
        Customer customer = new Customer(orig);
        customer.setState(state);
        // 构造一个全为 null 的 vo 因为其他字段都不用更新
        CustomerVo vo = new CustomerVo();
        return customer.createUpdatePo(vo);
    }

    /**
     * 用户重置密码
     * @param vo 重置密码对象
     * @param ip 请求ip地址
     * @author Shuhao Peng
     * Created at 2020/12/7 14:23
     */
    public ReturnObject<Object> resetPassword(ResetPwdVo vo, String ip) {

        //防止重复请求验证码
        if(redisTemplate.hasKey("ip_"+ip)){
            return new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
        }
        else {
            //1 min中内不能重复请求
            redisTemplate.opsForValue().set("ip_"+ip,ip);
            redisTemplate.expire("ip_" + ip, 60*1000, TimeUnit.MILLISECONDS);
        }

        //验证邮箱、用户名
        CustomerPoExample customerPoExample1 = new CustomerPoExample();
        CustomerPoExample.Criteria criteria = customerPoExample1.createCriteria();
        criteria.andUserNameEqualTo(vo.getUserName());
        //criteria.andEmailEqualTo(vo.getEmail());
        List<CustomerPo> customerPo1 = null;
        try {
            customerPo1 = customerPoMapper.selectByExample(customerPoExample1);
        }catch (Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(customerPo1.isEmpty()) {
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
        }
        else if(!customerPo1.get(0).getEmail().equals(vo.getEmail())) {
            return new ReturnObject<>(ResponseCode.EMAIL_WRONG);
        }


        //随机生成验证码
        String captcha = RandomCaptcha.getRandomString(6);
        while(redisTemplate.hasKey(captcha)) {
            captcha = RandomCaptcha.getRandomString(6);
        }

        String id = customerPo1.get(0).getId().toString();
        String key = "cp_" + captcha;
        //key:验证码,value:id存入redis
        redisTemplate.opsForValue().set(key,id);
        //五分钟后过期
        redisTemplate.expire("cp_" + captcha, 5*60*1000, TimeUnit.MILLISECONDS);

        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * 用户修改密码
     * @param modifyPwdVo 修改密码对象
     * @return Object
     * @author Shuhao Peng
     * Created at 2020/12/7 14:57
     */
    public ReturnObject<Object> modifyPassword(ModifyPwdVo modifyPwdVo) {
        //通过验证码取出id
        if(!redisTemplate.hasKey("cp_"+modifyPwdVo.getCaptcha())) {
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
        }
        String id= redisTemplate.opsForValue().get("cp_"+modifyPwdVo.getCaptcha()).toString();

        CustomerPo customerpo = null;
        try {
            customerpo = customerPoMapper.selectByPrimaryKey(Long.parseLong(id));
        }catch (Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }

        //新密码与原密码相同
        if(customerpo.getPassword().equals(modifyPwdVo.getNewPassword())) {
            return new ReturnObject<>(ResponseCode.PASSWORD_SAME);
        }

        //加密
        CustomerPo customerPo = new CustomerPo();
        customerPo.setId(Long.parseLong(id));
        customerPo.setPassword(modifyPwdVo.getNewPassword());

        //更新数据库
        try {
            customerPoMapper.updateByPrimaryKeySelective(customerPo);
        }catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

}
