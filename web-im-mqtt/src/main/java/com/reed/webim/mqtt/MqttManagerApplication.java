package com.reed.webim.mqtt;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class MqttManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MqttManagerApplication.class, args);
	}

	/**
	 * thread pool for @EnableAsync to run @Async
	 * @return
	 */
	@Bean("taskExecutor")
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5); // 设置核心线程数
		executor.setMaxPoolSize(10); // 设置最大线程数
		executor.setQueueCapacity(20); // 设置队列容量
		executor.setKeepAliveSeconds(60); // 设置线程活跃时间（秒）
		executor.setThreadNamePrefix("offline-msg-sender-"); // 设置默认线程名称
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());// 设置拒绝策略
		executor.setWaitForTasksToCompleteOnShutdown(true); // 等待所有任务结束后再关闭线程池
		return executor;
	}
	
}