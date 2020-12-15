package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.FavouriteGoodsPo;
import cn.edu.xmu.other.model.vo.FavouriteVo.FindFavouriteRetVo;
import cn.edu.xmu.other.model.vo.FavouriteVo.FindFavouriteSkuGoodRetVo;
import cn.edu.xmu.other.model.vo.FootPrintVo.FootPrintRetVo;
import lombok.Data;

import java.time.LocalDateTime;

/** 收藏Bo类
 *
 * @author 24320182203271 汤海蕴
 * createdBy 汤海蕴 2020/12/01 13:57
 * modifiedBy 汤海蕴 2020/12/01 19:20
 **/
@Data
public class FindFavourite implements VoObject
{
    private Long id;
    private Long customerId;
    private Long goodsSkuId;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    public FindFavourite(){
    }

    /**
     * 构造函数
     *
     * @author 24320182203271 汤海蕴
     * @param po 用PO构造
     * @return Favourite
     */
    public FindFavourite(FavouriteGoodsPo po) {
        this.id = po.getId();
        this.customerId = po.getCustomerId();
        this.goodsSkuId = po.getGoodsSkuId();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    @Override
    public Object createVo() {
        FindFavouriteRetVo vo=new FindFavouriteRetVo();
        vo.setId(this.id);
        vo.setGmtCreate(this.gmtCreate);
        vo.setFindFavouriteSkuGoodRetVo(this.getSkuGoodInfoBySkuId(this.goodsSkuId));
        return vo;
    }
    //TODO 集成商品模块，通过skuid查询商品的信息
    //@DubboReference()
    //IGoodsService iGoodsService;

    /**
     * 根据skuId查询商品信息
     */
    public FindFavouriteSkuGoodRetVo getSkuGoodInfoBySkuId(Long skuId)
    {
        FindFavouriteSkuGoodRetVo retVo=new FindFavouriteSkuGoodRetVo();
        //SkuDTO dto=iGoodsService.getSku(skuId);
        retVo.setId((long)428);
        retVo.setDisable((byte)0);
        retVo.setName("商品名");
        retVo.setImageUrl("url");
        retVo.setSkuSn("null");
        retVo.setOriginalPrice((long)299);
        retVo.setPrice((long)299);
        retVo.setInventory(100);
        return retVo;
    }


    @Override
    public Object createSimpleVo() {
        return null;
    }
}
