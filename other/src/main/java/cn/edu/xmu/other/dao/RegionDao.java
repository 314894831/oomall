package cn.edu.xmu.other.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.mapper.RegionPoMapper;
import cn.edu.xmu.other.model.bo.Region;
import cn.edu.xmu.other.model.po.RegionPo;
import cn.edu.xmu.other.model.po.RegionPoExample;
import cn.edu.xmu.other.model.vo.AddressVo.RegionVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Chengyang Li
 */
@Repository
public class RegionDao
{
    private  static  final Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private RegionPoMapper regionPoMapper;


    public ReturnObject<List> findParentRegionByPid(Long id)
    {
        //记录该地区的所有父级地区
        List<Region> allParentRegions = new ArrayList<>();

        RegionPo region=regionPoMapper.selectByPrimaryKey(id);
        if(region==null) {
            return null;
        }

        Long pid=region.getPid();
        while(pid!=0)
        {
            RegionPoExample example = new RegionPoExample();
            RegionPoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(pid);
            List<RegionPo> parentRegions=regionPoMapper.selectByExample(example);
            RegionPo parentRegion=parentRegions.get(0);
            Region reg=new Region(parentRegion);
            allParentRegions.add(reg);
            logger.debug("findRegionsById: retUsers = "+parentRegion);
            pid=parentRegion.getPid();
        }

        return new ReturnObject<>(allParentRegions);
    }

    /**
     * 根据 id 修改地区信息
     *
     * @param vo 传入的 Region 对象
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     * Created at 2020/11/29 19:41
     */
    public ReturnObject<Object> updateRegionByVo(Long id, RegionVo vo)
    {
        RegionPo orig=regionPoMapper.selectByPrimaryKey(id);
        //不修改已被废弃的地区
        if (orig == null || orig.getState().intValue() == 1) {
            logger.info("地区不存在或已被弃用：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        Region region=new Region(orig);
        RegionPo po=region.createUpdatePo(vo);

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = regionPoMapper.updateByPrimaryKeySelective(po);
        } catch (DataAccessException e) {
            // 如果发生 Exception，判断是什么错误
            if (Objects.requireNonNull(e.getMessage()).contains("region.region_name_uindex")) {
                logger.info("地区名字重复：" + vo.getName());
                retObj = new ReturnObject<>(ResponseCode.MOBILE_REGISTERED);
            } else {
                // 其他情况属未知错误
                logger.error("数据库错误：" + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                        String.format("发生了严重的数据库错误：%s", e.getMessage()));
            }
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("地区不存在或已被弃用：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("地区 id = " + id + " 的信息已更新");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * 根据 id 废弃地址
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     * Created at 2020/12/1 08:23
     */
    public ReturnObject deleteRegionById(Long id)
    {
        RegionPo orig=regionPoMapper.selectByPrimaryKey(id);
        //不修改已被废弃的地区
        if (orig == null || orig.getState().intValue() == 1) {
            logger.info("地区不存在或已被弃用：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        ReturnObject<Object> retObj = null;

        //先废除自己
        orig.setPid((long)0);
        orig.setGmtModified(LocalDateTime.now());
        orig.setState((byte)1);
        int ret1 = regionPoMapper.updateByPrimaryKeySelective(orig);
        if (ret1 == 0) {
            logger.info("地区不存在或已被弃用：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("地区 id = " + id + " 已被废弃");
            retObj = new ReturnObject<>();
        }

        //当废除的地区是父级地区时，将他所有的子地区一起废除
        RegionPoExample example = new RegionPoExample();
        RegionPoExample.Criteria criteria = example.createCriteria();
        criteria.andPidEqualTo(orig.getId());
        List<RegionPo> pos=regionPoMapper.selectByExample(example);
        for(int i=0;i<pos.size();i++)
        {
            pos.get(i).setState((byte)1);
            pos.get(i).setPid((long)0);
            pos.get(i).setGmtModified(LocalDateTime.now());
            int ret2 = regionPoMapper.updateByPrimaryKeySelective(pos.get(i));
            if (ret2 == 0) {
                logger.info("地区不存在或已被弃用：id = " + pos.get(i).getId());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("地区 id = " + pos.get(i).getId() + " 已被废弃");
                retObj = new ReturnObject<>();
            }
            RegionPoExample example1 = new RegionPoExample();
            RegionPoExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andPidEqualTo(pos.get(i).getId());
            List<RegionPo> thirdPos=regionPoMapper.selectByExample(example1);
            for(int j=0;j<thirdPos.size();j++)
            {
                Region region=new Region(thirdPos.get(j));
                RegionPo po= (RegionPo) region.createVo();
                po.setState((byte)1);
                po.setPid((long)0);
                po.setGmtModified(LocalDateTime.now());

                // 更新数据库
                int ret3;
                try {
                    ret3 = regionPoMapper.updateByPrimaryKeySelective(po);
                } catch (DataAccessException e) {
                    // 其他情况属未知错误
                    logger.error("数据库错误：" + e.getMessage());
                    retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                            String.format("发生了严重的数据库错误：%s", e.getMessage()));
                    return retObj;
                } catch (Exception e) {
                    // 其他 Exception 即属未知错误
                    logger.error("严重错误：" + e.getMessage());
                    return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                            String.format("发生了严重的未知错误：%s", e.getMessage()));
                }
                // 检查更新有否成功
                if (ret3 == 0) {
                    logger.info("地区不存在或已被弃用：id = " + thirdPos.get(j).getId());
                    retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                } else {
                    logger.info("地区 id = " + thirdPos.get(j).getId() + " 已被废弃");
                    retObj = new ReturnObject<>();
                }
            }
        }

        return retObj;
    }

    /**
     * 在一个地区下新增一个子地区
     *
     * @param vo 传入的 Region 对象
     * @return 返回对象 ReturnObj
     * @author ChengYang Li
     * Created at 2020/11/29 19:41
     */
    public ReturnObject insertChildRegionByVo(Long id, RegionVo vo)
    {
        RegionPo orig=regionPoMapper.selectByPrimaryKey(id);
        //不修改已被废弃的地区
        if (orig == null) {
            logger.info("地区不存在：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(orig.getState().intValue()==1)
        {
            logger.info("地区已废弃：id = " + id);
            return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
        }

        Region region=new Region(orig);
        RegionPo po=region.createNewPo(vo);

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = regionPoMapper.insert(po);
        } catch (DataAccessException e) {
            // 如果发生 Exception，判断是什么错误
            if (Objects.requireNonNull(e.getMessage()).contains("region.region_name_uindex")) {
                logger.info("地区名字重复：" + vo.getName());
                retObj = new ReturnObject<>(ResponseCode.MOBILE_REGISTERED);
            } else {
                // 其他情况属未知错误
                logger.error("数据库错误：" + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                        String.format("发生了严重的数据库错误：%s", e.getMessage()));
            }
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("地区不存在或已被弃用：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("已在父级地区 id = " + id + " 下新增子地区 name = " + vo.getName());
            retObj = new ReturnObject<>();
        }
        return retObj;
    }
}
