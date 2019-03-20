package com.reed.webim.netty.socketio.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import com.corundumstudio.socketio.listener.ExceptionListenerAdapter;

@Slf4j
public class BusinessExceptionListener extends ExceptionListenerAdapter {
	@Override
	public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
		log.error("ex:{}", e);
		ctx.close();

		return true;
	}
}