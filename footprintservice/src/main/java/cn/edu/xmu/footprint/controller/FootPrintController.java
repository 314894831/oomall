package cn.edu.xmu.footprint.controller;

import cn.edu.xmu.footprint.model.vo.FootPrintVo;
import cn.edu.xmu.footprint.service.FootPrintService;
import cn.edu.xmu.ooad.annotation.Audit;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;


/**
 * 足迹服务
 * @author Haiyun Tang
 * Modified at 2020/12/1 13:21
 **/
@Api(value = "足迹服务", tags = "footprint")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/footprint", produces = "application/json;charset=UTF-8")

public class FootPrintController {

    private  static  final Logger logger = LoggerFactory.getLogger(FootPrintController.class);

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private FootPrintService footPrintService;

    /**
     * 增加足迹
     * @return Object
     * createdBy Haiyun Tang 2020/12/01 16:05
     */
    @ApiOperation(value = "增加足迹")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "用户id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "FootPrintVo", name = "vo", value = "可填写的信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/users/{id}/footprints")
    public Object addFootPrint(@PathVariable Long id,
                               @Validated @RequestBody FootPrintVo vo, BindingResult bindingResult){

       System.out.println("测试数据传入成功：id="+id+"vo="+vo);

        logger.debug("modifyFootPrintInfo: id = "+ id +" vo = " + vo);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }

        ReturnObject retObject = footPrintService.addFootPrint(id,vo);
        if (retObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(retObject);
        } else {
            return Common.decorateReturnObject(retObject);
        }
    }


    /**
     * 管理员查看浏览记录
     * @author HY Tang
     * @date Created in 2020/12/3 0:33
     **/
    @ApiOperation(value = "管理员查看浏览记录",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization",   value ="用户token",   required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "userId",          value ="用户id",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "beginTime",       value ="开始时间",     required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "endTime",         value ="结束时间",     required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",            value ="页码",        required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",        value ="每页数目",     required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/footprints")
    public Object selectFootPrints(
            @RequestParam(required = false)  Integer userId,
            @RequestParam(required = false)  String  beginTime,
            @RequestParam(required = false)  String  endTime,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize) {

        logger.debug("selectAllFootPrints: page = "+ page +"  pageSize ="+pageSize);
        Object object = null;

        if(page <= 0 || pageSize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = footPrintService.findFootPrints(userId, beginTime,endTime, page, pageSize);
            object = Common.getPageRetObject(returnObject);
        }
        return object;

    }

}
