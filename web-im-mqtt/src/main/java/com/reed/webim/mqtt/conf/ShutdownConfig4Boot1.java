//package com.reed.webim.mqtt.conf;
//
//import org.apache.catalina.connector.Connector;
//import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
//import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
//import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
//import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Spring Boot1.X Tomcat容器优雅停机
// *
// */
//@Configuration
//public class ShutdownConfig4Boot1 {
//	/**
//	 * 用于接受shutdown事件
//	 * @return
//	 */
//	@Bean
//	public GracefulShutdown gracefulShutdown() {
//		return new GracefulShutdown();
//	}
//
//	/**
//	 * 用于注入 connector
//	 * @return
//	 */
//	@Bean
//	public EmbeddedServletContainerCustomizer tomcatCustomizer() {
//		return new EmbeddedServletContainerCustomizer() {
//			@Override
//			public void customize(ConfigurableEmbeddedServletContainer container) {
//				if (container instanceof TomcatEmbeddedServletContainerFactory) {
//					((TomcatEmbeddedServletContainerFactory) container).addConnectorCustomizers(gracefulShutdown());
//				}
//			}
//		};
//	}
//
//	private static class GracefulShutdown extends AbstractGracefulShutdown implements TomcatConnectorCustomizer {
//
//		@Override
//		public void customize(Connector connector) {
//			this.connector = connector;
//		}
//
//	}
//}