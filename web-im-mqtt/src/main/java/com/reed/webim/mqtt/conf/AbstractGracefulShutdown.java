package com.reed.webim.mqtt.conf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import com.reed.webim.mqtt.silo.SiloRunner;

import ir.mqtt.silo.dispatcher.Worker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGracefulShutdown implements ApplicationListener<ContextClosedEvent> {

	protected volatile Connector connector;
	private final int waitTime = 120;
	@Autowired
	private SiloRunner silo;

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		waitSiloQueueDone();
		stopTomcatThread();
	}

	public void stopTomcatThread() {
		this.connector.pause();
		Executor executor = this.connector.getProtocolHandler().getExecutor();
		if (executor instanceof ThreadPoolExecutor) {
			try {
				ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
				log.info("shutdown start");
				threadPoolExecutor.shutdown();
				log.info("shutdown end");
				if (!threadPoolExecutor.awaitTermination(waitTime, TimeUnit.SECONDS)) {
					log.info("Tomcat 进程在" + waitTime + "秒内无法结束，尝试强制结束");
				}
				log.info("shutdown success");
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * check silo's queue has consume done
	 */
	public void waitSiloQueueDone() {
		try {
			if (silo != null && silo.getClient() != null) {
				if (silo.getClient().isConnected()) {
					silo.getClient().disconnect();
				}
				if (silo.getDispatcher() != null) {
					List<Worker> workers = silo.getDispatcher().getWorkerPool();
					if (workers != null && !workers.isEmpty()) {
						boolean isDone = false;
						Map<Integer, Boolean> tmp = new HashMap<>();
						while (!isDone) {
							for (int i = 0; i < workers.size(); i++) {
								Worker w = workers.get(i);
								if (w != null && w.getQueueSize().get() > 0) {
									tmp.put(i, false);
								} else {
									tmp.put(i, true);
								}
							}
							isDone = checkDone(tmp);
							if (!isDone) {
								Thread.sleep(200);
							}
						}
					}
				}
			}
			log.info("=====Silo queue done!======");
		} catch (InterruptedException e) {
			log.error("=====wait silo done error======", e);
			Thread.currentThread().interrupt();
		}
	}

	private boolean checkDone(Map<Integer, Boolean> tmp) {
		boolean r = false;
		int i = 0;
		for (Map.Entry<Integer, Boolean> item : tmp.entrySet()) {
			if (item != null && item.getValue().equals(true)) {
				i++;
			}
		}
		r = i == tmp.size();
		return r;
	}
}
