package test.netty.socketio.client;

import java.net.URISyntaxException;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.reed.webim.netty.socketio.handler.MessageEventHandler;
import com.reed.webim.netty.socketio.pojo.MessageInfo;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Java client using socket.io-client for test
 * 使用namespace连接时，在使用transports为websocket方式时，服务端出现异常的issue:
 * https://github.com/mrniko/netty-socketio/issues/303
 * "ERROR 15176 --- [ntLoopGroup-5-1] c.c.socketio.handler.InPacketHandler     : Error during data processing.
 * java.lang.IllegalStateException: null"
 * 在使用transports为polling时，无此问题
 */
public class TestNettySocketClient {

	private static final String url = "http://localhost:8081/ns1?ns=ns1&clientid=3&room="
			+ MessageEventHandler.ROOM_TAG_SERVICE;
	// private static final String url = "http://localhost:8081/?clientid=3";

	public static void main(String[] args) {
		MessageInfo msg = new MessageInfo("3", "1", null, "hello");
		IO.Options options = new IO.Options();
		// options.transports = new String[] { "websocket","polling"};
		options.transports = new String[] { "websocket" };
		// options.reconnectionAttempts = 2;
		options.reconnectionDelay = 1000;// 失败重连的时间间隔
		options.timeout = 500;// 连接超时时间(ms)
		// options.query = "ns=ns1&clientid=3";
		// options.decoder=;
		try {
			Socket socket = IO.socket(url, options);

			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					System.out.println("连接成功，sessionId:" + socket.id());
					// MessageInfo msg = new MessageInfo("3", "1", null,
					// "hello");
					// socket.emit(MessageEventHandler.ENDPOINT_P2P,
					// JSON.toJSON(msg));
				}
			});

			socket.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					System.out.println("正在连接......");
				}
			});

			socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					System.out.println("连接失败");
				}
			});
			socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					System.out.println("连接超时");
				}
			});
			socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					System.out.println("ERROR:" + args[0]);
				}
			});
			socket.on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					System.out.println("reconnecting.......");
				}
			});

			socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					// 如调用disconnect()后，将不会再重连
					// socket.disconnect();
					System.out.println("连接关闭");
				}
			});

			// broadcast
			socket.on(MessageEventHandler.ENDPOINT_BROADCAST, new Emitter.Listener() {
				// socket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					System.out.println("sessionId:" + socket.id());
					for (Object obj : args) {
						System.out.println(obj);
					}
					System.out.println("收到服务器应答，将要断开连接...");
					// socket.disconnect();
				}
			});

			// ack from client to server
			socket.on(MessageEventHandler.ENDPOINT_P2P, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					//check if has ackCallback
					if (args != null && args.length > 1) {
						if (args[args.length - 1] instanceof Ack) {
							Ack ack = (Ack) args[args.length - 1];
							// 通知发送方的回调方法执行：BaseAbstractHandler.sendMsgByP2p
							ack.call("Data received by client on " + new Date() + ",data is:" + args[0]);
						}
					}
					System.out.println("ACK-from-client");
					for (Object obj : args) {
						System.out.println(obj);
					}
				}
			});

			// send msg,and ack from server
			socket.emit(MessageEventHandler.ENDPOINT_P2P, JSON.toJSON(msg), new Ack() {
				@Override
				public void call(Object... args) {
					System.out.println("ACK-from-server:");
					for (Object obj : args) {
						System.out.println(obj);
					}
				}
			});

			socket.connect();

		} catch (URISyntaxException e) {
			System.out.println(e.getCause());
		}
	}
}