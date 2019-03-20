package com.reed.webim.netty.socketio.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.reed.webim.netty.socketio.pojo.MessageInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseAbstractHandler {
	public final static String ENDPOINT_P2P = "messageevent";
	public final static String ENDPOINT_BROADCAST = "broadcast";

	public Map<String, UUID> sessions = new HashMap<>();

	@Autowired
	public SocketIOServer server;

	// 添加connect事件，当客户端发起连接时调用，本文中将clientid与sessionid存入内存,方便后面发送消息时查找到对应的目标client,
	@OnConnect
	public void onConnect(SocketIOClient client) {
		boolean r = addNs(client);
		if (r) {
			client.disconnect();
			return;
		}
		String clientId = client.getHandshakeData().getSingleUrlParam("clientid");
		UUID sessionId = client.getSessionId();
		sessions.put(clientId, sessionId);

		log.info("======connected for client:{},sessionId:{}======", clientId, sessionId);
	}

	// 添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
	@OnDisconnect
	public void onDisconnect(SocketIOClient client) {
		String clientId = client.getHandshakeData().getSingleUrlParam("clientid");
		sessions.remove(clientId);
		log.info("======disconnected for client:{}======", clientId);
	}

	// 消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
	@OnEvent(value = ENDPOINT_P2P)
	public void onEvent(SocketIOClient client, AckRequest ackRequest, MessageInfo data) {
		String targetClientId = data.getTargetClientId();
		data.setMsgType("chat");
		Collection<SocketIONamespace> ns = server.getAllNamespaces();
		Collection<SocketIOClient> cs = server.getBroadcastOperations().getClients();

		// check is ack requested by client,but it's not required check
		if (ackRequest.isAckRequested()) {
			// send ack response with data to client
			ackRequest.sendAckData("ACK:", data);
		}
		if (StringUtils.isEmpty(targetClientId)) {
			sendMsgByBroadcast(client, data);
		} else {
			sendMsgByP2p(client, ackRequest, data);
		}
		// send to self
		// client.sendEvent(ENDPOINT_P2P, data);
		log.info("========send msg:{}===========", data);
	}

	/**
	 * 根据client在url中传递的参数ns("host:port/{namespaceValue}?ns=namespaceValue")，判断是否动态创建新的namespace
	 * @param client
	 */
	public boolean addNs(SocketIOClient client) {
		boolean r = false;
		String ns = "/" + client.getHandshakeData().getSingleUrlParam("ns");
		if (server.getNamespace(ns) == null) {
			SocketIONamespace namespace = server.addNamespace(ns);
			NamespaceHandler handler = new NamespaceHandler(ns);
			handler.server = server;
			namespace.addListeners(handler);
			r = true;
		}
		return r;
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
			// sendMsgByBroadcast(sendData);
			log.warn("=====Local server can not find client:{}=====", sendData.getTargetClientId());
		}
	}

	/**
	 * broadcast
	 * @param sendData
	 */
	private void sendMsgByBroadcast(SocketIOClient client, MessageInfo sendData) {
		client.getNamespace().getBroadcastOperations().sendEvent(ENDPOINT_BROADCAST, sendData);
	}
}
