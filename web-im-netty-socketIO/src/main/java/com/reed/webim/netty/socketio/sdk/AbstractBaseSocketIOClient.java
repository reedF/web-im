package com.reed.webim.netty.socketio.sdk;

import java.net.URISyntaxException;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.reed.webim.netty.socketio.pojo.MessageInfo;
import com.reed.webim.netty.socketio.sdk.ack.BaseMsgAck;
import com.reed.webim.netty.socketio.sdk.ack.MsgSenderAck;
import com.reed.webim.netty.socketio.sdk.listener.AbstractBaseListener;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractBaseSocketIOClient implements NettySocketIOClient {

	private final SocketIOClientConfig clientConfig;

	private final AbstractBaseListener listener;

	private Socket socket;

	public AbstractBaseSocketIOClient(SocketIOClientConfig clientConfig, AbstractBaseListener listener) {
		this.clientConfig = clientConfig;
		this.listener = listener;
	}

	@Override
	public Socket start() {
		initConnect(clientConfig);
		return socket.open();
	}

	@Override
	public void stop() {
		if (socket != null) {
			socket.close();
		}
	}

	@Override
	public boolean sendMsg(String endPoint, MessageInfo msg, boolean isAck) {
		boolean r = false;
		if (msg != null) {
			// send msg,and ack from server
			if (isAck) {
				BaseMsgAck ack = new MsgSenderAck(msg);
				socket.emit(endPoint, JSON.toJSON(msg), ack);
				r = ack.isResult();
			} else {
				socket.emit(endPoint, JSON.toJSON(msg));
				r = true;
			}
		}
		return r;
	}

	@Override
	public void subscribeEndpoint(String endPoint, Emitter.Listener listener) {
		if (socket != null && listener != null) {
			socket.on(endPoint, listener);
		}
	}

	public void initConnect(SocketIOClientConfig clientConfig) {
		if (clientConfig == null || StringUtils.isEmpty(clientConfig.url)) {
			log.error("client config is null or url is null");
		} else {
			IO.Options options = new IO.Options();
			// options.transports = new String[] { "websocket","polling"};
			options.transports = new String[] { "websocket" };
			// options.reconnectionAttempts = 2;
			options.reconnectionDelay = clientConfig.reconnectionDelay;
			options.timeout = clientConfig.timeout;
			try {
				socket = IO.socket(clientConfig.url, options);

				socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
					@Override
					public void call(Object... args) {
						log.info("连接成功，sessionId:" + socket.id());
					}
				});

				socket.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
					@Override
					public void call(Object... args) {
						log.info("正在连接......");
					}
				});

				socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
					@Override
					public void call(Object... args) {
						log.info("连接失败");
					}
				});
				socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
					@Override
					public void call(Object... args) {
						log.info("连接超时");
					}
				});
				socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
					@Override
					public void call(Object... args) {
						log.info("ERROR:" + args[0]);
					}
				});
				socket.on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
					@Override
					public void call(Object... args) {
						log.info("reconnecting.......");
					}
				});

				socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
					@Override
					public void call(Object... args) {
						log.info("连接关闭");
					}
				});

				subscribeEndpoint(clientConfig.brokerEndpoint, listener);

			} catch (URISyntaxException e) {
				log.error("SocketIO-client-java-SDK connect error:{}", e);
			}
		}
	}
}
