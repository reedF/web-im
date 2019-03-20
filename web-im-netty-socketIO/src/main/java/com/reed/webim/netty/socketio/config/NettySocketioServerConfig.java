package com.reed.webim.netty.socketio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "netty.ws.server")
public class NettySocketioServerConfig {

	public String host;

	public Integer port;

	public int bossThreadNum;

	public int workerThreadNum;
	//是否使用redis保存netty连接
	public boolean enableRedissonStore;
}
