package com.reed.webim.netty.socketio.sdk.ack;

import com.reed.webim.netty.socketio.pojo.MessageInfo;

import io.socket.client.Ack;
import lombok.Getter;

/**
 * 抽象client发送消息时，client端接收到的来自broker端的ack
 * @author reed
 *
 */
@Getter
public abstract class BaseMsgAck implements Ack {

	public final MessageInfo msg;

	public boolean result = false;

	public BaseMsgAck(MessageInfo msg) {
		this.msg = msg;
	}
}