package com.reed.webim.mqtt.silo;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import lombok.Data;

/**
 * domain for silo msg, mapping to table: silo
 * @author reed
 *
 */
@Data
public class SiloMsg implements RowMapper<SiloMsg>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4511910851420349762L;

	private Long id;
	private String topic;
	private String message;
	private Long created;

	@Override
	public SiloMsg mapRow(ResultSet rs, int rowNum) throws SQLException {
		SiloMsg m = new SiloMsg();
		m.setId(rs.getLong("id"));
		m.setCreated(rs.getLong("created"));
		m.setMessage(rs.getString("message"));
		m.setTopic(rs.getString("topic"));
		return m;
	}

}
