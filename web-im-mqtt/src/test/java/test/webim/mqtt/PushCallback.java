package test.webim.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 发布消息的回调类
 * 
 * 必须实现MqttCallback的接口并实现对应的相关接口方法CallBack 类将实现 MqttCallback 
 * 每个客户机标识都需要一个回调实例。在此示例中，构造函数传递客户机标识以另存为实例数据。 在回调中，将它用来标识已经启动了该回调的哪个实例。
 * 必须在回调类中实现三个方法：
 * 
 * public void messageArrived(MqttTopic topic, MqttMessage message)接收已经预订的发布。
 * 
 * public void connectionLost(Throwable cause)在断开连接时调用。
 * 
 * public void deliveryComplete(MqttDeliveryToken token)) 接收到已经发布的 QoS 1 或 QoS 2
 * 消息的传递令牌时调用。 由 MqttClient.connect 激活此回调。
 * 
 */
@Slf4j
@Getter
@AllArgsConstructor
public class PushCallback implements MqttCallbackExtended {

	private MqttClient client;

	private String[] topics;

	private int[] qos;

	private boolean isNeedSub;

	public void connectionLost(Throwable cause) {
		// 连接丢失后，一般在这里面进行重连
		System.out.println("连接断开，可以做重连,错误原因：" + cause);
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("deliveryComplete---------" + token.isComplete());
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// subscribe后得到的消息会执行到这里面
		System.out.println("接收消息主题 : " + topic);
		System.out.println("接收消息Qos : " + message.getQos());
		System.out.println("接收消息内容 : " + new String(message.getPayload()));
	}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		try {
			// 配置options.setAutomaticReconnect(true)后会自动重连，但需要在此重新subscribe，否则无法获取消息
			if (isNeedSub && client != null && client.isConnected()) {
				client.subscribe(topics, qos);
			}
		} catch (MqttException e) {
			log.error("subcribe error when reconnected:{}", e);
		}
		log.info("连接成功：reconnect:{}, server URL:{}", reconnect, serverURI);
	}
}