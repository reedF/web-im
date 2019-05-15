package com.reed.webim.netty.socketio.sdk.ack;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import com.reed.webim.netty.socketio.pojo.MessageInfo;

import io.socket.client.Ack;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象client发送消息时，client端接收到的来自broker端的ack
 * @author reed
 *
 */
@Slf4j
@Getter
public abstract class BaseMsgAck implements Ack, Callable<Boolean> {

	public final MessageInfo msg;

	private volatile Boolean flag = false;

	private FutureTask<Boolean> result = new FutureTask<>(this);

	private ExecutorService executors;

	public BaseMsgAck(MessageInfo msg, ExecutorService es) {
		this.msg = msg;
		this.executors = es;
	}

	public void waitAck() {
		try {
			flag = true;
			executors.submit(result);
			log.info("ACK-from-server:result status has done or not: {},result is {},msg is {}", result.isDone(),
					result.get(), msg);
		} catch (InterruptedException | ExecutionException e) {
			log.error("=====Ack FutureTask error=====", e);
		}
	}

	@Override
	public Boolean call() throws Exception {
		return flag;
	}

}