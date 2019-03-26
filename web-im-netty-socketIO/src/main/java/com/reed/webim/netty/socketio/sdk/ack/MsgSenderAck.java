package com.reed.webim.netty.socketio.sdk.ack;

import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.reed.webim.netty.socketio.pojo.MessageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 校验ack
 * @author reed
 *
 */
@Slf4j
public class MsgSenderAck extends BaseMsgAck {

	public MsgSenderAck(MessageInfo msg) {
		super(msg);
	}

	/**
	 * 验证ack是否有效，根据ack返回的data,与发送的data是否一致,
	 * 对应broker的 BaseAbstractHandler.onEvent方法内的ackRequest.sendAckData
	 * 注：此种校验只为了展示ack的处理逻辑，消耗性能且不必须，真实应用可通过在broker端定义ackRequest.sendAckData返回的数据格式直接判断是否有效
	 */
	@Override
	public void call(Object... args) {
		for (Object obj : args) {
			if (obj instanceof JSONObject) {
				JSONObject json = (JSONObject) obj;
				if (msg.equals(JSON.parseObject(json.toString(), MessageInfo.class))) {
					result = true;
				}
			}
			log.info("ACK-from-server:result is {},msg is {}", result, obj);
		}
	}

}
