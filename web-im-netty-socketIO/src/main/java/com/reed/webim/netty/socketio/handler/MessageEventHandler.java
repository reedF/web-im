package com.reed.webim.netty.socketio.handler;

import org.springframework.stereotype.Component;

/**
 * 公共handler，处理默认namespace即""的endpoint
 * 注：使用@Component自动加载注入
 * @author reed
 *
 */
@Component
public class MessageEventHandler extends BaseAbstractHandler {

}