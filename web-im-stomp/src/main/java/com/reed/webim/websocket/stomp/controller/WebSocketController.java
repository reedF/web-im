package com.reed.webim.websocket.stomp.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reed.webim.websocket.deepstream.vo.AuthData;
import com.reed.webim.websocket.deepstream.vo.AuthRequest;
import com.reed.webim.websocket.deepstream.vo.AuthResponse;
import com.reed.webim.websocket.deepstream.vo.ClientData;
import com.reed.webim.websocket.deepstream.vo.ServerData;
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

	/**
	 * for deepstream http auth webhook url
	 * @param auth
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/auth", method = { RequestMethod.POST })
	@ResponseBody
	public AuthResponse auth4Deepstream(@RequestBody AuthRequest auth, HttpServletRequest request,
			HttpServletResponse response) {
		AuthResponse r = new AuthResponse();
		if (auth != null && auth.getAuthData() != null) {
			AuthData d = auth.getAuthData();
			r.setUsername(d.getUsername());
			if (d.getUsername() != null) {
				// check user auth
				// ....
				// setting extend data
				r.setClientData(new ClientData());
				r.setServerData(new ServerData());
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}
		return r;
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
		messagingTemplate.convertAndSendToUser(toUserMessage.getUserId(), "/message", toUserMessage.getMessage());
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

	/**
	 * auth for MQTT such as EMQ:https://github.com/emqx/emqx-auth-http
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/mqtt/auth", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public AuthResponse auth4MQTT(HttpServletRequest request, HttpServletResponse response) {
		AuthResponse r = new AuthResponse();
		Enumeration<String> params = request.getParameterNames();
		String userName = request.getParameter("username");
		String pwd = request.getParameter("password");
		String clientid = request.getParameter("clientid");
		//ACL access 方式，1: 发布 2：订阅 3：发布/订阅,可用于判断是否是acl校验，null表示是auth校验,非空时表示是acl校验
		String access = request.getParameter("access");
		String topic = request.getParameter("topic");
		String ip = request.getParameter("ipaddr");
		if (clientid != null) {
			if (userName != null) {
				// check user auth
				// ....
				// setting extend data
				r.setClientData(new ClientData());
				r.setServerData(new ServerData());
			} else {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
		}
		return r;
	}
}
