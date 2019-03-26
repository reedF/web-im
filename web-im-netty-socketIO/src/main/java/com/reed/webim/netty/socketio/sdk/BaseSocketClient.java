package com.reed.webim.netty.socketio.sdk;

import com.reed.webim.netty.socketio.sdk.listener.AbstractBaseListener;

/**
 * 对外使用的client,通过 SocketIOClientConfig 配置创建
 * @author reed
 *
 */
public class BaseSocketClient extends AbstractBaseSocketIOClient {

	public BaseSocketClient(SocketIOClientConfig clientConfig, AbstractBaseListener listener) {
		super(clientConfig, listener);
	}

}
