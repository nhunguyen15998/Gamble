// package com.futech.entertainment.packages.core.configuration;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.context.annotation.Configuration;

// import com.futech.entertainment.packages.core.rabbitmq.Receiver;

// import org.springframework.amqp.core.Binding;
// import org.springframework.amqp.core.BindingBuilder;
// import org.springframework.amqp.core.Queue;
// import org.springframework.amqp.core.TopicExchange;
// import org.springframework.amqp.rabbit.connection.ConnectionFactory;
// import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
// import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

// @Configuration
// @ComponentScan("com.futech.entertainment.*")
// public class RabbitConfig {
//     static final String topicExchangeName = "spring-boot-exchange";
//     public static final String queueName = "spring-boot";

//     @Bean
//     Queue queue() {
//         return new Queue(queueName, false);
//     }

//     @Bean
//     TopicExchange exchange() {
//         return new TopicExchange(topicExchangeName);
//     }

//     @Bean
//     Binding binding(Queue queue, TopicExchange exchange) {
//         return BindingBuilder.bind(queue).to(exchange).with(queueName);
//     }

//     @Bean
//     SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
//         MessageListenerAdapter listenerAdapter) {
//         SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//         container.setConnectionFactory(connectionFactory);
//         container.setQueueNames(queueName);
//         container.setMessageListener(listenerAdapter);
//         return container;
//     }

//     @Bean
//     MessageListenerAdapter listenerAdapter(Receiver receiver) {
//         return new MessageListenerAdapter(receiver, "receiveMessage");
//     }
// }
