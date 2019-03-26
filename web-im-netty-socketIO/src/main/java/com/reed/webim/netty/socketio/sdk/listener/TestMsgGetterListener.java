package com.reed.webim.netty.socketio.sdk.listener;

import com.reed.webim.netty.socketio.pojo.MessageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试业务方法实现Demo
 * @author reed
 *
 */
@Slf4j
public class TestMsgGetterListener extends AbstractBaseListener {

	@Override
	public void doBusiness(MessageInfo msg) {
		log.info("test listener to get msg:{}", msg);
	}

}
