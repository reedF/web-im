package com.reed.webim.websocket.stomp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * 
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        // 注册一个Stomp 协议的endpoint,并指定 SockJS协议,广播－用
        stompEndpointRegistry.addEndpoint("/endpointWisely").setAllowedOrigins("*").withSockJS();
        // 注册一个名字为"endpointChat" 的endpoint,并指定 SockJS协议。 点对点-用
        stompEndpointRegistry.addEndpoint("/endpointChat").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //配置客户端订阅地址的前缀，topic-广播，user-点对点
        registry.enableSimpleBroker("/topic", "/user");
        //配置服务端接收地址（@MessageMapping）的前缀
        registry.setApplicationDestinationPrefixes("/app/");
        //配置点对点消息的地址前缀(@SendToUser或convertAndSendToUser时使用)
        registry.setUserDestinationPrefix("/user/");
    }
}
