package cn.edu.xmu.shoppingcart.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.shoppingcart.model.vo.ShoppingCartVo;
import cn.edu.xmu.shoppingcart.service.RocketMQService;
import cn.edu.xmu.shoppingcart.service.ShoppingCartService;
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

@Api(value = "购物车服务", tags = "cart")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/carts", produces = "application/json;charset=UTF-8")
public class ShoppingCartController {
    private  static  final Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);
    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    HttpServletResponse httpServletResponse;
    /**
     * 买家将商品加入购物车
     *
     * @author 24320182203309 杨浩然
     * createdBy 杨浩然 2020/12/1 13:57
     * modifiedBy 杨浩然 2020/12/1 19:20
     */
    @ApiOperation(value = "买家将商品加入购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "body", dataType = "ShoppingCartVo", name = "vo", value = "添加购物车信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("")
    public Object addToCart(@Validated @RequestBody ShoppingCartVo vo,BindingResult bindingResult,
                            @LoginUser @ApiIgnore @RequestParam(required = true, defaultValue = "0") Long userId){
        Object retObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != retObject) {
            logger.debug("validate fail");
            return retObject;
        }
        logger.debug("addToCart: id = "+ vo.getGoodSkuID()+" userid: id = "+ userId);
        ReturnObject<VoObject> returnObject = cartService.addToCart(vo, userId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * @author yhr
     * @date Created in 2020/12/2 10:33
     **/
    @Audit
    @ApiOperation(value = "买家获得购物车列表",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("")
    public Object getCart(
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pagesize,
            @LoginUser @ApiIgnore @RequestParam(required = true, defaultValue = "0") Long userId) {
        Object object = null;
        if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = cartService.getCart(page, pagesize,userId);
            logger.debug("getCartById: getItems = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }
        return object;
    }

    /**
     * 买家修改购物车单个商品的数量或规格
     * @param id: 购物车明细 id
     * @param vo 修改信息 ShoppingCartVo 视图
     * @param bindingResult 校验信息
     * @return Object
     * @author 24320182203309 yhr
     * Created at 2020/12/2 20:20
     * Modified by 24320182203309 yhr at 2020/12/3 0:19
     */
    @ApiOperation(value = "买家修改购物车单个商品的数量或规格")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", required = true, dataType="Integer",value="购物车id", paramType="path"),
            @ApiImplicitParam(paramType = "body", dataType = "ShoppingCartVo", name = "vo", value = "添加购物车信息", required = true),
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @PutMapping("/{id}")
    public Object modifyCart(@LoginUser Long customerId, @PathVariable Long id,@Validated @RequestBody ShoppingCartVo vo, BindingResult bindingResult) {
        if (logger.isDebugEnabled()) {
            logger.debug("customer: id = "+customerId+" modifyCart: id = "+ id +" vo = " + vo);
        }
        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("incorrect data received while modifyCartInfo id = " + id);
            return returnObject;
        }
        ReturnObject returnObj = cartService.modifyCart(customerId, id, vo);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 买家删除购物车中商品
     * @param id: 购物车明细 id
     * @return Object
     * @author 24320182203309 yhr
     * Created at 2020/12/2 23:20
     * Modified by 24320182203309 yhr at 2020/12/3 0:19
     */
    @ApiOperation(value = "买家删除购物车中商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @DeleteMapping("/{id}")
    public Object deleteCartItem(@LoginUser Long customerId, @PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("customer id : "+ customerId +" deleteCartItem: id = "+ id);
        }
        ReturnObject returnObject = cartService.deleteItem(customerId, id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 买家清空购物车
     * @return Object
     * @author 24320182203309 yhr
     * Created at 2020/12/3 8:20
     * Modified by 24320182203309 yhr at 2020/12/3 0:19
     */
    @ApiOperation(value = "买家清空购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit // 需要认证
    @DeleteMapping("")
    public Object deleteCart( @LoginUser @ApiIgnore @RequestParam(required = true, defaultValue = "0") Long userId) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteCart: userId = "+ userId);
        }
        ReturnObject returnObject = cartService.deleteCart(userId);
        return Common.decorateReturnObject(returnObject);
    }
}
