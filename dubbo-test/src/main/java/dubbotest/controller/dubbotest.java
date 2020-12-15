package dubbotest.controller;

import cn.edu.xmu.goods.client.IGoodsService;
import cn.edu.xmu.goods.client.dubbo.SpuDTO;
import cn.edu.xmu.other.dto.CustomerDto;
import cn.edu.xmu.other.dto.TimeSegmentDto;
import cn.edu.xmu.other.impl.ICustomerService;
import cn.edu.xmu.other.impl.ITimeSegmentService;
import cn.edu.xmu.other.impl.InLoginService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ChengYang Li
 */
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class dubbotest
{
    @DubboReference(version = "1.0.0", check = false)
    private InLoginService inLoginService;

    @DubboReference(version = "1.0.0", check = false)
    private ICustomerService iCustomerService;

    @DubboReference(version = "0.0.1-SNAPSHOT")
    private IGoodsService iGoodsService;

    @DubboReference(version = "1.0.0")
    private ITimeSegmentService iTimeSegmentService;

    @RequestMapping("/login")
    public String Login()
    {
        return inLoginService.login("8606245097", "123456");
    }

    @RequestMapping("/customerInfo")
    public CustomerDto getCustomerInfo()
    {
        return iCustomerService.getCustomerById(1L);
    }

    @RequestMapping("/goodTest")
    public SpuDTO goodTest()
    {
        return iGoodsService.getSimpleSpuById(280L);
    }

    @RequestMapping("/timeTest")
    public List<TimeSegmentDto> timeTest()
    {
        return iTimeSegmentService.getAllFlashTimeSegment();
    }

}
