package com.reed.webim.mqtt.webhook;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

@Data
public class EmqWebHookData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1334464072543431824L;
	
	private String action;
	private String client_id;
	private String username;
	private String conn_ack;
	private String topic;
	private Map<String, Object> opts;
	private String reason;
}
