package com.reed.webim.netty.socketio.handler;

/**
 * 动态加载handler，根据请求中的namespace,动态加载该namespace下的处理endpoint
 * 注：不使用@Component自动加载注入，而是在BaseAbstractHandler.addNs()方法内创建实例
 * @author reed
 *
 */
public class NamespaceHandler extends BaseAbstractHandler {

	private String namespace;

	public NamespaceHandler(String namespace) {
		this.namespace = namespace;
	}

}