package com.reed.webim.netty.socketio.redis.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.ToString;

/**
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConfigurationProperties(prefix = "spring.redis", ignoreUnknownFields = false)
@Data
@ToString
public class RedisProperties {

	private int database;

	/**
	 * 等待节点回复命令的时间。该时间从命令发送成功时开始计时
	 */
	private int timeout;

	private String password;

	private String mode;

	/**
	 * 池配置
	 */
	private RedisPoolProperties pool;

	/**
	 * 单机信息配置
	 */
	private RedisSingleProperties single;

	/**
	 * 集群 信息配置
	 */
	private RedisClusterProperties cluster;

	/**
	 * 哨兵配置
	 */
	private RedisSentinelProperties sentinel;
}