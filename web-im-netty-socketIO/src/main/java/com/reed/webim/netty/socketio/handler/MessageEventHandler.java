package com.reed.webim.netty.socketio.handler;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;

import lombok.extern.slf4j.Slf4j;

/**
 * 公共handler，处理默认namespace即""的endpoint
 * 注：使用@Component自动加载注入
 * @author reed
 *
 */
@Slf4j
@Component
public class MessageEventHandler extends BaseAbstractHandler {

	/**
	 * dynamic add namespace
	 */
	@Override
	@OnConnect
	public void onConnect(SocketIOClient client) {
		boolean r = addNs(client);
		if (r) {
			client.disconnect();
		} else {
			String clientId = client.getHandshakeData().getSingleUrlParam(QUERY_PARAMS_CLIENTID);
			UUID sessionId = client.getSessionId();
			saveSession(client);
			joinRoom(client);
			log.info("======connected for client:{},sessionId:{}======", clientId, sessionId);
		}
	}
}