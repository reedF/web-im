package com.reed.webim.netty.socketio.sdk.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.reed.webim.netty.socketio.pojo.MessageInfo;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象消息接收者，SDK使用方需继承本类并根据业务实现doBusiness方法
 * 本类封装消息接收到后client端发送给broker端的ackCallback，同时暴露业务方法doBusiness供业务方实现
 * @author reed
 *
 */
@Slf4j
@Getter
public abstract class AbstractBaseListener implements Emitter.Listener {

	@Override
	public void call(Object... args) {
		ackCallback(args);
		if (args != null && args.length > 1) {
			if (args[0] instanceof MessageInfo) {
				doBusiness((MessageInfo) args[0]);
			}
			// fastjson
			if (args[0] instanceof JSONObject) {
				doBusiness(JSONObject.toJavaObject((JSONObject) args[0], MessageInfo.class));
			} else {
				// org.json
				String json = ((org.json.JSONObject) args[0]).toString();
				doBusiness(JSON.parseObject(json, MessageInfo.class));
			}
		}
	}

	/**
	 * send ack to server to invoke server's AckCallback method
	 * @param args
	 */
	public void ackCallback(Object... args) {
		// check if has ackCallback
		if (args != null && args.length > 1) {
			// ack from client to server
			if (args[args.length - 1] instanceof Ack) {
				Ack ack = (Ack) args[args.length - 1];
				// 通知发送方的回调方法执行,此处直接将原数据作为ack返回
				ack.call(args[0]);
			}
		}
		log.info("listener get msg：{}", args[0]);
	}

	/**
	 * 执行业务方法
	 * @param msg
	 */
	public abstract void doBusiness(MessageInfo msg);
}
