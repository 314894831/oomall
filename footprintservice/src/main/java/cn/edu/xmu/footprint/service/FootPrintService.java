package cn.edu.xmu.footprint.service;

import cn.edu.xmu.footprint.model.bo.FootPrint;
import cn.edu.xmu.footprint.model.po.FootPrintPo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.footprint.dao.FootPrintDao;
import cn.edu.xmu.footprint.model.vo.FootPrintVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class FootPrintService {
    private Logger logger = LoggerFactory.getLogger(FootPrintService.class);

    @Autowired
    FootPrintDao footPrintDao;

/**
 * auth 业务: 根据用户id和vo对象增加足迹
 * @param id 用户 id
 * @param vo Vo 对象
 * @return 返回对象 ReturnObject
 * @author
 * Created at 2020/11/4 20:30
 */
@Transactional
public ReturnObject<Object> addFootPrint(Long id, FootPrintVo vo) {
    ReturnObject<Object> ret=footPrintDao.addFootPrintAndVo(id,vo);
        return ret;
    }



    /**
     * 分页查询足迹
     *
     * @author 24320182203271 汤海蕴
     * @param userId 用户id
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param pageSize 页尺寸
     * @return ReturnObject<PageInfo<VoObject>>> 足迹列表
     */
public ReturnObject<PageInfo<VoObject>> findFootPrints(Integer userId,String beginTime,String endTime,Integer page,Integer pageSize) {

    PageHelper.startPage(page, pageSize);
    PageInfo<FootPrintPo> footPrintPos = footPrintDao.findAllFootPrints(userId, beginTime,endTime, page, pageSize);
    List<VoObject> footPrints = footPrintPos.getList().stream().map(FootPrint::new).collect(Collectors.toList());

    System.out.println(footPrints);

    PageInfo<VoObject> returnObject = new PageInfo<>(footPrints);
    returnObject.setPageNum(footPrintPos.getPageNum());
    returnObject.setPageSize(footPrintPos.getPageSize());
    returnObject.setTotal(footPrintPos.getTotal());
    returnObject.setPages(footPrintPos.getPages());

    return new ReturnObject<>(returnObject);

    }
}


