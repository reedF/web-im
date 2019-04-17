package com.reed.webim.mqtt.silo;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import ir.mqtt.silo.client.MyMqttClient;
import ir.mqtt.silo.conf.SysConfig;
import ir.mqtt.silo.database.DatabaseWorkerPool;
import ir.mqtt.silo.dispatcher.SimpleDispatcher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 从MQTT broker持久化消息
 * @author reed
 *
 */
@Getter
@Slf4j
@Component
public class SiloRunner {

	private static String yamlName = "silo.yaml";

	private MyMqttClient client;

	private SysConfig sysConfig;
	
	private SimpleDispatcher dispatcher;

	@PostConstruct
	private void start() {
		if (client == null) {
			try {
				// URI f =
				// SiloRunner.class.getClassLoader().getResource(yamlName).toURI();
				URL f = ClassLoader.getSystemResource(yamlName);
				log.info("=========silo config file:{}===========", f);
				sysConfig = SysConfig.getInstance(f);
				if (sysConfig != null) {
					sysConfig.mqttClientId += "-" + getHost();
					sysConfig.mqttConf.setClientId(sysConfig.mqttClientId);
				}
			} catch (Exception e) {
				log.error("=======Silo ERROR...bad conf file.=========", e);
			}

			DatabaseWorkerPool.getInstance(sysConfig);

			client = MyMqttClient.getInstance(sysConfig.mqttConf);
			dispatcher = new SimpleDispatcher(sysConfig);
			client.setMqttListener(dispatcher);
			client.tryConnecting();
			log.info("Silo is running........");
		}
	}

	@PreDestroy
	private void stop() {
		if (client != null && client.isConnected()) {
			client.disconnect();
		}

		log.info("Silo is stopped......");
	}

	private String getHost() {
		String host = "";
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error("get server host Exception e:", e);
		}
		return host;
	}
}
