package cn.edu.xmu.aftersale.controller;

import cn.edu.xmu.aftersale.model.vo.LogSnVo;
import cn.edu.xmu.aftersale.model.vo.ResolutionVo;
import cn.edu.xmu.aftersale.service.AfterSaleService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
@Api(value = "售后服务", tags = "aftersale")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/aftersale", produces = "application/json;charset=UTF-8")
public class AfterSaleController
{
    private  static  final Logger logger = LoggerFactory.getLogger(AfterSaleController.class);

    @Autowired
    AfterSaleService afterSaleService;


    HttpServletResponse httpServletResponse;

    /**
     * @author Kejian Shi
     * @param userId
     * @param id
     * @param vo
     * @param shopId
     * @param bindingResult
     * @param httpServletResponse
     * @return
     */
    @ApiOperation(value = "店家寄出货物")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path"),
            @ApiImplicitParam(name="shopId", required = true, dataType="String", paramType="path"),
            @ApiImplicitParam(name="vo", required = true, dataType="LogSnVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/shops/{shopId}/aftersales/{id}/deliver")
    public Object ShopUploadLogSn( @LoginUser Long userId, @PathVariable("id") Long id, @RequestBody LogSnVo vo, @PathVariable("shopId") Long shopId,
                              BindingResult bindingResult,
                              HttpServletResponse httpServletResponse){

        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return o;
        }
        ReturnObject<VoObject> returnObject = afterSaleService.ShopUploadLogSn(userId,shopId,id,vo);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param id
     * @param vo
     * @param bindingResult
     * @param httpServletResponse
     * @return
     */
    @ApiOperation(value = "店家确认收到买家的退（换）货")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="integer", paramType="path"),
            @ApiImplicitParam(name="shopId", required = true, dataType="integer", paramType="path"),
            @ApiImplicitParam(name="vo", required = true, dataType="ResolutionVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/shops/{shopId}/aftersales/{id}/receive")
    public Object ShopConfirmRecive( @LoginUser Long userId, @PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @RequestBody ResolutionVo vo,
                              BindingResult bindingResult,
                              HttpServletResponse httpServletResponse){

        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return o;
        }
        ReturnObject<VoObject> returnObject = afterSaleService.ShopConfirmRecive(userId,shopId,id,vo);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param id
     * @param vo
     * @param bindingResult
     * @param httpServletResponse
     * @return
     */
    @ApiOperation(value = "管理员同意/不同意（退款，换货，维修）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="shopId", required = true, dataType="integer", paramType="path"),
            @ApiImplicitParam(name="id", required = true, dataType="integer", paramType="path"),
            @ApiImplicitParam(name="vo", required = true, dataType="ResolutionVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/shops/{shopId}/aftersales/{id}/confirm")
    public Object AdminCheckAfterSale( @LoginUser Long userId, @PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @RequestBody ResolutionVo vo,
                                    BindingResult bindingResult,
                                    HttpServletResponse httpServletResponse){

        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return o;
        }
        ReturnObject<VoObject> returnObject = afterSaleService.AdminCheckAfterSale(userId,shopId,id,vo);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param id
     * @return
     */
    @ApiOperation(value = "买家根据售后id查询售后单据信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/shops/{shopId}/aftersales/{id}")
    public Object GetAfterSaleInfoById( @LoginUser Long userId, @PathVariable("shopId") Long shopId, @PathVariable("id") Long id)
    {
        System.out.println("AfterSaleController -> GetAfterSaleInfoById");
        return Common.getRetObject(afterSaleService.GetAfterSaleInfoById(userId,shopId,id));
    }

    /**
     * @author Kejian Shi
     * @param userId
     * @param id
     * @return
     */
    @ApiOperation(value = "买家确认售后单结束")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/aftersales/{id}/confirm")
    public Object CustomerConfirmAfterSale(@LoginUser Long userId,  @PathVariable("id") Long id)
    {
        return Common.getRetObject(afterSaleService.CustomerConfirmAfterSale(userId,id));
    }

    @ApiOperation(value = "买家填写售后信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path"),
            @ApiImplicitParam(name="vo", required = true, dataType="LogSnVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/aftersales/{id}/sendback")
    public Object CustomerUploadLogSn( @LoginUser Long userId, @PathVariable("id") Long id, @RequestBody LogSnVo vo,
                               BindingResult bindingResult,
                               HttpServletResponse httpServletResponse){

        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return o;
        }
        ReturnObject<VoObject> returnObject = afterSaleService.CustomerUploadLogSn(userId,id,vo);
        return Common.getRetObject(returnObject);
    }

    @ApiOperation(value = "买家取消或者逻辑删除售后单")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("/aftersales/{id}")
    public Object DeleteAfterSaleInfoById( @LoginUser Long userId, @PathVariable("id") Long id)
    {
        System.out.println("AfterSaleController -> DeleteAfterSaleInfoById");
        return Common.getRetObject(afterSaleService.DeleteAfterSaleInfoById(userId,id));
    }
}
