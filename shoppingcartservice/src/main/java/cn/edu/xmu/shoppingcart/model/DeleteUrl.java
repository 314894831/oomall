package cn.edu.xmu.shoppingcart.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ChengYang Li
 */
@Data
public class DeleteUrl  implements Serializable {
    private String baseUrl="/carts";
    private Long shoppingCartId;
    private Long customerId;

    public DeleteUrl(Long shoppingCartId, Long customerId)
    {
        this.shoppingCartId=shoppingCartId;
        this.customerId=customerId;
    }

    public String GetDeleteUrl()
    {
        //删除购物车明细
        if(this.shoppingCartId>0) {
            String url=baseUrl+"/"+this.shoppingCartId.toString();
            return url;
        }
        //清空购物车
        return baseUrl;
    }
}
