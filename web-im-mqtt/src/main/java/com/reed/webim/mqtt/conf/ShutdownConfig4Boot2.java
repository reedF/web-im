package com.reed.webim.mqtt.conf;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot2.X Tomcat容器优雅停机
 *
 */
@Configuration
public class ShutdownConfig4Boot2 {
	/**
	 * 用于接受shutdown事件
	 * @return
	 */
	@Bean
	public GracefulShutdown gracefulShutdown() {
		return new GracefulShutdown();
	}

	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addConnectorCustomizers(gracefulShutdown());
		return tomcat;
	}

	private static class GracefulShutdown extends AbstractGracefulShutdown implements TomcatConnectorCustomizer {
		@Override
		public void customize(Connector connector) {
			this.connector = connector;
		}
	}
}