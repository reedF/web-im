package com.reed.webim.netty.socketio.sdk;

import lombok.Data;

@Data
public class SocketIOClientConfig {
	// broker hostname+port
	public String url;
	// broker暴露的端点
	public String brokerEndpoint;
	//namespace
	public String nameSpace;
	//room
	public String channel;
	// 失败重连的时间间隔
	public long reconnectionDelay = 1000;
	// 连接超时时间(ms)
	public long timeout = 20000;
}
