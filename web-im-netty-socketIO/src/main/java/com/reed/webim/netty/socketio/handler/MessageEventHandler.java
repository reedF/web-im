package com.reed.webim.netty.socketio.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.reed.webim.netty.socketio.pojo.MessageInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageEventHandler {
	public final static String ENDPOINT_P2P = "messageevent";
	public final static String ENDPOINT_BROADCAST = "broadcast";
	private Map<String, UUID> sessions = new HashMap<>();
	@Autowired
	private SocketIOServer server;

	// 添加connect事件，当客户端发起连接时调用，本文中将clientid与sessionid存入数据库
	// 方便后面发送消息时查找到对应的目标client,
	@OnConnect
	public void onConnect(SocketIOClient client) {
		String clientId = client.getHandshakeData().getSingleUrlParam("clientid");
		UUID sessionId = client.getSessionId();
		sessions.put(clientId, sessionId);
		log.info("======connected for client:{}======", clientId);
	}

	// 添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
	@OnDisconnect
	public void onDisconnect(SocketIOClient client) {
		String clientId = client.getHandshakeData().getSingleUrlParam("clientid");
		UUID sessionId = client.getSessionId();
		sessions.remove(clientId);
		log.info("======disconnected for client:{}======", clientId);
	}

	// 消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
	@OnEvent(value = ENDPOINT_P2P)
	public void onEvent(SocketIOClient client, AckRequest request, MessageInfo data) {
		String targetClientId = data.getTargetClientId();

		MessageInfo sendData = new MessageInfo();
		sendData.setSourceClientId(data.getSourceClientId());
		sendData.setTargetClientId(data.getTargetClientId());
		sendData.setMsgType("chat");
		sendData.setMsgContent(data.getMsgContent());
		if (StringUtils.isEmpty(targetClientId)) {
			sendMsgByBroadcast(sendData);
		} else {
			sendMsgByP2p(client, request, sendData);
		}
		// send to self
		client.sendEvent(ENDPOINT_P2P, sendData);
		log.info("========send msg:{}===========", data);
	}

	/**
	 * p2p
	 * @param client
	 * @param request
	 * @param sendData
	 */
	private void sendMsgByP2p(SocketIOClient client, AckRequest request, MessageInfo sendData) {
		UUID uuid = sessions.get(sendData.getTargetClientId());
		if (uuid != null) {
			server.getClient(uuid).sendEvent(ENDPOINT_P2P, sendData);
		} else {
			log.error("=====can not find client:{}=====", sendData.getTargetClientId());
		}
	}

	/**
	 * broadcast
	 * @param sendData
	 */
	private void sendMsgByBroadcast(MessageInfo sendData) {
		server.getBroadcastOperations().sendEvent(ENDPOINT_BROADCAST, sendData);
	}
}