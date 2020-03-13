package com.qf.shop_auth;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    //1.创建一个交换机
    @Bean
    public FanoutExchange getExchange() {
        return new FanoutExchange("mail_exchange");
    }

    //2.创建一个队列
    @Bean
    public Queue getQueue() {
        return new Queue("mail_queue");
    }

    //3.绑定交换机和队列，参数Spring会自己去容器找。
    @Bean
    public Binding getBinding(Queue getQueue, FanoutExchange getExchange) {
        return BindingBuilder.bind(getQueue).to(getExchange);
    }
}
