package cn.edu.xmu.other.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dao.FavouriteDao;
import cn.edu.xmu.other.model.bo.FindFavourite;
import cn.edu.xmu.other.model.po.FavouriteGoodsPo;
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
public class FavouriteService {
    private Logger logger = LoggerFactory.getLogger(FavouriteService.class);

    @Autowired
    private FavouriteDao favouriteDao;



    /**
     * 分页查询收藏
     *
     * @author 24320182203271 汤海蕴
     * @param userName 用户名
     * @param page 页码
     * @param pageSize 页尺寸
     * @return ReturnObject<PageInfo<VoObject>>> 收藏列表
     */
    @Transactional
    public ReturnObject<PageInfo<VoObject>> findFavourites(Long userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);
        PageInfo<FavouriteGoodsPo> favouritePos = favouriteDao.findAllFavourites(userId, page, pageSize);
        List<VoObject> favourites = favouritePos.getList().stream().map(FindFavourite::new).collect(Collectors.toList());

        PageInfo<VoObject> returnObject = new PageInfo<>(favourites);
        returnObject.setPageNum(favouritePos.getPageNum());
        returnObject.setPageSize(favouritePos.getPageSize());
        returnObject.setTotal(favouritePos.getTotal());
        returnObject.setPages(favouritePos.getPages());

        return new ReturnObject<>(returnObject);
    }

    /**
     * 删除收藏商品
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     * @author
     */
    @Transactional
    public ReturnObject<Object> deleteFavourites(Long userId,Integer id) {
        return favouriteDao.deleteFavouritesById(userId,id);
    }

    /**
     * 增加角色权限
     * @param userId 用户id
     * @param skuId
     * @return
     */
    @Transactional
    public ReturnObject<VoObject> addFavourites(Long userId,Integer skuId){
        //新增
        ReturnObject<VoObject> ret = favouriteDao.addFavouriteById(userId,skuId);
        return ret;
    }

}
