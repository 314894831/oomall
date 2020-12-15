package cn.edu.xmu.address.service;

import cn.edu.xmu.address.dao.RegionDao;
import cn.edu.xmu.address.model.vo.RegionVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
public class RegionService
{
    private  static  final Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private RegionDao regionDao;

    /**
     * ID获取父地址
     * @author ChengYang Li
     * @param id
     * @return 地区
     */
    public ReturnObject<List> findParentRegionByPid(Long id)
    {
        ReturnObject<List> returnObject = null;
        returnObject=regionDao.findParentRegionByPid(id);
        if(returnObject != null) {
            logger.debug("findParentRegionByPid : " + returnObject);
        } else {
            logger.debug("findParentRegionByPid: Not Found");
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        return returnObject;
    }

    /**
     * @param id 地区 id
     * @param vo 地区视图 对象 含要修改地区的信息
     * @return 返回对象 ReturnObject
     * @author ChengYang Li
     * Created at 2020/11/29 19:37
     */
    public ReturnObject<Object> updateRegionInfo(Long id, RegionVo vo) {
        return regionDao.updateRegionByVo(id, vo);
    }

    /**
     * @param id 地区 id
     * @return 返回对象 ReturnObject
     * @author ChengYang Li
     * Created at 2020/12/1 08:23
     */
    public ReturnObject deleteRegionById(Long id)
    {
        return regionDao.deleteRegionById(id);
    }

    /**
     * @param id 地区 id
     * @param vo 地区视图 对象 含要增加的地区信息
     * @return 返回对象 ReturnObject
     * @author ChengYang Li
     * Created at 2020/12/1 08:53
     */
    public ReturnObject insertChildRegionByVo(Long id, RegionVo vo)
    {
        return regionDao.insertChildRegionByVo(id, vo);
    }
}
