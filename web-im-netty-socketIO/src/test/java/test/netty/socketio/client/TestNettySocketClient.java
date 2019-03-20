package test.netty.socketio.client;

import java.net.URISyntaxException;

import com.alibaba.fastjson.JSON;
import com.reed.webim.netty.socketio.handler.MessageEventHandler;
import com.reed.webim.netty.socketio.pojo.MessageInfo;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Java client using socket.io-client for test
 */
public class TestNettySocketClient {

	private static final String url = "http://localhost:8080/ns1?ns=ns1&clientid=3";
	// private static final String url =
	// "http://localhost:9092/chat1?clientid=3";

	public static void main(String[] args) {
		IO.Options options = new IO.Options();
		options.transports = new String[] { "websocket" };
		options.reconnectionAttempts = 2;
		options.reconnectionDelay = 1000;// 失败重连的时间间隔
		options.timeout = 500;// 连接超时时间(ms)
		try {
			// final Socket socket =
			// IO.socket("http://localhost:8080/?token=123456",
			// options);//错误的token值连接示例
			Socket socket = IO.socket(url, options);

			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					System.out.println("连接成功，sessionId:" + socket.id());
					//MessageInfo msg = new MessageInfo("3", "1", null, "hello");
					//socket.emit(MessageEventHandler.ENDPOINT_P2P, JSON.toJSON(msg));
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
			// p2p
			// socket.on(MessageEventHandler.ENDPOINT_P2P, new
			// Emitter.Listener() {
			// @Override
			// public void call(Object... args) {
			// System.out.println("sessionId:" + socket.id());
			// System.out.println("P2P");
			// for (Object obj : args) {
			// System.out.println(obj);
			// }
			//
			// }
			// });

			socket.connect();

		} catch (URISyntaxException e) {
			System.out.println(e.getCause());
		}
	}
}