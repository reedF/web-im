package test.netty.socketio.client;

import com.reed.webim.netty.socketio.handler.BaseAbstractHandler;
import com.reed.webim.netty.socketio.handler.MessageEventHandler;
import com.reed.webim.netty.socketio.pojo.MessageInfo;
import com.reed.webim.netty.socketio.sdk.NettySocketIOClient;
import com.reed.webim.netty.socketio.sdk.NettySocketIOClientBuilder;
import com.reed.webim.netty.socketio.sdk.inner.ClientTypeEnum;
import com.reed.webim.netty.socketio.sdk.inner.InnerSocketIOClientConfig;
import com.reed.webim.netty.socketio.sdk.listener.TestMsgGetterListener;

/**
 * 测试通过SDK建立client
 */
public class TestSdkClient {

	private static final String url = "http://localhost:8080/ns1?ns=ns1&clientid=4&room="
			+ MessageEventHandler.ROOM_TAG_SERVICE;

	public static void main(String[] args) {
		MessageInfo msg = new MessageInfo("4", "1", null, "hello, I am 4");
		InnerSocketIOClientConfig config = new InnerSocketIOClientConfig();
		config.setUrl(url);
		config.setBrokerEndpoint(BaseAbstractHandler.ENDPOINT_P2P);
		config.setClientType(ClientTypeEnum.TARGETSERVICE);

		NettySocketIOClient client = NettySocketIOClientBuilder.INSTANCE.buildClient(config,
				new TestMsgGetterListener());

		client.start();

		boolean r = client.sendMsg(BaseAbstractHandler.ENDPOINT_P2P, msg, true);
		System.out.println(r);
	}

}
