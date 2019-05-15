package com.reed.webim.netty.socketio.sdk.ack;

import java.util.concurrent.Callable;
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

	private Boolean flag = false;

	private FutureTask<Boolean> result = new FutureTask<>(this);

	private ExecutorService executors;

	public BaseMsgAck(MessageInfo msg, ExecutorService es) {
		this.msg = msg;
		this.executors = es;
	}

	public void waitAck() {
		//log.info("ACK-from-server0:future task has done: {},result is {},msg is {}", result.isDone(), flag,msg);
		executors.submit(result);
		log.info("ACK-from-server:future task has done: {},result is {},msg is {}", result.isDone(), flag,msg);
	}

	@Override
	public Boolean call() throws Exception {
		flag = true;
		return flag;
	}

}