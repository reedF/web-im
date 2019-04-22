package com.reed.webim.mqtt.msg.offline;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.reed.webim.mqtt.silo.SiloMsg;
import com.reed.webim.mqtt.silo.SiloRunner;
import com.reed.webim.mqtt.silo.dao.SiloDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OfflineMsgService {
	// tag for sub
	public static final String[] MultiSubs = { "#", "+" };

	@Autowired
	private SiloDao siloDao;

	@Autowired
	private SiloRunner siloRunner;

	// TODO ts
	public Long getLastDisConnectTime(String clientId) {
		Long t = System.currentTimeMillis();

		return t;
	}

	public void refreshDisConnectTime(String clientId, Long ts) {

	}

	public void getTopicAndResend(String topic, String clientId, Long start) {
		if (!StringUtils.isEmpty(topic) && !StringUtils.isEmpty(clientId)) {
			if (!topic.contains(MultiSubs[0]) && !topic.contains(MultiSubs[1])) {
				Long end = getLastDisConnectTime(clientId);
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
			log.info("=====resend msg to topic:{},ts:{}-{},total is:{},success is:{}======", topic, start, end,
					msgs.size(), i);
		}
	}
}
