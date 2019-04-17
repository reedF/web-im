//package com.reed.webim.mqtt.conf;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import org.apache.catalina.connector.Connector;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.event.ContextClosedEvent;
//
///**
// * Spring Boot2.X Tomcat容器优雅停机
// *
// */
//@Configuration
//public class ShutdownConfig4Boot2 {
//	/**
//	 * 用于接受shutdown事件
//	 * @return
//	 */
//	@Bean
//	public GracefulShutdown gracefulShutdown() {
//		return new GracefulShutdown();
//	}
//
//	@Bean
//	public ServletWebServerFactory servletContainer() {
//		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
//		tomcat.addConnectorCustomizers(gracefulShutdown());
//		return tomcat;
//	}
//
//	private static class GracefulShutdown extends AbstractGracefulShutdown implements TomcatConnectorCustomizer {
//		private static final Logger log = LoggerFactory.getLogger(GracefulShutdown.class);
//		private volatile Connector connector;
//		private final int waitTime = 120;
//
//		@Override
//		public void customize(Connector connector) {
//			this.connector = connector;
//		}
//	}
//}