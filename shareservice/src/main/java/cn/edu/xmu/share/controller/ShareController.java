package cn.edu.xmu.share.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.share.model.vo.ShareActivityVo;
import cn.edu.xmu.share.model.vo.ShareVo;
import cn.edu.xmu.share.service.ShareService;
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

@Api(value = "分享服务", tags = "share")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/share", produces = "application/json;charset=UTF-8")
public class ShareController {

    private  static  final Logger logger = LoggerFactory.getLogger(ShareController.class);
    @Autowired
    private ShareService shareService;
    HttpServletResponse httpServletResponse;
    /**
     * 分享者生成分享链接
     * @author 24320182203309 杨浩然
     * createdBy 杨浩然 2020/12/4 13:57
     * modifiedBy 杨浩然 2020/12/4 19:20
     */
    @ApiOperation(value = "分享者生成分享链接")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "spuId", value = "生成分享链接所需的商品spuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/spus/{id}/shares")
    public Object addToCart(@PathVariable Long id,
                            @LoginUser  Long userId){
        System.out.println("进入测试");
        logger.debug("生成分享链接: id = "+ id);
        ReturnObject<VoObject> returnObject = shareService.createShare(id, userId);
        if (returnObject.getCode() == ResponseCode.OK) {
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
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "goodsSpuId",    value ="SPUId",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "beginTime",     value ="开始时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "endTime",       value ="结束时间",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shares")
    public Object getShares(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pagesize,
            @RequestParam(required = false)  Long goodsSpuId,
            @RequestParam(required = false)  String beginTime,
            @RequestParam(required = false)  String endTime,
            @LoginUser Long userId) {
        Object object = null;
        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = shareService.getShares(page,pagesize,goodsSpuId,beginTime,endTime,userId);
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
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "goodsSpuId",    value ="SPUId",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shops/{id}/shares")
    public Object adminGetShares(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pagesize,
            @RequestParam(required = false)  Long goodsSpuId,
            @PathVariable Long id) {
        Object object = null;
        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = shareService.adminGetShares(page,pagesize,goodsSpuId,id);
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
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "goodsSpuId",    value ="SPUId",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "beginTime",     value ="开始时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "endTime",       value ="结束时间",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/beshared")
    public Object getBeShared(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pagesize,
            @RequestParam(required = false)  Long goodsSpuId,
            @RequestParam(required = false)  String beginTime,
            @RequestParam(required = false)  String endTime,
            @LoginUser Long userId) {
        Object object = null;
        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = shareService.getBeShared(page,pagesize,goodsSpuId,beginTime,endTime,userId);
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
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "goodsSpuId",    value ="SPUId",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "beginTime",     value ="开始时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "endTime",       value ="结束时间",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shops/{id}/beshared")
    public Object adminGetBeShared(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pagesize,
            @RequestParam(required = false)  Long goodsSpuId,
            @RequestParam(required = false)  String beginTime,
            @RequestParam(required = false)  String endTime,
            @PathVariable Long id,
            @LoginUser Long userId) {
        Object object = null;
        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = shareService.adminGetBeShared(id,page,pagesize,goodsSpuId,beginTime,endTime);
            logger.debug("getBeShared: adminGetBeShared = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }
        return object;
    }

    /**
     * 平台或店家创建新的分享活动
     * @author 24320182203309 杨浩然
     * createdBy 杨浩然 2020/12/1 13:57
     * modifiedBy 杨浩然 2020/12/1 19:20
     */
    @ApiOperation(value = "平台或店家创建新的分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "body", dataType = "ShareActivityVo", name = "vo", value = "商品id和活动的起止时间与活动规则", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "shopId",  value ="店铺id",    required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "spuId",  value ="SPUId",    required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/shops/{shopId}/goods/{spuId}/shareactivities")
    public Object createShareActivity(@Validated @RequestBody ShareActivityVo vo, BindingResult bindingResult,
                                      @PathVariable Long shopId,
                                      @PathVariable Long spuId){
        System.out.println("进入测试");
        Object retObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != retObject) {
            logger.debug("validate fail");
            return retObject;
        }
        logger.debug("创建新的分享活动");
        ReturnObject<VoObject> returnObject = shareService.createShareActivity(vo,shopId,spuId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**查看特定商品分享活动（所有用户均可）
     * @author yhr
     * @date Created in 2020/12/2 10:33
     **/
    @Audit
    @ApiOperation(value = "查看特定商品分享活动（所有用户均可）",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "goodsSpuId",    value ="SPUId",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "shopId",     value ="店铺Id",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shareactivities")
    public Object getShareActivities(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pagesize,
            @RequestParam(required = false)  Long goodsSpuId,
            @RequestParam(required = false)  Long shopId) {
        Object object = null;
        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = shareService.getShareActivities(page,pagesize,goodsSpuId,shopId);
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
     * Modified by 24320182203309 yhr at 2020/12/6 0:19
     */
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
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("validate fail. shareActivity:" + id);
            return returnObject;
        }
        ReturnObject returnObj = shareService.modifyShareActivity(id,shopId, vo,userId);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员上线分享活动
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param Id
     * @return
     */
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
        System.out.println("进入测试");
        logger.debug("管理员上线分享活动: userId :"+ userId + " shopId :"+shopId+" Id :"+Id);
        ReturnObject returnObject = shareService.getShareOnline(userId,shopId,Id);
        return Common.decorateReturnObject(returnObject);

    }
    /**
     * 管理员下线分享活动
     * @author Kejian Shi
     * @param userId
     * @param shopId
     * @param Id
     * @return
     */
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
        System.out.println("进入测试");
        logger.debug("管理员下线分享活动: userId :"+ userId + " shopId :"+shopId+" Id :"+Id);
        ReturnObject returnObject = shareService.getShareOffline(userId,shopId,Id);
        return Common.decorateReturnObject(returnObject);

    }
}
