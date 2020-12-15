package cn.edu.xmu.advertise.controller;

import cn.edu.xmu.advertise.model.bo.TimeSegment;
import cn.edu.xmu.advertise.model.vo.TimeSegmentVo;
import cn.edu.xmu.advertise.service.TimeSegmentService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Api(value = "时间段服务", tags = "time")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class TimeSegmentController
{
    private  static  final Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private TimeSegmentService timeSegmentService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 平台管理员新增广告时间段
     * @author ChengYang li
     * @date Created in 2020/12/2 20:15
     **/
    @Audit
    @ApiOperation(value = "平台管理员新增广告时间段",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "TimesegmentVo", name = "vo", value = "时间段信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 604, message = "时间段冲突")
    })
    @PostMapping("/shops/{did}/advertisement/timesegments")
    public Object addAdTime(
            @Depart @ApiIgnore @RequestParam(required = false) Long departId,
            @Validated @RequestBody TimeSegmentVo vo, BindingResult bindingResult)
    {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("平台管理员新增广告时间段：vo = " + vo);
        }

        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (returnObject != null) {
            logger.info("incorrect data received while 平台管理员新增广告时间段：vo = " + vo);
            if (vo.getBeginTime().isBlank()) {
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.Log_BEGIN_NULL));
            }
            if (vo.getEndTime().isBlank()) {
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.Log_END_NULL));
            }
            return returnObject;
        }
        //校验开始时间是否大于结束时间
        if (vo.isBeginTimeBiggerThanEndTime()) {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.Log_Bigger));
        }

        TimeSegment timeSegment=vo.createTimeSegment();
        timeSegment.setType((byte)0);
        timeSegment.setGmtCreate(LocalDateTime.now());

        ReturnObject retObject = timeSegmentService.addTime(timeSegment);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 管理员获取广告时间段列表
     * @author ChengYang li
     * @date Created in 2020/12/3 16:19
     **/
    @Audit
    @ApiOperation(value = "管理员获取广告时间段列表",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = true)
    })
    @ApiResponses({
    })
    @GetMapping("/shops/{did}/advertisement/timesegments")
    public Object getAllAdTime(
            @LoginUser Long id,
            @Depart @ApiIgnore @RequestParam(required = false) Long departId,
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
            ReturnObject<PageInfo<VoObject>> returnObject = timeSegmentService.getAllAdTime(page, pagesize, (byte)0);
            logger.debug("getAllAdTime: timeSegments = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }

        return object;
    }

    /**
     * 平台管理员新增秒杀时间段
     * @author ChengYang li
     * @date Created in 2020/12/2 20:15
     **/
    @Audit
    @ApiOperation(value = "平台管理员新增秒杀时间段",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "TimesegmentVo", name = "vo", value = "时间段信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 604, message = "时间段冲突")
    })
    @PostMapping("/shops/{did}/flashsale/timesegments")
    public Object addFlashTime(
            @Depart @ApiIgnore @RequestParam(required = false) Long departId,
            @Validated @RequestBody TimeSegmentVo vo, BindingResult bindingResult)
    {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("平台管理员新增秒杀时间段：vo = " + vo);
        }

        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (returnObject != null) {
            logger.info("incorrect data received while 平台管理员新增秒杀时间段：vo = " + vo);
            if (vo.getBeginTime().isBlank()) {
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.Log_BEGIN_NULL));
            }
            if (vo.getEndTime().isBlank()) {
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.Log_END_NULL));
            }
            return returnObject;
        }
        //校验开始时间是否大于结束时间
        if (vo.isBeginTimeBiggerThanEndTime()) {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.Log_Bigger));
        }

        TimeSegment timeSegment=vo.createTimeSegment();
        timeSegment.setType((byte)1);
        timeSegment.setGmtCreate(LocalDateTime.now());

        ReturnObject retObject = timeSegmentService.addTime(timeSegment);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 管理员获取秒杀时间段列表
     * @author ChengYang li
     * @date Created in 2020/12/3 16:19
     **/
    @Audit
    @ApiOperation(value = "管理员获取秒杀时间段列表",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = true)
    })
    @ApiResponses({
    })
    @GetMapping("/shops/{did}/flashsale/timesegments")
    public Object getAllFlashTime(
            @LoginUser Long id,
            @Depart @ApiIgnore @RequestParam(required = false) Long departId,
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
            ReturnObject<PageInfo<VoObject>> returnObject = timeSegmentService.getAllAdTime(page, pagesize, (byte)1);
            logger.debug("getAllAdTime: timeSegments = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }

        return object;
    }

    /**
     * 平台管理员删除广告时间段
     * @author ChengYang li
     * @date Created in 2020/12/4 16:25
     **/
    @Audit
    @ApiOperation(value = "平台管理员删除广告时间段",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="时段id", required = true)
    })
    @ApiResponses({
    })
    @DeleteMapping("/shops/{did}/advertisement/timesegments/{id}")
    public Object deleteAdTime(@LoginUser Long id,
                               @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                               @PathVariable("id") Long segid) {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("平台管理员: id = "+ id +" 删除广告时间段：id = " + segid);
        }

        ReturnObject returnObj = timeSegmentService.deleteTimeSegmentById(segid, (byte)0);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 平台管理员删除秒杀时间段
     * TODO 需要一个将删除的秒杀时段下的秒杀活动的segId全部更改为0的接口
     * @author ChengYang Li
     * @date Created in 2020/12/4 16:35
     **/
    @Audit
    @ApiOperation(value = "平台管理员删除秒杀时间段",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did", value ="店id", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="时段id", required = true)
    })
    @ApiResponses({
    })
    @DeleteMapping("/shops/{did}/flashsale/timesegments/{id}")
    public Object deleteFlashTime(@LoginUser Long id,
                                  @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                                  @PathVariable("id") Long segid) {

        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("平台管理员: id = "+ id +" 删除秒杀时间段：id = " + segid);
        }

        ReturnObject returnObj = timeSegmentService.deleteTimeSegmentById(segid, (byte)1);
        return Common.decorateReturnObject(returnObj);
    }
}
