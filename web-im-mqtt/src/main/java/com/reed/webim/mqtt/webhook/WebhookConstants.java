package com.reed.webim.mqtt.webhook;

/**
 * web hook for emq
 * @author reed
 *
 */
public class WebhookConstants {
	// actions:refer to https://github.com/emqx/emqx-web-hook
	public static final String ACTION_CONNECTED = "client_connected";
	public static final String ACTION_SUBSCRIBE = "client_subscribe";
	public static final String ACTION_DISCONNECTED = "client_disconnected";
	// opts
	//value is int
	public static final String OPTS_NL = "nl";
	//value is int
	public static final String OPTS_QOS = "qos";
	//value is int
	public static final String OPTS_RAP = "rap";
	//value is int
	public static final String OPTS_RC = "rc";
	//value is int
	public static final String OPTS_RH = "rh";
	//value is String
	public static final String OPTS_SHARE = "share";

}
