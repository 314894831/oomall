package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.model.vo.FootPrintVo.FootPrintVo;
import cn.edu.xmu.other.service.FootPrintService;
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
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "sku_id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/skus/{id}/footprints")
    public Object addFootPrint(
            @LoginUser Long userid,
            @PathVariable("id") Long sku_id)
    {
        logger.debug("modifyFootPrintInfo: sku_id = " + sku_id);
        //校验前端数据
        Object object = null;

        if (sku_id == null) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }
        ReturnObject retObject = footPrintService.addFootPrint(userid, sku_id);
        if (retObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(retObject);
        }
        else {
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
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "did",             value ="店id",        required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "userId",          value ="用户id",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "beginTime",       value ="开始时间",     required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "endTime",         value ="结束时间",     required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",            value ="页码",        required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",        value ="每页数目",     required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/shops/{did}/footprints")
    public Object selectFootPrints(
            @Depart @ApiIgnore @RequestParam(required = false) Long departId,
            @RequestParam(required = false)  Integer userId,
            @RequestParam(required = false)  String  beginTime,
            @RequestParam(required = false)  String  endTime,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize) {
        if(departId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
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
