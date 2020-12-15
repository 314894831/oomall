package cn.edu.xmu.address.controller;

import cn.edu.xmu.address.model.vo.RegionVo;
import cn.edu.xmu.address.service.RegionService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 地区控制器
 * @author ChengYang Li
 **/
@Api(value = "地区服务", tags = "address")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/region", produces = "application/json;charset=UTF-8")
public class RegionController
{
    private  static  final Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private RegionService regionService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    /**
     * 查询某个地区的所有上级地区
     * @author ChengYang li
     * @date Created in 2020/11/25 16:11
     **/
    @Audit
    @ApiOperation(value = "查询某个地区的所有上级地区",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="该地区id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("region/{id}/ancestor")
    public Object getParentRegionsById(@PathVariable("id") Long id)
    {
        Object returnObject = null;
        ReturnObject<List> parentRegion = regionService.findParentRegionByPid(id);
        logger.debug("findParentRegionByPid: regions = " + parentRegion.getData() + " code = " + parentRegion.getCode());

        if (!parentRegion.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
            returnObject = Common.getListRetObject(parentRegion);
        } else {
            returnObject = Common.getNullRetObj(new ReturnObject<>(parentRegion.getCode(), parentRegion.getErrmsg()), httpServletResponse);
        }
        return returnObject;
    }

    /**
     * 管理员修改某个地区
     * @author ChengYang li
     * @date Created in 2020/11/29 19:16
     **/
    @Audit
    @ApiOperation(value = "管理员修改某个地区",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="该地区id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "RegionVo", name = "vo", value = "可修改的地区信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("region/{id}")
    public Object updateRegion(@PathVariable("id") Long id, @Validated @RequestBody RegionVo vo, BindingResult bindingResult)
    {
        System.out.println("开始测试修改region");
        if (logger.isDebugEnabled()) {
            logger.debug("updateRegionInfo: id = "+ id +" vo = " + vo);
        }
        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (returnObject != null) {
            logger.info("incorrect data received while updateRegionInfo id = " + id);
            return returnObject;
        }
        ReturnObject returnObj = regionService.updateRegionInfo(id, vo);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员让某个地区无效
     * @author ChengYang Li
     * @date Created in 2020/12/1 08:16
     **/
    @Audit
    @ApiOperation(value = "管理员让某个地区无效",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="该地区id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @DeleteMapping("region/{id}")
    public Object insertChildRegion(@PathVariable("id") Long id)
    {
        ReturnObject returnObj = regionService.deleteRegionById(id);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员在地区下新增子地区
     * @author ChengYang li
     * @date Created in 2020/12/1 08:16
     **/
    @Audit
    @ApiOperation(value = "管理员让某个地区无效",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id", value ="该地区id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "RegionVo", name = "vo", value = "可修改的地区信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PostMapping("region/{id}")
    public Object deleteRegion(@PathVariable("id") Long id, @Validated @RequestBody RegionVo vo, BindingResult bindingResult)
    {
        System.out.println("开始测试废弃region");

        if (logger.isDebugEnabled()) {
            logger.debug("updateRegionInfo: id = "+ id +" vo = " + vo);
        }
        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (returnObject != null) {
            logger.info("incorrect data received while updateRegionInfo id = " + id);
            return returnObject;
        }

        ReturnObject returnObj = regionService.insertChildRegionByVo(id, vo);
        return Common.decorateReturnObject(returnObj);
    }
}
