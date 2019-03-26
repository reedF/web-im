package com.reed.webim.netty.socketio.sdk.inner;

import com.reed.webim.netty.socketio.sdk.AbstractBaseSocketIOClient;
import com.reed.webim.netty.socketio.sdk.SocketIOClientConfig;
import com.reed.webim.netty.socketio.sdk.listener.AbstractBaseListener;

/**
 * 对内使用的为broker自己或内部target service创建client,不对外开放,通过 InnerSocketIOClientConfig 配置创建
 * @author reed
 *
 */
public class InnerSocketClient extends AbstractBaseSocketIOClient {

	public InnerSocketClient(SocketIOClientConfig clientConfig, AbstractBaseListener listener) {
		super(clientConfig, listener);
	}

}
