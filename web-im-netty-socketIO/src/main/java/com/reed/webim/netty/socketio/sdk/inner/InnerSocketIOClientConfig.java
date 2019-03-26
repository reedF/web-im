package com.reed.webim.netty.socketio.sdk.inner;

import com.reed.webim.netty.socketio.sdk.SocketIOClientConfig;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 对内使用的配置，为broker自己或内部target service创建client,不对外开放
 * 
 * @author reed
 *
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InnerSocketIOClientConfig extends SocketIOClientConfig {

	// just for broker or target service using as a client
	public ClientTypeEnum clientType;
}
