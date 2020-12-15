package cn.edu.xmu.other.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.mapper.CustomerPoMapper;
import cn.edu.xmu.other.mapper.FavouriteGoodsPoMapper;
import cn.edu.xmu.other.model.bo.AddFavourite;
import cn.edu.xmu.other.model.po.CustomerPo;
import cn.edu.xmu.other.model.po.CustomerPoExample;
import cn.edu.xmu.other.model.po.FavouriteGoodsPo;
import cn.edu.xmu.other.model.po.FavouriteGoodsPoExample;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class FavouriteDao {
    private static final Logger logger = LoggerFactory.getLogger(FavouriteDao.class);

    @Autowired
    private FavouriteGoodsPoMapper favouritePoMapper;

//    @Autowired
//    private  CustomerPoMapper customerPoMapper;

    /**
     * 分页查询所有收藏
     *
     * @param userId   用户id
     * @param page     页码
     * @param pageSize 页尺寸
     * @return PageInfo<FootPrintPo> 返回Po
     * @author 24320182203271 汤海蕴
     */
    public PageInfo<FavouriteGoodsPo> findAllFavourites(Long userId, Integer page, Integer pageSize) {
//        CustomerPoExample example1=new CustomerPoExample();
//        CustomerPoExample.Criteria criteria1=example1.createCriteria();
//        criteria1.andUserNameEqualTo(userName);
//        List<CustomerPo> customerPos= customerPoMapper.selectByExample(example1);
//        CustomerPo customerPo=customerPos.get(0);
//        Long userId=customerPo.getId();
//        System.out.println("userId="+userId);

        FavouriteGoodsPoExample example = new FavouriteGoodsPoExample();
        FavouriteGoodsPoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(userId);

        List<FavouriteGoodsPo> favourites = favouritePoMapper.selectByExample(example);
        //TODO 需要使用skuid查询商品信息

        logger.debug("findFavourites: retFavourites = " + favourites);

        return new PageInfo<>(favourites);
    }


    /**
     * 删除一个收藏商品
     *
     * @param userId 角色id
     * @param id     商品spuId
     * @return ReturnObject<Object> 删除结果
     * @author
     */
    public ReturnObject<Object> deleteFavouritesById(Long userId, Integer id) {
        ReturnObject<Object> retObj = null;
        FavouriteGoodsPoExample favouritePoId = new FavouriteGoodsPoExample();
        FavouriteGoodsPoExample.Criteria criteriaId = favouritePoId.createCriteria();
        criteriaId.andCustomerIdEqualTo(userId);
        Long Id = Long.valueOf(id);
        criteriaId.andGoodsSkuIdEqualTo(Id);

        List<FavouriteGoodsPo> favouritesPos = favouritePoMapper.selectByExample(favouritePoId);

        if (favouritesPos.isEmpty()) {
            logger.debug("deleteFavourites: goods not exist = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("收藏商品不存在：" + id));
        } else {
            System.out.println("******查找成功******");
            FavouriteGoodsPo favouritesPo = favouritesPos.get(0);
            int ret = favouritePoMapper.deleteByPrimaryKey(favouritesPo.getId());
            System.out.println("******favourites_goods_key=" + favouritesPo.getId());
            retObj = new ReturnObject<>();
        }
        return retObj;
    }


    /**
     * 增加一件收藏商品
     *
     * @param userId 用户 id
     * @return ReturnObject<VoObject> 新增结果
     * @author 24320182203271 汤海蕴
     */
    public ReturnObject<VoObject> addFavouriteById(Long userId, Integer skuId) {
        FavouriteGoodsPo favouritePo = new FavouriteGoodsPo();

        //查询是否已收藏
        FavouriteGoodsPoExample example = new FavouriteGoodsPoExample();
        FavouriteGoodsPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        Long SkuId = Long.valueOf(skuId);
        criteria.andGoodsSkuIdEqualTo(SkuId);
        List<FavouriteGoodsPo> favouritePos = favouritePoMapper.selectByExample(example);

        if (favouritePos.isEmpty()) {
            //如果没收藏，添加收藏
            favouritePo.setCustomerId(userId);
            favouritePo.setGoodsSkuId(SkuId);
            favouritePo.setGmtCreate(LocalDateTime.now());
            favouritePo.setGmtModified(null);

            try {
                int ret = favouritePoMapper.insert(favouritePo);

                if (ret == 0) {
                    //插入失败
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                } else {
                    //插入成功
                    //组装返回的bo
                    //TODO 需要使用skuid查询商品信息

                    AddFavourite addFavourite=new AddFavourite(favouritePo);

                    return new ReturnObject<>(addFavourite);
                }
            } catch (DataAccessException e) {
                // 数据库错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            } catch (Exception e) {
                // 其他Exception错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
            }

        } else {
            //已收藏
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("商品重复收藏"));
        }
    }
}
