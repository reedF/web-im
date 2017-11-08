package com.reed.webim.websocket.stomp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.reed.webim.websocket.stomp.message.ClientMessage;
import com.reed.webim.websocket.stomp.message.ServerMessage;
import com.reed.webim.websocket.stomp.message.ToUserMessage;

/**
 * 
 */
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/ws")
    public String ws() {
        return "ws";
    }

    @RequestMapping(value = "/chat")
    public String chat() {
        return "chat";
    }

    @MessageMapping("/welcome")
    @SendTo("/topic/getResponse")
    public ServerMessage say(ClientMessage clientMessage) {
        System.out.println("clientMessage.getName() = " + clientMessage.getName());
        return new ServerMessage("Welcome , " + clientMessage.getName() + " !");
    }

    @MessageMapping("/cheat")
    public void cheatTo(ToUserMessage toUserMessage) {
        System.out.println("toUserMessage.getMessage() = " + toUserMessage.getMessage());
        System.out.println("toUserMessage.getUserId() = " + toUserMessage.getUserId());
        messagingTemplate.convertAndSendToUser(toUserMessage.getUserId(), "/message",
                toUserMessage.getMessage());
    }

    /**
     * 等效于cheatTo
     * @param toUserMessage
     * @return
     */
    @MessageMapping("/p2p")
    @SendToUser("/message")
    public ToUserMessage p2p(ToUserMessage toUserMessage) {
        return toUserMessage;
    }
}
