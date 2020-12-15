package cn.edu.xmu.footprint.dao;

import cn.edu.xmu.footprint.mapper.FootPrintPoMapper;
import cn.edu.xmu.footprint.model.po.FootPrintPo;
import cn.edu.xmu.footprint.model.po.FootPrintPoExample;
import cn.edu.xmu.footprint.model.vo.FootPrintVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class FootPrintDao {
    private static final Logger logger = LoggerFactory.getLogger(FootPrintDao.class);

    @Autowired
    private FootPrintPoMapper footPrintPoMapper;

    /**
     * 增加一条足迹
     *
     * @author 24320182203271 汤海蕴
     * @param id 用户 id
     * @param vo Vo 对象
     * @return ReturnObject<FootPrint> 新增结果
     */
    public ReturnObject<Object> addFootPrintAndVo(Long id, FootPrintVo vo) {
        FootPrintPo footprintPo = new FootPrintPo();
        ReturnObject<Object> retObj = null;
        footprintPo.setCustomerId(id);
        footprintPo.setGoodsSpuId(vo.getGoodsSpuId());
        //footprintPo.getId();
        footprintPo.setGmtCreate(LocalDateTime.now());
        footprintPo.setGmtModified(LocalDateTime.now());

        try{
            int ret = footPrintPoMapper.insert(footprintPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertRole: insert footprint fail " + footprintPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + footprintPo.getId()));
            } else {
                //插入成功
                logger.debug("insertRole: insert footprint = " + footprintPo.toString());
                retObj = new ReturnObject<>(ResponseCode.OK);
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }


    /**
     * 分页查询所有角色
     *
     * @author 24320182203271 汤海蕴
     * @param userId 用户id
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param pageSize 页尺寸
     * @return PageInfo<FootPrintPo> 返回Po
     */
    public PageInfo<FootPrintPo>  findAllFootPrints(Integer userId, String beginTime,String endTime, Integer page, Integer pageSize) {
        FootPrintPoExample example = new FootPrintPoExample();
        FootPrintPoExample.Criteria criteria = example.createCriteria();
        if(userId==null)
            System.out.println("userId为空");
        else
        {
            System.out.println("userId非空，="+userId);
            Long UserId=Long.valueOf(userId);
            criteria.andCustomerIdEqualTo(UserId);
        }
        if(!beginTime.isBlank())
        {
            LocalDateTime BeginTime=LocalDateTime.parse(beginTime);
            criteria.andGmtCreateGreaterThanOrEqualTo(BeginTime);
        }
        if(!endTime.isBlank()) {
            LocalDateTime EndTime = LocalDateTime.parse(endTime);
            criteria.andGmtCreateLessThanOrEqualTo(EndTime);
        }
        System.out.println("example创建成功，example="+example);
        List<FootPrintPo> footPrints = footPrintPoMapper.selectByExample(example);

        logger.debug("findFootPrints: retFootPrints = "+footPrints);

        System.out.println("查询成功");
        return new PageInfo<>(footPrints);
    }

}
