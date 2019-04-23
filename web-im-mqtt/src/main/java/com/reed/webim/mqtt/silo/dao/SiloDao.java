package com.reed.webim.mqtt.silo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.reed.webim.mqtt.silo.SiloMsg;

@Repository
public class SiloDao {

	public static final String TB_SILO = "silo";
	public static final String TOPIC_LASTWILL = "client/lastwill/";

	@Autowired
	@Qualifier("siloJdbcTemplate")
	private JdbcTemplate siloJdbcTemplate;

	@Autowired
	@Qualifier("siloNamedTemplate")
	private NamedParameterJdbcTemplate siloNamedTemplate;

	public List<SiloMsg> getOfflineMsg(String topic, Long start, Long end) {
		StringBuffer sql = new StringBuffer("select * from " + TB_SILO);
		sql.append(" where topic = '" + topic + "'");
		sql.append(" and created >= " + start);
		sql.append(" and created <= " + end);
		List<SiloMsg> list = siloJdbcTemplate.query(sql.toString(), new SiloMsg());
		return list;
	}

	public Long getClientDisconnectTsByLastWill(String clientId) {
		Long t = 0l;
		if (clientId != null && clientId.length() > 0) {
			String topic = TOPIC_LASTWILL + clientId;
			StringBuffer sql = new StringBuffer("select * from " + TB_SILO);
			sql.append(" where topic = '" + topic + "'");
			sql.append(" order by created desc");
			sql.append(" limit 0,1");
			List<SiloMsg> msg = siloJdbcTemplate.query(sql.toString(), new SiloMsg());
			if (msg != null && !msg.isEmpty()) {
				t = msg.get(0).getCreated();
			}
		}
		return t;
	}
}
