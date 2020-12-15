package cn.edu.xmu.shoppingcart.service;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.shoppingcart.model.DeleteUrl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ChengYang Li
 */
@Service
public class RocketMQService
{
    private static final Logger logger = LoggerFactory.getLogger(RocketMQService.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendDeleteShoppingCartMessage(Long shoppingCartId, Long customerId){
        DeleteUrl url=new DeleteUrl(shoppingCartId, customerId);
        String json = JacksonUtil.toJson(url);
        Message message = MessageBuilder.withPayload(json).build();
        logger.info("sendDeleteShoppingCartMessage: message = " + message);
        rocketMQTemplate.sendOneWay("delete-topic:1", message);
    }
}
