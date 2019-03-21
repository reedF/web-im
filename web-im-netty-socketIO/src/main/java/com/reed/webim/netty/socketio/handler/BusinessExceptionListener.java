package com.reed.webim.netty.socketio.handler;

import com.corundumstudio.socketio.listener.DefaultExceptionListener;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessExceptionListener extends DefaultExceptionListener {
	@Override
	public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		if (e instanceof IllegalStateException) {
			log.warn("ex:{}", e.getMessage());
		} else {
			super.exceptionCaught(ctx, e);
		}
		// ctx.close();
		return true;
	}
}