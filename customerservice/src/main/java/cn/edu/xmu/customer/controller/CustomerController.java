package cn.edu.xmu.customer.controller;

import cn.edu.xmu.customer.model.bo.Customer;
import cn.edu.xmu.customer.model.vo.*;
import cn.edu.xmu.customer.service.CustomerService;
import cn.edu.xmu.customer.util.IpUtil;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;


import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 买家控制器
 * @author Shuhao Peng
 * Modified at 2020/11/28 16:42
 **/
@Api(value = "买家服务", tags = "customer")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/user", produces = "application/json;charset=UTF-8")
public class CustomerController {
    private  static  final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 注册用户
     * @param vo:vo对象
     * @param result 检查结果
     * @return  Object
     * createdBy: Shuhao Peng 2020-11-28 17:02
     */
    @ApiOperation(value="注册用户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "NewCustomerVo", name = "vo", value = "newCustomerInfo", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 404, message = "参数不合法")
    })
    @PostMapping("users")
    public Object register(@Validated @RequestBody NewCustomerVo vo, BindingResult result){
        if(result.hasErrors()){
            return Common.processFieldErrors(result,httpServletResponse);
        }

        Customer customer=vo.createCustomer();

        ReturnObject returnObject=customerService.register(customer);

        if (returnObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg()), httpServletResponse);
        }

    }

    /**
     * 用户登录
     * @param loginVo
     * @param bindingResult
     * @param httpServletResponse
     * @param httpServletRequest
     * @return
     * @author Shuhao Peng
     * createdBy: Shuhao Peng 2020-12-03 08:30
     */
    @ApiOperation(value = "登录")
    @PostMapping("users/login")
    public Object login(@Validated @RequestBody LoginVo loginVo, BindingResult bindingResult
            , HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
        /* 处理参数校验错误 */
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return o;
        }

        String ip = IpUtil.getIpAddr(httpServletRequest);
        ReturnObject<String> jwt = customerService.login(loginVo.getUserName(), loginVo.getPassword(), ip);

        if(jwt.getData() == null){
            return ResponseUtil.fail(jwt.getCode(), jwt.getErrmsg());
        }else{
            return ResponseUtil.ok(jwt.getData());
        }
    }

    /**
     * 用户注销
     * @param customerId
     * @return
     * @author Shuhao Peng
     * createdat 2020-12-05 12:44
     */
    @ApiOperation(value = "注销")
    @Audit
    @GetMapping("users/logout")
    public Object logout(@LoginUser Long customerId){

        logger.debug("logout: customerId = "+customerId);
        ReturnObject<Boolean> success = customerService.logout(customerId);
        if (success.getData() == null)  {
            return ResponseUtil.fail(success.getCode(), success.getErrmsg());
        }else {
            return ResponseUtil.ok();
        }
    }

    /**
     * 查询所有状态
     * @return Object
     * createdBy: Shuhao Peng 2020-12-03 17:12
     */
    @ApiOperation(value="获得用户的所有状态")
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @GetMapping("users/states")
    public Object getAllStates(){
        Customer.State[] states=Customer.State.class.getEnumConstants();
        List<StateVo> stateVos=new ArrayList<StateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new StateVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }

    /**
     * 用户查看自己信息
     * @return  Object
     * createdBy: Shuhao Peng 2020-11-28 17:35
     */
    @ApiOperation(value = "查看自己信息",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value ="用户token", required = true)
    })
    @ApiResponses({
    })
    @Audit
    @GetMapping("users")
    public Object getCustomerSelf(@LoginUser Long customerId) {
        logger.debug("getCustomerSelf customerId:" + customerId);
        Object returnObject;
        ReturnObject<VoObject> customer =  customerService.findCustomerById(customerId);
        logger.debug("finderSelf: customer = " + customer.getData() + " code = " + customer.getCode());
        returnObject = Common.getRetObject(customer);
        return returnObject;
    }

    /**
     * 修改自己的信息
     * @param vo 修改信息 CustomerVo 视图
     * @param bindingResult 校验信息
     * @return Object
     * @author Shuhao Peng
     * Created at 2020/12/05 9:51
     */
    @ApiOperation(value = "修改自己的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CustomerVo", name = "vo", value = "可修改的用户信息", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @PutMapping("users")
    public Object changeCustomerself(@LoginUser Long id, @Validated @RequestBody CustomerVo vo, BindingResult bindingResult) {
        if (logger.isDebugEnabled()) {
            logger.debug("modifyUserInfo: id = "+ id +" vo = " + vo);
        }
        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("incorrect data received while modifyUserInfo id = " + id);
            return returnObject;
        }
        ReturnObject returnObj = customerService.modifyCustomerInfo(id, vo);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员获得所有用户列表
     * @return Object
     * createdBy Shuhao Peng 2020/12/5 13:07
     */
    @ApiOperation(value = "平台管理员获取所有用户列表")
    @Audit
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "userName",      value ="用户名",    required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "email",         value ="邮箱",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "mobile",        value ="电话号码",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = true)
    })
    @ApiResponses({
    })
    @GetMapping("users/all")
    public Object findAllCustomer(
            @RequestParam  String  userName,
            @RequestParam  String  email,
            @RequestParam  String  mobile,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pagesize) {

        Object object = null;

        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = customerService.findAllCustomers(userName, email,mobile, page, pagesize);
            logger.debug("findCustomerById: getCustomers = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }

        return object;
    }

    /**
     * 管理员获得任意用户信息
     * @return Object
     * createdBy Shuhao Peng 2020/12/5 14:44
     */
    @Audit
    @ApiOperation(value = "查看任意用户信息",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id",            value ="用户id",    required = true)
    })
    @ApiResponses({
    })
    @GetMapping("users/{id}")
    public Object getCustomerById(@PathVariable("id") Long id) {

        Object returnObject = null;

        ReturnObject<VoObject> customer = customerService.findCustomerById(id);
        logger.debug("findCustomerById: customer = " + customer.getData() + " code = " + customer.getCode());

        if (!customer.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
            returnObject = Common.getRetObject(customer);
        } else {
            returnObject = Common.getNullRetObj(new ReturnObject<>(customer.getCode(), customer.getErrmsg()), httpServletResponse);
        }

        return returnObject;
    }

    /**
     * 平台管理员封禁买家
     * @param id: 用户 id
     * @return Object
     * @author Shuhao Peng
     * Created at 2020/12/5 15:05
     */
    @ApiOperation(value = "平台管理员封禁买家")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @PutMapping("users/{id}/ban")
    public Object forbidUser(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("forbidCustomer: id = "+ id);
        }
        ReturnObject returnObject = customerService.forbidCustomer(id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 平台管理员解禁买家
     * @param id: 用户 id
     * @return Object
     * @author Shuhao Peng
     * Created at 2020/12/5 16:12
     */
    @ApiOperation(value = "平台管理员解禁买家")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @PutMapping("users/{id}/release")
    public Object releaseUser(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("releaseCustomer: id = "+ id);
        }
        ReturnObject returnObject = customerService.releaseCustomer(id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 用户重置密码
     * @param vo 重置密码对象
     * @param httpServletResponse HttpResponse
     * @param httpServletRequest HttpRequest
     * @param bindingResult 校验信息
     * @return Object
     * @author Shuhao Peng
     * Created at 2020/12/7 14:08
     */
    @ApiOperation(value="用户重置密码")
    @ApiResponses({
            @ApiResponse(code = 745, message = "与系统预留的邮箱不一致"),
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("users/password/reset")
    @ResponseBody
    public Object resetPassword(@RequestBody ResetPwdVo vo, BindingResult bindingResult
            , HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {

        if (logger.isDebugEnabled()) {
            logger.debug("resetPassword");
        }
        /* 处理参数校验错误 */
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return o;
        }

        String ip = IpUtil.getIpAddr(httpServletRequest);

        ReturnObject returnObject = customerService.resetPassword(vo,ip);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 用户修改密码
     * @param vo 修改密码对象
     * @return Object
     * @author Shuhao Peng
     * Created at 2020/12/7 14:53
     */
    @ApiOperation(value="用户修改密码",produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 741, message = "不能与旧密码相同"),
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("users/password")
    @ResponseBody
    public Object modifyPassword(@Validated @RequestBody ModifyPwdVo vo, BindingResult result) {
        if(result.hasErrors()){
            return Common.processFieldErrors(result,httpServletResponse);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("modifyPassword");
        }
        ReturnObject returnObject = customerService.modifyPassword(vo);
        return Common.decorateReturnObject(returnObject);
    }
}
