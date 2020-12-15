package cn.edu.xmu.shoppingcart.service.mq;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.shoppingcart.controller.ShoppingCartController;
import cn.edu.xmu.shoppingcart.model.DeleteUrl;
import cn.edu.xmu.shoppingcart.model.bo.ShoppingCart;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息消费者
 * @author ChengYang Li
 * @date Created in 2020/12/4 12:37
 **/
@Service
@RocketMQMessageListener(topic = "delete-topic", selectorExpression = "1", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "log-group")
public class DeleteUrlConsumerListener implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener
{
    private static final Logger logger = LoggerFactory.getLogger(DeleteUrlConsumerListener.class);

    private static Pattern p=Pattern.compile(".*\\d+.*");

    @Autowired
    private ShoppingCartController shoppingCartController;

    @Override
    public void onMessage(String message) {
        DeleteUrl deleteUrl = JacksonUtil.toObj(message, DeleteUrl.class);
        String url=deleteUrl.GetDeleteUrl();
        Long customerId=deleteUrl.getCustomerId();
        //通过判断url中是否含有数字来判断是清空购物车还是删除某一条购物车记录
        if(HasDigit(url)){
            shoppingCartController.deleteCartItem(customerId,deleteUrl.getShoppingCartId());
        }
        else{
            shoppingCartController.deleteCart(customerId);
        }

        logger.info("onMessage: got message deleteUrl =" + deleteUrl);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        logger.info("prepareStart: consumergroup =" + defaultMQPushConsumer.getConsumerGroup());
    }

    // 判断一个字符串是否含有数字
    public boolean HasDigit(String content)
    {
        boolean flag=false;
        Matcher m=p.matcher(content);
        if(m.matches()){
            flag=true;
        }
        return flag;
    }
}
