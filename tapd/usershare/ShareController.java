package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.*;
import cn.edu.xmu.other.model.bo.ShareActivityStrategy;
import cn.edu.xmu.other.model.vo.ShareVo.GetShareRetVo;
import cn.edu.xmu.other.model.vo.ShareVo.ShareActivityVo;
import cn.edu.xmu.other.service.ShareService;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.x.protobuf.Mysqlx;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Api(value = "分享服务", tags = "share")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class ShareController {

    private  static  final Logger logger = LoggerFactory.getLogger(ShareController.class);
    @Autowired
    private ShareService shareService;

    @Autowired
    HttpServletResponse httpServletResponse;
    /**
     * 分享者生成分享链接
     * @author 24320182203309 杨浩然
     * createdBy 杨浩然 2020/12/4 13:57
     * modifiedBy 杨浩然 2020/12/16 14:20
     **/
    @ApiOperation(value = "分享者生成分享链接")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "skuId", value = "生成分享链接所需的商品skuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/skus/{id}/shares")
    public Object addToCart(@PathVariable Long id,
                            @LoginUser Long userId){
        logger.debug("生成分享链接: id = "+ id);
        ReturnObject<VoObject> returnObject = shareService.createShare(id, userId);
        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(201);
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**买家查询所有分享记录
     * @author yhr
     * @date Created in 2020/12/2 10:33
     **/
    @Audit
    @ApiOperation(value = "买家查询所有分享记录",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "goodsSkuId",    value ="SKUId",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "beginTime",     value ="开始时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "endTime",       value ="结束时间",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",      value ="每页数目",  required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shares")
    public Object getShares(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize,
            @RequestParam(required = false, defaultValue = "")  Long goodsSkuId,
            @RequestParam(required = false, defaultValue = "")  String beginTime,
            @RequestParam(required = false, defaultValue = "")  String endTime,
            @LoginUser Long userId) {
        Object object = null;
        if(page <= 0 || pageSize <= 0) {
            object = Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = shareService.getShares(page,pageSize,goodsSkuId,beginTime,endTime,userId);
            logger.debug("getShareById: getShares = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }
        return object;
    }

    /**管理员查询商品分享记录
     * @author yhr
     * @date Created in 2020/12/5 10:33
     **/
    @Audit
    @ApiOperation(value = "管理员查询商品分享记录",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "店铺Id", required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer", name = "skuId",    value ="SKUId",      required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",      value ="每页数目",  required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shops/{did}/skus/{id}/shares")
    public Object adminGetShares(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize,
            @PathVariable Long id,
            @PathVariable Long did) {
        Object object = null;
        if(page <= 0 || pageSize <= 0) {
            object = Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = shareService.adminGetShares(page,pageSize,id,did);
            logger.debug("getShareById: getShares = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }
        return object;
    }

    /**买家查询所有分享成功记录
     * @author yhr
     * @date Created in 2020/12/2 10:33
     **/
    @Audit
    @ApiOperation(value = "买家查询所有分享成功记录",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "skuId",    value ="SKUId",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "beginTime",     value ="开始时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "endTime",       value ="结束时间",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",      value ="每页数目",  required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/beshared")
    public Object getBeShared(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize,
            @RequestParam(required = false, defaultValue = "")  Long skuId,
            @RequestParam(required = false, defaultValue = "")  String beginTime,
            @RequestParam(required = false, defaultValue = "")  String endTime,
            @LoginUser Long userId) {
        Object object = null;
        if(page <= 0 || pageSize <= 0) {
            object = Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = shareService.getBeShared(page,pageSize,skuId,beginTime,endTime,userId);
            logger.debug("getBeSharedById: getShared = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }
        return object;
    }

    /**管理员查询所有分享成功记录
     * @author yhr
     * @date Created in 2020/12/2 10:33
     **/
    @Audit
    @ApiOperation(value = "管理员查询所有分享成功记录",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "店铺Id", required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer", name = "goodsSkuId",    value ="SKUId",      required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "beginTime",     value ="开始时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "endTime",       value ="结束时间",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",      value ="每页数目",  required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shops/{did}/skus/{id}/beshared")
    public Object adminGetBeShared(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize,
            @RequestParam(required = false, defaultValue = "")  String beginTime,
            @RequestParam(required = false, defaultValue = "")  String endTime,
            @PathVariable Long did,
            @PathVariable Long id,
            @LoginUser Long userId) {
        Object object = null;
        if(page <= 0 || pageSize <= 0) {
            object = Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = shareService.adminGetBeShared(did,page,pageSize,id,beginTime,endTime);
            logger.debug("getBeShared: adminGetBeShared = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }
        return object;
    }

    /**
     * 平台或店家创建新的分享活动
     * @author 24320182203309 杨浩然
     * createdBy 杨浩然 2020/12/1 13:57
     * modifiedBy 杨浩然 2020/12/16 14:20
     **/
    @ApiOperation(value = "平台或店家创建新的分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "body", dataType = "ShareActivityVo", name = "vo", value = "商品id和活动的起止时间与活动规则", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "shopId",  value ="店铺id",    required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "skuId",  value ="SKUId",    required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit//
    @PostMapping("/shops/{shopId}/skus/{id}/shareactivities")
    public Object createShareActivity(@Validated @RequestBody ShareActivityVo vo, BindingResult bindingResult,
                                      @PathVariable Long shopId,
                                      @PathVariable Long id){
        Object retObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != retObject) {
            logger.debug("validate fail");
            return retObject;
        }
        //开始时间在结束时间后，返回错误码
        try {
            if (parseStringToDateTime(vo.getBeginTime()).isAfter(parseStringToDateTime(vo.getEndTime()))) {
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.Log_Bigger));
            }
        }
        catch (Exception ex){
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        }
        //校验strategy是否正确
        if(!validateStrategy(vo.getStrategy())){

            logger.error("wrong strategy");
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseUtil.fail(ResponseCode.FIELD_NOTVALID,"分享规则格式错误");
        }
        //SHOPID为0 skuId为0 代表平台默认
        //shopId不为0 skuId为0 代表店铺默认
        //skuid不为0 shopid不为0 代表商店某个商品
        if(shopId==0&&id!=0){
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return ResponseUtil.fail(ResponseCode.RESOURCE_ID_NOTEXIST,"平台活动无法设置skuId");
        }
        logger.debug("创建新的分享活动");
        ReturnObject<VoObject> returnObject = shareService.createShareActivity(vo,shopId,id);
        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(201);
            return Common.getRetObject(returnObject);
        }
        //时间段重复返回BadRequest400
        else if(returnObject.getCode() == ResponseCode.SHAREACT_CONFLICT)httpServletResponse.setStatus(400);
        return Common.decorateReturnObject(returnObject);

    }

    /**查看特定商品分享活动（所有用户均可）
     * @author yhr
     * @date Created in 2020/12/2 10:33
     **/
    @Audit
    @ApiOperation(value = "查看特定商品分享活动（所有用户均可）",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "skuId",    value ="SKUId",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "shopId",     value ="店铺Id",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",      value ="每页数目",  required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shareactivities")
    public Object getShareActivities(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize,
            @RequestParam(required = false, defaultValue = "")  Long skuId,
            @RequestParam(required = false, defaultValue = "")  Long shopId) {
        Object object = null;
        if(page <= 0 || pageSize <= 0) {
            object = Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = shareService.getShareActivities(page,pageSize,skuId,shopId);
            logger.debug("getShareById: getShares = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }
        return object;
    }

    /**
     * 管理员修改商品分享活动
     * @param id: 分享活动 id
     * @param shopId: 店铺Id shopId
     * @param vo 修改信息 ShareActivityVo 视图
     * @param bindingResult 校验信息
     * @return Object
     * @author 24320182203309 yhr
     * Created at 2020/12/5 20:20
     * Modified by 24320182203309 yhr at 2020/12/16 14:19
     **/
    @ApiOperation(value = "管理员修改商品分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", required = true, dataType="Integer",value="分享活动id", paramType="path"),
            @ApiImplicitParam(name="shopId", required = true, dataType="Integer",value="店铺id", paramType="path"),
            @ApiImplicitParam(paramType = "body", dataType = "ShareActivityVo", name = "vo", value = "修改活动信息", required = true),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @PutMapping("/shops/{shopId}/shareactivities/{id}")
    public Object modifyShareActivity( @PathVariable Long id,
                                       @PathVariable Long shopId,
                                       @Validated @RequestBody ShareActivityVo vo,
                                       BindingResult bindingResult,
                                       @LoginUser Long userId) {
        if (logger.isDebugEnabled()) {
            logger.debug("modifyShareActivity: id = "+ id +" vo = " + vo);
        }
        // 校验前端数据
        Object returnObj = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObj != null) {
            logger.info("validate fail. shareActivity:" + id);
            System.out.println("validate fail");
            return returnObj;
        }
        //开始时间在结束时间后，返回错误码
        try {
            if (parseStringToDateTime(vo.getBeginTime()).isAfter(parseStringToDateTime(vo.getEndTime()))) {
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.Log_Bigger));
            }
        }
        catch (Exception ex){
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        }

        //校验strategy是否正确
        if(!validateStrategy(vo.getStrategy())){
            logger.error("wrong strategy");
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseUtil.fail(ResponseCode.FIELD_NOTVALID,"分享规则格式错误");
        }
        ReturnObject returnObject = shareService.modifyShareActivity(id,shopId,vo,userId);
        //没有操作的目标资源的权限
        if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
            httpServletResponse.setStatus(403);
        }
        else if(returnObject.getCode() == ResponseCode.SHAREACT_CONFLICT)httpServletResponse.setStatus(400);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员上线分享活动
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param Id
     * @return
     **/
    @ApiOperation(value = "管理员上线分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "上线活动对应的shopID", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "Id", value = "分享活动的ID", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/shops/{shopId}/shareactivities/{Id}/online")
    public Object getShareOnline(@LoginUser Long userId ,@PathVariable Long shopId,@PathVariable Long Id)
    {
        logger.debug("管理员上线分享活动: userId :"+ userId + " shopId :"+shopId+" activityId :"+Id);
        ReturnObject returnObject = shareService.getShareOnline(userId,shopId,Id);
        //没有操作的目标资源的权限
        if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
            httpServletResponse.setStatus(403);
        }

        return Common.decorateReturnObject(returnObject);
    }
    /**
     * 管理员下线分享活动
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param Id
     * @return
     **/
    @ApiOperation(value = "管理员下线分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "上线活动对应的shopID", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "Id", value = "分享活动的ID", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/shareactivities/{Id}")
    public Object getShareOffline(@LoginUser Long userId ,@PathVariable Long shopId,@PathVariable Long Id)
    {
        logger.debug("管理员下线分享活动: userId :"+ userId + " shopId :"+shopId+" activityId :"+Id);
        ReturnObject returnObject = shareService.getShareOffline(userId,shopId,Id);
        //没有操作的目标资源的权限
        if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
            httpServletResponse.setStatus(403);
        }

        return Common.decorateReturnObject(returnObject);
    }

    //String转为LocalDateTime
    public static LocalDateTime parseStringToDateTime(String time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, df);
    }
    //校验分享规则
    static public boolean validateStrategy(String strategyStr){
        ShareActivityStrategy strategy;
        try{
            strategy= JacksonUtil.toObj(strategyStr,ShareActivityStrategy.class);
        }
        catch (Exception ex){
            System.out.println("exception");
            return false;
        }
        if(strategy==null)return false;
        return strategy.validate();
    }
}
