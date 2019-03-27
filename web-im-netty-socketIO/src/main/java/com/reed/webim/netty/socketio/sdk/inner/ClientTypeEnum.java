package com.reed.webim.netty.socketio.sdk.inner;

/**
 * client type
 * @author reed
 *
 */
public enum ClientTypeEnum {

	TARGETSERVICE("room-target-services"), BROKER("room-brokers");
	// client using room's name
	private String channelName;

	public String getChannelName() {
		return channelName;
	}

	private ClientTypeEnum(String channelName) {
		this.channelName = channelName;
	}

}
