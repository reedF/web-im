package com.reed.webim.netty.socketio.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.reed.webim.netty.socketio.handler.NamespaceHandler;

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
			//addNameSpace("ns1");
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

	private void addNameSpace(String namespace) {
		namespace = "/" + namespace;
		SocketIONamespace ns = server.addNamespace(namespace);
		ns.addListeners(new NamespaceHandler(namespace));
	}
}