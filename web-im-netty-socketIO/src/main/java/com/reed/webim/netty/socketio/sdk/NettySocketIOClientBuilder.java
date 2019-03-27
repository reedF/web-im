package com.reed.webim.netty.socketio.sdk;

import com.reed.webim.netty.socketio.sdk.inner.InnerSocketClient;
import com.reed.webim.netty.socketio.sdk.inner.InnerSocketIOClientConfig;
import com.reed.webim.netty.socketio.sdk.listener.AbstractBaseListener;

import lombok.extern.slf4j.Slf4j;

/**
 * client单例工厂，创建单例client连接
 * @author reed
 *
 */
@Slf4j
public enum NettySocketIOClientBuilder {

	/**
	 * use java enum to implement singleton pattern
	 */
	INSTANCE;

	private volatile static NettySocketIOClient client;

	public NettySocketIOClient buildClient(String clientId, SocketIOClientConfig config,
			AbstractBaseListener listener) {
		if (clientId == null) {
			log.error("clientId can't be empty.");
			return null;
		}
		if (config == null) {
			log.error("config can't be empty.");
			return null;
		}

		if (listener == null) {
			log.error("msg listener can not be null");
			return null;
		}

		if (client == null) {
			// for target service
			if (config instanceof InnerSocketIOClientConfig) {
				client = new InnerSocketClient(clientId, config, listener);
			} else {
				// for open sdk
				client = new BaseSocketClient(clientId, config, listener);
			}
		}

		return client;
	}
}