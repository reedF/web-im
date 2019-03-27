package test.netty.socketio.client;

import com.reed.webim.netty.socketio.handler.BaseAbstractHandler;
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

	private static final String url = "http://localhost:8080";
	public static final String clientId = "4";

	public static void main(String[] args) throws InterruptedException {
		MessageInfo msg = new MessageInfo(clientId, "1", null, "hello, I am " + clientId);
		InnerSocketIOClientConfig config = new InnerSocketIOClientConfig(ClientTypeEnum.TARGETSERVICE);
		config.setUrl(url);
		config.setBrokerEndpoint(BaseAbstractHandler.ENDPOINT_P2P);
		config.setClientType(ClientTypeEnum.TARGETSERVICE);
		config.setNameSpace("/ns1");
		//config.setChannel(ClientTypeEnum.TARGETSERVICE.getChannelName());

		NettySocketIOClient client = NettySocketIOClientBuilder.INSTANCE.buildClient(clientId, config,
				new TestMsgGetterListener());

		client.start();
		
		for (int i = 0; i < 100; i++) {
			boolean r = client.sendMsg(BaseAbstractHandler.ENDPOINT_P2P, msg, true);
			System.out.println(r);
			Thread.sleep(10000);
		}
	}

}
