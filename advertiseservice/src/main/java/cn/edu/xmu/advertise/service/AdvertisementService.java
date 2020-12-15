package cn.edu.xmu.advertise.service;

import cn.edu.xmu.advertise.dao.AdvertisementDao;
import cn.edu.xmu.advertise.model.bo.Advertisement;
import cn.edu.xmu.advertise.model.po.AdvertisementPo;
import cn.edu.xmu.advertise.model.vo.AdvertisementExamVo;
import cn.edu.xmu.advertise.model.vo.AdvertisementModifyVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ImgHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ChengYang Li
 */
@Service
//@DubboService(version = "1.0.1", group = "advertise")
public class AdvertisementService
{
    @Autowired
    AdvertisementDao advertisementDao;

    @Autowired
    TimeSegmentService timeSegmentService;

    private Logger logger = LoggerFactory.getLogger(AdvertisementService.class);

    @Value("${advertisementservice.dav.username}")
    private String davUsername;

    @Value("${advertisementservice.dav.password}")
    private String davPassword;

    @Value("${advertisementservice.dav.baseUrl}")
    private String baseUrl;

    /**
     * 管理员在广告时段下新建广告
     *
     * @param advertisement
     * @author ChengYang Li
     */
    public ReturnObject addAdUnderTimeSeg(Advertisement advertisement)
    {
        ReturnObject<Advertisement> retObj = advertisementDao.addAdUnderTimeSeg(advertisement);
        return retObj;
    }

    /**
     * 管理员修改广告的时段
     *
     * @author ChengYang Li
     */
    public ReturnObject updateAdTimeId(Long timeId, Long adId)
    {
        ReturnObject<Advertisement> retObj = advertisementDao.updateAdTimeId(timeId, adId);
        return retObj;
    }

    /**
     * 管理员查看某一个广告时间段的id
     *
     * @author ChengYang Li
     */
    public ReturnObject<PageInfo<VoObject>> findAllAdByTimeId(Long timeId, Integer page, Integer pagesize)
    {
        PageHelper.startPage(page, pagesize);
        PageInfo<AdvertisementPo> advertisementPos = advertisementDao.findAllAdByTimeId(timeId, page, pagesize);

        List<VoObject> advertisements = advertisementPos.getList().stream().map(Advertisement::new).collect(Collectors.toList());

        PageInfo<VoObject> returnObject = new PageInfo<>(advertisements);
        returnObject.setPages(advertisementPos.getPages());
        returnObject.setPageNum(advertisementPos.getPageNum());
        returnObject.setPageSize(advertisementPos.getPageSize());
        returnObject.setTotal(advertisementPos.getTotal());

        return new ReturnObject<>(returnObject);
    }

    /**
     * 管理员审核广告
     *
     * @author ChengYang Li
     */
    public ReturnObject examAdvertisement(Long adId, AdvertisementExamVo vo)
    {
        return advertisementDao.examAdvertisement(adId, vo);
    }

    /**
     * 管理员下架广告
     *
     * @author ChengYang Li
     */
    public ReturnObject offLineAdvertisement(Long adId)
    {
        return advertisementDao.offLineAdvertisement(adId);
    }

    /**
     * 管理员上架广告
     *
     * @author ChengYang Li
     */
    public ReturnObject onLineAdvertisement(Long adId)
    {
        return advertisementDao.onLineAdvertisement(adId);
    }

    /**
     * 管理员设置默认广告
     *
     * @author ChengYang Li
     */
    public ReturnObject setDefaultAdvertisement(Long adId)
    {
        return advertisementDao.setDefaultAdvertisement(adId);
    }

    /**
     * 管理员删除广告
     *
     * @author ChengYang Li
     */
    public ReturnObject deleteAdvertisement(Long adId)
    {
        return advertisementDao.deleteAdvertisement(adId);
    }

    /**
     * 管理员修改广告信息
     *
     * @author ChengYang Li
     */
    public ReturnObject updateAdvertisementById(Long adId, AdvertisementModifyVo vo)
    {
        return advertisementDao.updateAdvertisementById(adId, vo);
    }

    /**
     * 管理员上传广告图片
     *
     * @author ChengYang Li
     */
    public ReturnObject uploadImg(Long adId, MultipartFile multipartFile)
    {
        ReturnObject<Advertisement> adReturnObject = advertisementDao.getAdvertisementById(adId);

        if(adReturnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
            return adReturnObject;
        }
        Advertisement advertisement = adReturnObject.getData();

        ReturnObject returnObject = new ReturnObject();
        try{
            returnObject = ImgHelper.remoteSaveImg(multipartFile, 2, davUsername, davPassword, baseUrl);

            //文件上传错误
            if(returnObject.getCode()!=ResponseCode.OK){
                logger.debug(returnObject.getErrmsg());
                return returnObject;
            }


            String oldImgPath = advertisement.getImageUrl().substring(baseUrl.length(),advertisement.getImageUrl().length());
            advertisement.setImageUrl(baseUrl+returnObject.getData().toString());
            ReturnObject updateReturnObject = advertisementDao.updateImageUrl(advertisement);

            //数据库更新失败，需删除新增的图片
            if(updateReturnObject.getCode()==ResponseCode.FIELD_NOTVALID){
                ImgHelper.deleteRemoteImg(returnObject.getData().toString(),davUsername, davPassword,baseUrl);
                return updateReturnObject;
            }

            //数据库更新成功需删除旧图片，未设置则不删除
            if(oldImgPath!=null) {
                ImgHelper.deleteRemoteImg(oldImgPath, davUsername, davPassword,baseUrl);
            }
        }
        catch (IOException e){
            logger.debug("uploadImg: I/O Error:" + baseUrl);
            return new ReturnObject(ResponseCode.FILE_NO_WRITE_PERMISSION);
        }
        return returnObject;
    }

    /**
     * 获取当前时段广告列表
     *
     * @author ChengYang Li
     */
    public ReturnObject getAllAdByTimeNow()
    {
        Long segId=timeSegmentService.getSegIdByTimeNow();
        return advertisementDao.getAllAdByTimeNow(segId);
    }

    /**
     * 将处于该时段下的广告或秒杀活动的segId全部置为0
     *
     * @author ChengYang Li
     */

    public ReturnObject setSegIdToZero(Long segId)
    {
        return advertisementDao.setAdSegIdToZero(segId);
    }
}
