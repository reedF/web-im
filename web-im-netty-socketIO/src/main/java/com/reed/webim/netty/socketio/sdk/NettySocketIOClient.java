package com.reed.webim.netty.socketio.sdk;

import com.reed.webim.netty.socketio.pojo.MessageInfo;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * 封装SDK对外接口
 * @author reed
 *
 */
public interface NettySocketIOClient {

	/**
	 * 启动client
	 * @return
	 */
	Socket start();

	/**
	 * 销毁client
	 */
	void stop();

	/**
	 * 注册client要订阅的broker端的endpoint
	 * @param endPoint
	 * @return
	 */
	void subscribeEndpoint(final String endPoint, final Emitter.Listener listener);

	/**
	 * 发送消息至broker端对应的endpoint
	 * @param endPoint
	 * @param msg
	 * @param isAck
	 * @return
	 */
	boolean sendMsg(final String endPoint, final MessageInfo msg, final boolean isAck);
}
