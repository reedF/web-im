package com.reed.webim.netty.socketio.sdk;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.reed.webim.netty.socketio.handler.BaseAbstractHandler;
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

	private String clientId;

	private Socket socket;

	private ExecutorService threadPool = Executors.newFixedThreadPool(10);

	public AbstractBaseSocketIOClient(String clientId, SocketIOClientConfig clientConfig,
			AbstractBaseListener listener) {
		this.clientId = clientId;
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
		if (threadPool != null && !threadPool.isShutdown()) {
			threadPool.shutdown();
		}
	}

	// 注：返回值r在isAck=true时，由于ack由异步线程执行，ack.result在当前线程内获取的值存在延迟，如使用同步方式获取结果会导致r取值错误，需使用异步方式
	@Override
	public Future<Boolean> sendMsg(String endPoint, MessageInfo msg, boolean isAck) {
		Future<Boolean> r = CompletableFuture.completedFuture(true);
		if (msg != null) {
			// send msg,and ack from server
			if (isAck) {
				BaseMsgAck ack = new MsgSenderAck(msg, threadPool);
				socket.emit(endPoint, JSON.toJSON(msg), ack);
				r = ack.getResult();
			} else {
				socket.emit(endPoint, JSON.toJSON(msg));
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
			String url = makeUpUrl(clientConfig);
			try {
				socket = IO.socket(url, options);

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

	public String makeUpUrl(SocketIOClientConfig clientConfig) {
		String paramNs = "?" + BaseAbstractHandler.QUERY_PARAMS_NAMESPACE + "=";
		String paramRoom = "&" + BaseAbstractHandler.QUERY_PARAMS_ROOM + "=";
		StringBuffer url = new StringBuffer(clientConfig.url.endsWith("/") ? clientConfig.url : clientConfig.url + "/");
		String ns = paramNs;
		if (!StringUtils.isEmpty(clientConfig.nameSpace)) {
			String nsValue = clientConfig.nameSpace;
			if (nsValue.startsWith("/")) {
				nsValue = nsValue.replace("/", "");
			}
			ns = nsValue + ns + nsValue;
		}
		String room = StringUtils.isEmpty(clientConfig.channel) ? paramRoom : paramRoom + clientConfig.channel;
		url.append(ns + "&" + BaseAbstractHandler.QUERY_PARAMS_CLIENTID + "=" + clientId);
		url.append(room);
		return url.toString();
	}
}
