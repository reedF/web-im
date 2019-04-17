package test.webim.mqtt;

import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * 
 * Title:Server Description: 服务器向多个客户端推送主题，即不同客户端可向服务器订阅相同主题
 * 
 */
public class PublisherMqtt {

	// tcp://MQTT安装的服务器地址:MQTT定义的端口号
	public static final String HOST =  "tcp://172.16.32.68:21883";//"tcp://localhost:1883";//"tcp://nginxhost:21883"
	// 定义一个主题
	public static final String TOPIC = "test/t1";
	// 共享订阅topic,注：1.EMQ3集群模式下是否支持?EMQ3.0之后已支持：https://github.com/emqx/emqx/issues/1689
	public static final String SHARE_TOPIC = "$queue/" + TOPIC;

	// 定义MQTT的ID，可以在MQTT服务配置中指定
	private static final String clientid = "test-pub";

	private MqttClient client;
	private MqttTopic topic11;
	private String userName = "mosquitto";
	private String passWord = "mosquitto";

	private MqttMessage message;

	/**
	 * 构造函数
	 * 
	 * @throws MqttException
	 */
	public PublisherMqtt() {
		connect();
	}

	/**
	 * 用来连接服务器
	 */
	private void connect() {
		try {
			// MemoryPersistence设置clientid的保存形式，默认为以内存保存
			client = new MqttClient(HOST, clientid, new MemoryPersistence());
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(true);
			options.setAutomaticReconnect(true);
			options.setUserName(userName);
			options.setPassword(passWord.toCharArray());
			// 设置超时时间
			options.setConnectionTimeout(10);
			// 设置会话心跳时间
			options.setKeepAliveInterval(20);
			options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
			int[] Qos = { 2 };
			String[] topic = { TOPIC };

			client.setCallback(new PushCallback(client, topic, Qos, false));
			client.connect(options);

			topic11 = client.getTopic(TOPIC);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param topic
	 * @param message
	 * @throws MqttPersistenceException
	 * @throws MqttException
	 */
	public void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException, MqttException {
		MqttDeliveryToken token = topic.publish(message);
		token.waitForCompletion();
		System.out.println("message is published completely! " + token.isComplete() + "---" + message.toString());
	}

	/**
	 * 启动入口
	 * 
	 * @param args
	 * @throws MqttException
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) {
		int i = 0;
		PublisherMqtt server = new PublisherMqtt();
		while (true) {			
			i++;
			if (server.client.isConnected()) {
				server.message = new MqttMessage();
				server.message.setQos(2);
				// 设置保留消息(Retained Message)标志，保留消息(Retained
				// Message)会驻留在消息服务器，“后来的”订阅者订阅主题时仍可以接收该消息
				// 即设置为true后，在消息发送之后上线的订阅客户端也会获取到其连接之前的已发送的消息(最近一条)
				// 关于retain的说明
				// Mnesia：retained_message
				// 终端设备publish消息时，如果retain值是true，则服务器会一直记忆，哪怕是服务器重启。因为Mnesia会本地持久化。
				// 两种清除方式:
				// 如果服务器接收到终端publish某主题的消息，payload为空且retain值是true，则会删除这条持久化的消息。
				// 如果服务器接收到终端publish某主题的消息，payload为空且retain值是false，则不会删除这条持久化的消息。
				// 或在消息服务器设置保留消息的超期时间:https://github.com/emqx/emqx-retainer
				server.message.setRetained(false);
				if (i > 0) {
					server.message.setPayload(("hello:" + i + " at " + new Date()).getBytes());
				} else {
					server.message.setRetained(true);
				}
				try {
					server.publish(server.topic11, server.message);
					System.out.println(server.message.isRetained() + "------ratained状态");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}