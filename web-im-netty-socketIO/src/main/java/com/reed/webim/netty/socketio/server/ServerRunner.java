package com.reed.webim.netty.socketio.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOServer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServerRunner {
	@Autowired
	private SocketIOServer server;

	@PostConstruct
	private void start() {
		if (server != null) {
			server.start();
			log.info("Netty socket-io server is running........");
		}
	}

	@PreDestroy
	private void stop() {
		if (server != null) {
			server.stop();
			log.info("Netty socket-io server is stopped......");
		}
	}
}