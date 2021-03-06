package com.dc.product.config;

import com.dc.product.mq.ProductChannelListener;
import com.dc.product.mq.ProductConfirmCallback;
import com.dc.product.mq.ProductConnectionListener;
import com.dc.product.mq.ProductReturnCallback;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MqConfig {

    @Resource(name = "connectionFactory")
    private CachingConnectionFactory connectionFactory;
    @Resource(name = "rabbitAdmin")
    private RabbitAdmin rabbitAdmin;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public void rabbitConfig() {
        // ConnectionListener
        connectionFactory.addConnectionListener(new ProductConnectionListener());
        connectionFactory.addChannelListener(new ProductChannelListener());
        connectionFactory.setConnectionNameStrategy(connectionFactory -> "PRODUCT_CONNECTION");

        RabbitTemplate rabbitTemplate = rabbitAdmin.getRabbitTemplate();
        // confirm
        rabbitTemplate.setConfirmCallback(confirmCallback());
        // return
        rabbitTemplate.setReturnCallback(returnCallback());
        // message converter
        rabbitTemplate.setMessageConverter(messageConverter());
    }

    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback() {
        return new ProductConfirmCallback();
    }

    @Bean
    public RabbitTemplate.ReturnCallback returnCallback() {
        return new ProductReturnCallback();
    }

    @Bean
    public Exchange delayExchange() {
        return new TopicExchange("delayExchange",true,false);
    }

    // 延迟队列
    @Bean
    public Queue delayQueue() {
        Map<String,Object> argMap = new HashMap<>();
        argMap.put("x-message-ttl",6000);
        argMap.put("x-dead-letter-exchange", "exchange");
        return new Queue("delayQueue",true,false,false, argMap);
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with("topic").noargs();
    }


    // 普通  交换机,队列,绑定设置
    @Bean
    public Exchange exchange() {
        return new TopicExchange("exchange");
    }

    @Bean
    public Queue queue() {
        return new Queue("queue");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("topic").noargs();
    }


}
