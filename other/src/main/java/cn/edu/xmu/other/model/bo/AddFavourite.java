package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.FavouriteGoodsPo;
import cn.edu.xmu.other.model.vo.FavouriteVo.AddFavouriteRetVo;
import cn.edu.xmu.other.model.vo.FavouriteVo.AddFavouriteSkuGoodRetVo;
import cn.edu.xmu.other.model.vo.FavouriteVo.FindFavouriteRetVo;
import cn.edu.xmu.other.model.vo.FavouriteVo.FindFavouriteSkuGoodRetVo;
import lombok.Data;

import java.time.LocalDateTime;

/** 收藏Bo类
 *
 * @author 24320182203271 汤海蕴
 * createdBy 汤海蕴 2020/12/01 13:57
 * modifiedBy 汤海蕴 2020/12/01 19:20
 **/
@Data
public class AddFavourite implements VoObject
{
    private Long id;
    private Long customerId;
    private Long goodsSkuId;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    public AddFavourite(){
    }
    /**
     * 构造函数
     *
     * @author 24320182203271 汤海蕴
     * @param po 用PO构造
     * @return Favourite
     */
    public AddFavourite(FavouriteGoodsPo po) {
        this.id = po.getId();
        this.customerId = po.getCustomerId();
        this.goodsSkuId = po.getGoodsSkuId();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    @Override
    public Object createVo() {
        AddFavouriteRetVo vo=new AddFavouriteRetVo();
        vo.setId(this.id);
        vo.setGmtCreate(this.gmtCreate);
        vo.setAddFavouriteSkuGoodRetVo(this.getSkuGoodInfoBySkuId(this.goodsSkuId));
        return vo;
    }

    //TODO 集成商品模块，通过skuid查询商品的信息
    //@DubboReference()
    //IGoodsService iGoodsService;

    /**
     * 根据skuId查询商品信息
     */
    public AddFavouriteSkuGoodRetVo getSkuGoodInfoBySkuId(Long skuId)
    {
        AddFavouriteSkuGoodRetVo retVo=new AddFavouriteSkuGoodRetVo();
        //SkuDTO dto=iGoodsService.getSku(skuId);
        retVo.setId((long)1);
        retVo.setDisable((byte)0);
        retVo.setName("商品名");
        retVo.setImageUrl("url");
        retVo.setSkuSn("skuSn");
        retVo.setOriginalPrice((long)9.99);
        retVo.setPrice((long)99.9);
        retVo.setInventory(100);
        return retVo;
    }


    @Override
    public Object createSimpleVo() {
        return null;
    }
}
