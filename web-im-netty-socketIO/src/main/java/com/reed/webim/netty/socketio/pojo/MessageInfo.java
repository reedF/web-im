package com.reed.webim.netty.socketio.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class MessageInfo implements Serializable {
	// 源客户端id
	private String sourceClientId;
	// 目标客户端id
	private String targetClientId;
	// 消息类型
	private String msgType;
	// 消息内容
	private String msgContent;

}