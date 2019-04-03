package test.webim.mqtt;

import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SubscriberMqtt {

	public static final String HOST = PublisherMqtt.HOST;
	public static final String TOPIC = PublisherMqtt.SHARE_TOPIC;

	public static final String clientid = "test-sub";
	// 分组共享订阅
	public static final String GROUP_TOPIC = "$share/" + clientid + TOPIC;

	private MqttClient client;
	private MqttConnectOptions options;
	private String userName = "admin";
	private String passWord = "admin";

	private ScheduledExecutorService scheduler;

	private void start() {
		try {
			// host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
			client = new MqttClient(HOST, clientid, new MemoryPersistence());
			// MQTT的连接设置
			options = new MqttConnectOptions();
			// 设置是否清空session,即保留会话，设置为false时，那么该客户端上线，并订阅了主题"r"，该主题会一直存在，即使客户端离线，该主题也仍然会记忆在EMQ服务器内存,直到会话超时注销。
			// 当客户端离线又上线时，仍然会接收到离线期间别人发来的publish消息（QoS=0，1，2）。类似即时通讯软件，终端可以接收离线消息。
			// 除非客户端主动取消订阅主题，否则主题一直存在,直到会话超时注销。另外，Mnesia本地不会持久化session,subscription和topic，服务器重启则丢失。
			// 多用于sub方，且为非共享订阅模式（即sub只订阅某个topic，不使用共享模式标识），不建议在pub方或共享订阅模式下使用，原因：
			// 因为sub方只订阅自己的topic,在使用"$queue"等标识的共享订阅时，会导致某个sub在离线后，pub发的消息还会路由至已离线的sub作为离线消息等待其恢复上线后接收
			// 这里如果设置为false表示服务器会保留客户端的连接记录，设置为true表示每次连接到服务器都以新的身份连接
			options.setCleanSession(false);
			// 设置连接的用户名
			options.setUserName(userName);
			// 设置连接的密码
			options.setPassword(passWord.toCharArray());
			// 设置超时时间 单位为秒
			options.setConnectionTimeout(10);
			// 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
			options.setKeepAliveInterval(20);
			// 是否自动重连
			options.setAutomaticReconnect(true);

			int[] Qos = { 2 };
			String[] topic1 = { TOPIC };
			// 设置回调
			client.setCallback(new PushCallback(client, topic1, Qos, true));

			client.connect(options);

			MqttTopic topic = client.getTopic(TOPIC);
			// setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
			options.setWill(topic, "close".getBytes(), 2, false);

			// 订阅消息
			client.subscribe(topic1, Qos);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws MqttException {
		SubscriberMqtt client = new SubscriberMqtt();
		client.start();
	}
}