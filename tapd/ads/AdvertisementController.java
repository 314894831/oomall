package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.model.bo.Advertisement;
import cn.edu.xmu.other.model.vo.AdvertisementVo.AdvertisementExamVo;
import cn.edu.xmu.other.model.vo.AdvertisementVo.AdvertisementModifyVo;
import cn.edu.xmu.other.model.vo.AdvertisementVo.AdvertisementVo;
import cn.edu.xmu.other.model.vo.AdvertisementVo.StateVo;
import cn.edu.xmu.other.service.AdvertisementService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 广告模块
 * @author ChengYang Li
 */
@Api(value = "广告服务", tags = "advertise")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class AdvertisementController
{
    private  static  final Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 获得广告所有状态
     * @author ChengYang li
     * @date Created in 2020/12/5 11:08
     **/
    @Audit
    @ApiOperation(value = "获得广告所有状态",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/advertisement/states")
    public Object findAllAdState(@LoginUser Long id) {
        Advertisement.State[] states=Advertisement.State.class.getEnumConstants();
        List<StateVo> stateVos=new ArrayList<StateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new StateVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }

    /**
     * 管理员在广告时段下新增广告
     * @author ChengYang li
     * @date Created in 2020/12/7 15:35
     **/
    @Audit
    @ApiOperation(value = "管理员在广告时段下新增广告",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="时段id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AdvertisementVo", name = "vo", value = "可填写的广告信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 603, message = "达到广告时段上限")
    })
    @PostMapping("/shops/{did}/timesegments/{id}/advertisement")
    public Object addAdUnderTimeSeg(@LoginUser Long id,
                                    @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                                    @PathVariable("id") Long timeId,
                                    @Validated @RequestBody AdvertisementVo vo, BindingResult bindingResult)
    {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("平台管理员: id = "+ id +" 在时段 id = "+timeId+" 下新增广告 ：vo = " + vo);
        }
        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (returnObject != null) {
            logger.info("incorrect data received while 平台管理员: id = "+ id +" 在时段 id = "+timeId+" 下新增广告 ：vo = " + vo);
            if (vo.getBeginDate().isBlank()) {
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.Log_BEGIN_NULL));
            }
            if (vo.getEndDate().isBlank()) {
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.Log_END_NULL));
            }
            return returnObject;
        }

        //校验开始时间是否大于结束时间
        if (vo.isBeginTimeBiggerThanEndTime()) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.Log_Bigger));
        }

        Advertisement advertisement=vo.createAdvertisement();
        advertisement.setSegId(timeId);
        advertisement.setGmtCreate(LocalDateTime.now());
        advertisement.setImageUrl(null);
        advertisement.setMessage(null);
        advertisement.setState((byte)0);

        ReturnObject retObject = advertisementService.addAdUnderTimeSeg(advertisement);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 管理员修改广告时段
     * @author ChengYang li
     * @date Created in 2020/12/7 15:35
     **/
    @Audit
    @ApiOperation(value = "管理员修改广告时段",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "tid", value ="时段id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="广告id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 603, message = "达到广告时段上限")
    })
    @PostMapping("/shops/{did}/timesegments/{tid}/advertisement/{id}")
    public Object updateAdTimeId(@LoginUser Long id,
                                 @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                                 @PathVariable("tid") Long timeId,
                                 @PathVariable("id") Long adId)
    {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("平台管理员: id = "+ id +" 修改广告 id = "+adId+" 的时段");
        }

        ReturnObject retObject = advertisementService.updateAdTimeId(timeId, adId);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return Common.getRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * @author ChengYang li
     * @date Created in 2020/12/7 17:09
     **/
    @Audit
    @ApiOperation(value = "管理员查看某一个广告时间段的广告",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="广告时段id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "String", name = "beginDate",          value ="广告开始时间",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String", name = "endDate",          value ="广告结束时间",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/shops/{did}/timesegments/{id}/advertisement")
    public Object findAllAdByTimeId(
            @LoginUser Long id,
            @Depart @ApiIgnore @RequestParam(required = false) Long departId,
            @PathVariable("id") Long timeId,
            @RequestParam(required = false)  String beginDate,
            @RequestParam(required = false)  String endDate,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pagesize) {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        Object object = null;

        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = advertisementService.findAllAdByTimeId(beginDate, endDate, timeId, page, pagesize);
            logger.debug("findAllAdByTimeId: advertisements = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }

        return object;
    }

    /**
     * 管理员审核广告
     * @author ChengYang li
     * @date Created in 2020/12/7 17:34
     **/
    @Audit
    @ApiOperation(value = "管理员审核广告",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="广告id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AdvertisementExamVo", name = "vo", value = "审核意见信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 608, message = "广告状态禁止")
    })
    @PutMapping("/shops/{did}/advertisement/{id}/audit")
    public Object examAdvertisement(@LoginUser Long id,
                                    @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                                    @PathVariable("id") Long adId,
                                    @Validated @RequestBody AdvertisementExamVo vo, BindingResult bindingResult)
    {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("管理员: id = "+ id +" 审核广告：adId = " + adId);
        }

        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (returnObject != null) {
            logger.info("incorrect data received while 管理员: id = "+ id +" 审核广告：adId = " + adId);
            return returnObject;
        }

        if(!vo.getConclusion().equals("true")&&!vo.getConclusion().equals("false"))
        {
            logger.info("incorrect data received while 管理员: id = "+ id +" 审核广告：adId = " + adId);
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("审核意见只能为true或false")));
        }


        ReturnObject returnObj = advertisementService.examAdvertisement(adId, vo);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员下架广告
     * @author ChengYang li
     * @date Created in 2020/12/7 20:31
     **/
    @Audit
    @ApiOperation(value = "管理员下架广告",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="广告id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 608, message = "广告状态禁止")
    })
    @PutMapping("/shops/{did}/advertisement/{id}/offshelves")
    public Object offLineAdvertisement(@LoginUser Long id,
                                       @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                                       @PathVariable("id") Long adId)
    {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("管理员: id = "+ id +" 下架广告：adId = " + adId);
        }


        ReturnObject returnObj = advertisementService.offLineAdvertisement(adId);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员上架广告
     * @author ChengYang li
     * @date Created in 2020/12/7 20:31
     **/
    @Audit
    @ApiOperation(value = "管理员上架广告",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="广告id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 608, message = "广告状态禁止")
    })
    @PutMapping("/shops/{did}/advertisement/{id}/onshelves")
    public Object onLineAdvertisement(@LoginUser Long id,
                                      @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                                      @PathVariable("id") Long adId)
    {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("管理员: id = "+ id +" 上架广告：adId = " + adId);
        }

        ReturnObject returnObj = advertisementService.onLineAdvertisement(adId);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员设置默认广告
     * @author ChengYang li
     * @date Created in 2020/12/7 20:59
     **/
    @Audit
    @ApiOperation(value = "管理员设置默认广告",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="广告id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("/shops/{did}/advertisement/{id}/default")
    public Object setDefaultAdvertisement(@LoginUser Long id,
                                          @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                                          @PathVariable("id") Long adId)
    {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("管理员: id = "+ id +" 设置默认广告：adId = " + adId);
        }

        ReturnObject returnObj = advertisementService.setDefaultAdvertisement(adId);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员删除一个广告
     * @author ChengYang li
     * @date Created in 2020/12/7 21:37
     **/
    @Audit
    @ApiOperation(value = "管理员删除一个广告",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="广告id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 608, message = "广告状态禁止")
    })
    @DeleteMapping("/shops/{did}/advertisement/{id}")
    public Object deleteAdvertisement(@LoginUser Long id,
                                      @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                                      @PathVariable("id") Long adId)
    {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("管理员: id = "+ id +" 删除广告：adId = " + adId);
        }

        ReturnObject returnObj = advertisementService.deleteAdvertisement(adId);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员修改广告内容
     * @author ChengYang li
     * @date Created in 2020/12/7 23:17
     **/
    @Audit
    @ApiOperation(value = "管理员修改广告内容",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="广告id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AdvertisementModifyVo", name = "vo", value = "可修改的广告信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("/shops/{did}/advertisement/{id}")
    public Object updateAdvertisementById(@LoginUser Long id,
                                          @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                                          @PathVariable("id") Long adId,
                                          @Validated @RequestBody AdvertisementModifyVo vo, BindingResult bindingResult)
    {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("管理员: id = "+ id +" 修改广告：id = " + adId);
        }

        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (returnObject != null) {
            logger.info("incorrect data received while 管理员: id = "+ id +" 修改广告：id = " + adId);
            return returnObject;
        }

        ReturnObject returnObj = advertisementService.updateAdvertisementById(adId, vo);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员上传广告图片
     * @author ChengYang li
     * @date Created in 2020/12/8 10:07
     **/
    @Audit
    @ApiOperation(value = "管理员上传广告图片",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="广告id", required = true),
            @ApiImplicitParam(paramType = "formData", dataType = "file", name = "img", value ="文件", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 506, message = "该目录文件夹没有写入的权限")
    })
    @PostMapping("/shops/{did}/advertisement/{id}/uploadImg")
    public Object uploadAdvertisementImage(@LoginUser Long id,
                                           @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                                           @PathVariable("id") Long adId,
                                           @RequestParam("img") MultipartFile multipartFile)
    {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        logger.debug("平台管理员 id = "+id+" 上传广告图片: id = "+ adId +" img :" + multipartFile.getOriginalFilename());
        ReturnObject returnObject = advertisementService.uploadImg(adId,multipartFile);
        return Common.getNullRetObj(returnObject, httpServletResponse);
    }

    /**
     * 获取当前时段广告列表
     * @author ChengYang li
     * @date Created in 2020/12/8 19:15
     **/
    @ApiOperation(value = "获取当前时段广告列表",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/advertisement/current")
    public Object getAllAdByTimeNow()
    {
        logger.debug("获取当前时段广告列表");
        ReturnObject returnObject = advertisementService.getAllAdByTimeNow();
        return Common.getListRetObject(returnObject);
    }
}