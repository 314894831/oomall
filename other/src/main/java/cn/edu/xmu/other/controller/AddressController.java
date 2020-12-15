package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.model.bo.Address;
import cn.edu.xmu.other.model.vo.AddressVo.AddressVo;
import cn.edu.xmu.other.service.AddressService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * ，买家收货地址地址控制器
 * @author ChengYang Li
 **/
@Api(value = "地址服务", tags = "address")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/addresses", produces = "application/json;charset=UTF-8")
public class AddressController
{
    private  static  final Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private AddressService addressService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 买家新增收货地址
     * @author ChengYang li
     * @date Created in 2020/12/1 09:25
     **/
    @Audit
    @ApiOperation(value = "买家新增收货地址",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AddressVo", name = "vo", value = "地址信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 601, message = "地址簿达到上限")
    })
    @PostMapping("")
    public Object addAddress(@LoginUser Long id, @Validated @RequestBody AddressVo vo, BindingResult bindingResult)
    {
        if (logger.isDebugEnabled()) {
            logger.debug("买家: id = "+ id +" 新增收货地址：vo = " + vo);
        }
        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (returnObject != null) {
            logger.info("incorrect data received while 买家: id = "+ id +" 新增收货地址：vo = " + vo);
            return returnObject;
        }

        Address address = vo.createAddress();
        address.setCustomerId(id);
        address.setGmtCreate(LocalDateTime.now());

        ReturnObject retObject = addressService.addAddress(address);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 买家设置默认地址
     * @author ChengYang li
     * @date Created in 2020/12/1 15:28
     **/
    @Audit
    @ApiOperation(value = "买家设置默认地址",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="地址id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("/{id}/default")
    public Object updateAddress(@LoginUser Long custumerId, @PathVariable("id") Long id)
    {
        if (logger.isDebugEnabled()) {
            logger.debug("买家: id = "+ custumerId +" 设置默认地址：id = " + id);
        }

        ReturnObject returnObj = addressService.setDefaultAddressById(custumerId, id);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 买家修改自己的地址信息
     * @author ChengYang li
     * @date Created in 2020/12/2 11:43
     **/
    @Audit
    @ApiOperation(value = "买家修改自己的地址信息",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="地址id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AddressVo", name = "vo", value = "地址信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("/{id}")
    public Object addAddress(@LoginUser Long custumerId, @PathVariable("id") Long id,
                             @Validated @RequestBody AddressVo vo, BindingResult bindingResult)
    {
        if (logger.isDebugEnabled()) {
            logger.debug("买家: id = "+ custumerId +" 设置默认地址：id = " + id);
        }

        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (returnObject != null) {
            logger.info("incorrect data received while 买家: id = "+ id +" 修改收货地址信息：vo = " + vo);
            return returnObject;
        }

        ReturnObject returnObj = addressService.updateAddressById(custumerId, id, vo);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 买家删除自己的地址
     * @author ChengYang li
     * @date Created in 2020/12/2 11:43
     **/
    @Audit
    @ApiOperation(value = "买家删除自己的地址",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="地址id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @DeleteMapping("/{id}")
    public Object deletAddress(@LoginUser Long custumerId, @PathVariable("id") Long id)
    {
        if (logger.isDebugEnabled()) {
            logger.debug("买家: id = "+ custumerId +" 设置默认地址：id = " + id);
        }

        ReturnObject returnObj = addressService.deleteAddressById(custumerId, id);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 买家获取自己的所有收货地址
     * @author ChengYang li
     * @date Created in 2020/12/2 11:43
     **/
    @Audit
    @ApiOperation(value = "买家获取自己的所有收货地址",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("")
    public Object findAllAddresses(
            @LoginUser Long custumerId,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pagesize) {

        Object object = null;

        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = addressService.getAllAddressesById(custumerId, page, pagesize);
            logger.debug("getAllAddressesById: addresses = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }

        return object;
    }
}
