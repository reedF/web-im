package com.reed.webim.mqtt.silo;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.reed.webim.mqtt.conf.SpringContextUtil;

import ir.mqtt.silo.client.MyMqttClient;
import ir.mqtt.silo.conf.SysConfig;
import ir.mqtt.silo.database.DatabaseWorkerPool;
import ir.mqtt.silo.dispatcher.SimpleDispatcher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 从MQTT broker持久化消息
 * 
 * jar启动时命令：
 * java -jar target\web-im-mqtt.jar --spring.profiles.active=test
 * 注：不能使用"-Dspring.profiles.active="，springboot2.X此方式无效
 * @author reed
 *
 */
@Getter
@Slf4j
@Component
public class SiloRunner {

	private static String PROFILE = "{-profile}";
	private static String yamlName = "silo" + PROFILE + ".yaml";

	@Autowired
	private SpringContextUtil contextUtil;

	private MyMqttClient client;

	private SysConfig sysConfig;

	private SimpleDispatcher dispatcher;

	@PostConstruct
	private void start() {
		if (client == null) {
			try {
				String fileName = getActiveProfileConfigFile();
				URL f = Thread.currentThread().getContextClassLoader().getResource(fileName);
				//URL f = ClassLoader.getSystemResource(fileName);
				log.info("=========silo config file name:{},url:{}===========", fileName, f);
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

	private String getActiveProfileConfigFile() {
		String s = yamlName;
		String p = contextUtil.getActiveProfile();
		if (!StringUtils.isEmpty(p)) {
			s = s.replace(PROFILE, "-" + p);
		} else {
			s = s.replace(PROFILE, "");
		}
		return s;
	}
}
