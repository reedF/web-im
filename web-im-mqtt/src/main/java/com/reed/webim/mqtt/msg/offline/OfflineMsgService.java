package com.reed.webim.mqtt.msg.offline;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.reed.webim.mqtt.silo.SiloMsg;
import com.reed.webim.mqtt.silo.SiloRunner;
import com.reed.webim.mqtt.silo.dao.SiloDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 供webhook使用，必须使用异步模式"@EnableAsync @Async"，否则会阻塞相关事件
 * @author reed
 *
 */
@Slf4j
@Service
@EnableAsync
public class OfflineMsgService {
	// tag for sub
	public static final String[] MultiSubs = { "#", "+" };
	// 离线消息有效期:1天
	public static final long OFFLINE_EXPIRE_TIME = 24 * 3600 * 1000;

	@Autowired
	private SiloDao siloDao;

	@Autowired
	private SiloRunner siloRunner;

	@Autowired
	private StringRedisTemplate template;

	@Autowired
	private RedisTemplate<String, Long> clientTsTemplate;

	/**
	 * 获取最近一次断开连接时间
	 * @param clientId
	 * @return
	 */
	public Long getLastDisConnectTime(String clientId) {
		Long t = System.currentTimeMillis();
		if (!StringUtils.isEmpty(clientId)) {
			Long ts = clientTsTemplate.opsForValue().get(clientId);
			if (ts == null) {
				ts = siloDao.getClientDisconnectTsByLastWill(clientId);
			}
			if (ts > 0) {
				t = ts;
			}
		}
		return t;
	}

	/**
	 * 刷新断开连接时间
	 * 注：供webhook使用，必须使用异步模式，否则会阻塞相关事件
	 * @param clientId
	 * @param ts
	 */
	@Async
	public void refreshDisConnectTime(String clientId, Long ts) {
		if (!StringUtils.isEmpty(clientId) && ts != null) {
			clientTsTemplate.opsForValue().set(clientId, ts);
		}
	}

	/**
	 * 重发离线期间消息
	 * 注：供webhook使用，必须使用异步模式，否则会阻塞相关事件
	 * @param topic
	 * @param clientId
	 * @param end
	 */
	@Async
	public void getTopicAndResend(String topic, String clientId, Long end) {
		if (!StringUtils.isEmpty(topic) && !StringUtils.isEmpty(clientId)) {
			if (!topic.contains(MultiSubs[0]) && !topic.contains(MultiSubs[1])) {
				// sleep for sub can get msg
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					log.error("=====resend offline msg error for sleep=======", e);
				}
				Long start = getLastDisConnectTime(clientId);
				if (end - start > OFFLINE_EXPIRE_TIME) {
					start = end - OFFLINE_EXPIRE_TIME;
				}
				resendOfflineMsg(topic, start, end);
			}
		}
	}

	public void resendOfflineMsg(String topic, Long start, Long end) {
		if (!StringUtils.isEmpty(topic) && start != null && end != null && start < end) {
			List<SiloMsg> msgs = siloDao.getOfflineMsg(topic, start, end);
			int i = 0;
			if (msgs != null && !msgs.isEmpty()) {
				for (SiloMsg m : msgs) {
					if (m != null) {
						if (siloRunner.getClient().publishMessage(m.getTopic(), m.getMessage())) {
							i++;
						}
					}
				}
			}
			log.info("=====resend offline msg to topic:{},ts:{}-{},total is:{},success is:{}======", topic, start, end,
					msgs.size(), i);
		}
	}
}
