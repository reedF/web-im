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
	public final static String ENDPOINT_CLIENT_DISPATCH = "clientdispatch";

	public Map<String, UUID> sessions = new HashMap<>();

	@Autowired
	public SocketIOServer server;

	// no need to add namespace
	// 添加connect事件，当客户端发起连接时调用，本文中将clientid与sessionid存入内存,方便后面发送消息时查找到对应的目标client,
	@OnConnect
	public void onConnect(SocketIOClient client) {
		String clientId = client.getHandshakeData().getSingleUrlParam("clientid");
		UUID sessionId = client.getSessionId();
		saveSession(client);
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
	 * 对内：服务端->客户端消息路由入口，分布式下接收其他broker广播来的消息，判断session是否在本机内，并发送给相应client
	 * @param client
	 * @param ackRequest
	 * @param data
	 */
	@OnEvent(value = ENDPOINT_CLIENT_DISPATCH)
	public void onClientDispath(SocketIOClient client, AckRequest ackRequest, MessageInfo data) {
		// check is ack requested by client,but it's not required check
		if (ackRequest.isAckRequested()) {
			// send ack response with data to client
			ackRequest.sendAckData("ACK:", data);
		}

		UUID uuid = sessions.get(data.getTargetClientId());
		if (uuid != null) {
			server.getNamespace(client.getNamespace().getName()).getClient(uuid).sendEvent(ENDPOINT_P2P, data);
		}

		log.info("========client dispatch msg:{}===========", data);
	}

	/**
	 * 根据client在url中传递的参数ns("host:port/{namespaceValue}?ns=namespaceValue")，判断是否动态创建新的namespace
	 * @param client
	 */
	public boolean addNs(SocketIOClient client) {
		boolean r = false;
		String defaultNs = client.getNamespace().getName();
		String ns = client.getHandshakeData().getSingleUrlParam("ns");
		if (!defaultNs.equals(ns)) {
			if (!StringUtils.isEmpty(ns)) {
				ns = "/" + ns;
				if (server.getNamespace(ns) == null) {
					SocketIONamespace namespace = server.addNamespace(ns);
					NamespaceHandler handler = new NamespaceHandler(ns);
					handler.server = server;
					namespace.addListeners(handler);
					r = true;
				}
			}
		}
		return r;
	}

	public void saveSession(SocketIOClient client) {
		String clientId = client.getHandshakeData().getSingleUrlParam("clientid");
		UUID sessionId = client.getSessionId();
		sessions.put(clientId, sessionId);
	}

	/**
	 * p2p
	 * @param client
	 * @param request
	 * @param sendData
	 */
	private void sendMsgByP2p(SocketIOClient client, AckRequest request, MessageInfo sendData) {
		UUID uuid = sessions.get(sendData.getTargetClientId());
		//local session
		if (uuid != null) {
			server.getNamespace(client.getNamespace().getName()).getClient(uuid).sendEvent(ENDPOINT_P2P, sendData);
		} else {
			sendMsgByClientDispatch(client, sendData);
			log.info("=====Client:{}, not on the server:{}=====", sendData.getTargetClientId(),
					server.getConfiguration().getHostname() + ":" + server.getConfiguration().getPort());
		}
	}

	/**
	 * broadcast to the same namespace's clients
	 */
	private void sendMsgByBroadcast(SocketIOClient client, MessageInfo sendData) {
		client.getNamespace().getBroadcastOperations().sendEvent(ENDPOINT_BROADCAST, sendData);
	}

	/**
	 * dispatch msg to other borkers
	 * @param client
	 * @param sendData
	 */
	private void sendMsgByClientDispatch(SocketIOClient client, MessageInfo sendData) {
		client.getNamespace().getBroadcastOperations().sendEvent(ENDPOINT_CLIENT_DISPATCH, sendData);
	}
}
