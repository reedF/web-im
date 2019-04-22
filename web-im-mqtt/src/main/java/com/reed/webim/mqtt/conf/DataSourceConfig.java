package com.reed.webim.mqtt.conf;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

@Configuration
public class DataSourceConfig {
	@Primary
	@Bean(name = "siloDs")
	@ConfigurationProperties(prefix = "spring.ds.silo")
	public DataSource siloDs() {
		return DruidDataSourceBuilder.create().build();
	}

	@Bean(name = "siloJdbcTemplate")
	public JdbcTemplate siloJdbcTemplate(@Qualifier("siloDs") DataSource siloDs) {
		return new JdbcTemplate(siloDs);
	}

	@Bean(name = "siloNamedTemplate")
	public NamedParameterJdbcTemplate siloNamedTemplate(@Qualifier("siloDs") DataSource siloDs) {
		return new NamedParameterJdbcTemplate(siloDs);
	}

}
