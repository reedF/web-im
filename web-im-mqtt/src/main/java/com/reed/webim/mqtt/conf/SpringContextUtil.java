package com.reed.webim.mqtt.conf;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {

	private static ApplicationContext context = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	// 传入线程中
	@SuppressWarnings("unchecked")
	public <T> T getBean(String beanName) {
		return (T) context.getBean(beanName);
	}

	// 国际化使用
	public String getMessage(String key) {
		return context.getMessage(key, null, Locale.getDefault());
	}

	/// 获取当前环境
	public String getActiveProfile() {
		String p = null;
		String[] profiles = context.getEnvironment().getActiveProfiles();
		if (profiles != null && profiles.length > 0) {
			p = profiles[0];
		}
		return p;
	}
}