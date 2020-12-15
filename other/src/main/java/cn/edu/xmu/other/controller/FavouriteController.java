package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.service.FavouriteService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 收藏服务
 * @author Haiyun Tang
 * Modified at 2020/12/5 13:21
 **/

@Api(value = "收藏服务", tags = "favourite")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "favorites", produces = "application/json;charset=UTF-8")
public class FavouriteController {
    private  static  final Logger logger = LoggerFactory.getLogger(FavouriteController.class);

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private FavouriteService favouriteService;

    /**
     * 买家查看所有收藏的商品
     * @author HY Tang
     * @date Created in 2020/12/5 0:33
     **/
    @ApiOperation(value = "买家查看所有收藏的商品",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization",   value ="用户token",   required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",            value ="页码",        required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",        value ="每页数目",     required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("")
    public Object findFavourites(
            @LoginUser Long userId,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize) {

        logger.debug("findFavourites: page = "+ page +"  pageSize ="+pageSize);
        Object object = null;
        System.out.println("*************************************userId="+userId);

        if(page <= 0 || pageSize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = favouriteService.findFavourites(userId, page, pageSize);
            object = Common.getPageRetObject(returnObject);
        }
        return object;

    }


    /**
     * 买家删除某个收藏的商品
     * @param userId
     * @param id 收藏商品id
     * @return
     */
    @ApiOperation(value = "买家删除某个收藏的商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @DeleteMapping("/{id}")
    public Object deleteUser(
            @LoginUser Long userId,
            @PathVariable Integer id) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteGoods:goodsSpuId = "+id);
            System.out.println("userId="+userId);
        }
        ReturnObject returnObject = favouriteService.deleteFavourites(userId,id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 买家收藏商品
     * @param userId
     * @return
     */
    @ApiOperation(value = "买家收藏商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="spuId", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @PostMapping("/goods/{skuId}")
    public Object addFavourites(@LoginUser Long userId, @PathVariable Integer skuId) {
        if (logger.isDebugEnabled()) {
            logger.debug("addGoods:goodsSpuId = "+skuId);
            System.out.println("userId="+userId);
        }
        ReturnObject returnObject = favouriteService.addFavourites(userId,skuId);
        return Common.decorateReturnObject(returnObject);
    }

}
