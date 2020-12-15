package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.model.bo.AfterSale;
import cn.edu.xmu.other.model.vo.AfterSaleVo.*;
import cn.edu.xmu.other.service.AfterSaleService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "售后服务", tags = "aftersale")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class AfterSaleController {
    private  static  final Logger logger = LoggerFactory.getLogger(AfterSaleController.class);
    @Autowired
    private AfterSaleService afterSaleService;
    @Autowired
    HttpServletResponse httpServletResponse;

    /**买家提交售后单
     * @author 24320182203309 杨浩然
     * createdBy 杨浩然 2020/12/6 13:57
     * modifiedBy 杨浩然 2020/12/6 19:20
     */
    @ApiOperation(value = "买家提交售后单")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer",value="订单明细id", paramType="path"),
            @ApiImplicitParam(paramType = "body", dataType = "AfterSaleVo", name = "vo", value = "添加售后单信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/orderItems/{id}/aftersales")
    public Object applyAfterSale(@Validated @RequestBody AfterSaleVo vo, BindingResult bindingResult, @PathVariable Long id,
                                 @LoginUser @ApiIgnore @RequestParam(required = true, defaultValue = "0") Long userId){
        Object retObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != retObject) {
            logger.debug("validate fail");
            return retObject;
        }
        logger.debug("applyAfterSaleService: userId = "+ userId+" orderItemId: id = "+ id);
        ReturnObject<VoObject> returnObject = afterSaleService.applyAfterSaleService(vo, id,userId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**买家查询所有售后单的信息
     * @author yhr
     * @date Created in 2020/12/7 10:33
     **/
    @Audit
    @ApiOperation(value = "买家查询所有售后单的信息",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "spuId",         value ="商品SpuId", required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "skuId",         value ="商品skuId", required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "beginTime",     value ="开始时间",   required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "endTime",       value ="结束时间",   required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "type",          value ="售后类型",   required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "state",         value ="售后状态",   required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/aftersales")
    public Object getAfterSales(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pagesize,
            @RequestParam(required = false)  Long spuId,
            @RequestParam(required = false)  Long skuId,
            @RequestParam(required = false)  String beginTime,
            @RequestParam(required = false)  String endTime,
            @RequestParam(required = false)  Byte type,
            @RequestParam(required = false)  Byte state,
            @LoginUser @ApiIgnore @RequestParam(required = true, defaultValue = "0") Long userId) {
        Object object = null;
        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = afterSaleService.getAllAfterSale(page, pagesize,spuId,skuId,beginTime,endTime,type,state,userId);
            logger.debug("getAllAfterSale: = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }
        return object;
    }

    /**查询所有状态
     * @return Object
     * createdBy: yhr3309 2020-12-7 18:41
     **/
    @ApiOperation(value="获得售后单的所有状态")
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @GetMapping("aftersales/states")
    public Object getAllAfterSaleStates(){
        AfterSale.State[] states=AfterSale.State.class.getEnumConstants();
        List<StateVo> stateVos=new ArrayList<StateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new StateVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }

    /**管理员查看所有售后单（可根据售后类型和状态选择）
     * @author yhr
     * @date Created in 2020/12/7 10:33
     **/
    @Audit
    @ApiOperation(value = "管理员查看所有售后单（可根据售后类型和状态选择）",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="shopId", required = true, dataType="Integer",value="店铺id", paramType="path"),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "spuId",         value ="商品SpuId", required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "skuId",         value ="商品skuId", required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "beginTime",     value ="开始时间",   required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "endTime",       value ="结束时间",   required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "type",          value ="售后类型",   required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "state",         value ="售后状态",   required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shops/{id}/aftersales")
    public Object adminGetAfterSales(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pagesize,
            @RequestParam(required = false)  Long spuId,
            @RequestParam(required = false)  Long skuId,
            @RequestParam(required = false)  String beginTime,
            @RequestParam(required = false)  String endTime,
            @RequestParam(required = false)  Byte type,
            @RequestParam(required = false)  Byte state,
            @PathVariable Long id,
            @LoginUser @ApiIgnore @RequestParam(required = true, defaultValue = "0") Long userId) {
        Object object = null;
        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = afterSaleService.adminGetAllAfterSales(page, pagesize,spuId,skuId,beginTime,endTime,type,state,id);
            logger.debug("getAllAfterSale: = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }
        return object;
    }

    /**买家根据售后单id查询售后单信息
     * @author yhr
     * @date Created in 2020/12/7 10:33
     **/
    @Audit
    @ApiOperation(value = "买家根据售后单id查询售后单信息",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", required = true, dataType="Integer",value="售后单id", paramType="path"),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/aftersales/{id}")
    public Object getAfterSaleById(
            @PathVariable Long id,
            @LoginUser @ApiIgnore @RequestParam(required = true, defaultValue = "0") Long userId) {
        ReturnObject<VoObject> returnObject = afterSaleService.getAfterSaleById(id,userId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**买家修改售后单信息（店家生成售后单前）
     * @param id: 售后单 id
     * @param vo 修改信息
     * @param bindingResult 校验信息
     * @author 24320182203309 yhr
     * Created at 2020/12/7 20:20
     * Modified by 24320182203309 yhr at 2020/12/8 0:19
     **/
    @ApiOperation(value = "买家修改售后单信息（店家生成售后单前）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", required = true, dataType="Integer",value="售后单id", paramType="path"),
            @ApiImplicitParam(paramType = "body", dataType = "AfterSaleModifyVo", name = "vo", value = "修改售后单信息", required = true),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @PutMapping("/aftersales/{id}")
    public Object modifyAfterSale( @PathVariable Long id,
                                   @Validated @RequestBody AfterSaleModifyVo vo,
                                   BindingResult bindingResult,
                                   @LoginUser Long userId) {
        if (logger.isDebugEnabled()) {
            logger.debug("modifyAfterSale: id = "+ id +" vo = " + vo);
        }
        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("validate fail. afterSale:" + id);
            return returnObject;
        }
        ReturnObject returnObj = afterSaleService.modifyAfterSaleById(id, vo,userId);
        return Common.decorateReturnObject(returnObj);
    }

    /**买家取消售后单和逻辑删除售后单
     * @param id: 售后单 id
     * @author 24320182203309 yhr
     * Created at 2020/12/7 20:20
     * Modified by 24320182203309 yhr at 2020/12/8 0:19
     **/
    @ApiOperation(value = "买家取消售后单和逻辑删除售后单")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", required = true, dataType="Integer",value="售后单id", paramType="path"),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @DeleteMapping("/aftersales/{id}")
    public Object deleteAfterSale( @PathVariable Long id,
                                   @LoginUser Long userId) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteAfterSale: id = "+ id );
        }
        ReturnObject returnObj = afterSaleService.deleteAfterSaleById(id,userId);
        return Common.decorateReturnObject(returnObj);
    }
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
    public Object ShopUploadLogSn(@LoginUser Long userId, @PathVariable("id") Long id, @RequestBody LogSnVo vo, @PathVariable("shopId") Long shopId,
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



}
