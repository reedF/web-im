package com.reed.webim.netty.socketio.pojo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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