package com.reed.webim.mqtt.web;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.reed.webim.mqtt.webhook.EmqWebHookData;
import com.reed.webim.mqtt.webhook.WebhookConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
@RestController
public class MqttController {

	/**
	 * auth for MQTT such as EMQ:https://github.com/emqx/emqx-auth-http
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/mqtt/auth", method = { RequestMethod.POST, RequestMethod.GET })
	public String auth4MQTT(HttpServletRequest request, HttpServletResponse response) {
		String r = new String();
		Enumeration<String> params = request.getParameterNames();
		String userName = request.getParameter("username");
		String pwd = request.getParameter("password");
		String clientid = request.getParameter("clientid");
		// ACL access 方式，1: 发布 2：订阅
		// 3：发布/订阅,可用于判断是否是acl校验，null表示是auth校验,非空时表示是acl校验
		String access = request.getParameter("access");
		String topic = request.getParameter("topic");
		String ip = request.getParameter("ipaddr");
		if (clientid != null) {
			if (userName != null) {
				// check user auth
				// ....
				// setting extend data

			} else {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
		}
		return r;
	}

	/**
	 * webhook for mqtt,such as EMQ:https://github.com/emqx/emqx-web-hook
	 * 注：
	 * 1.需在EMQ中对web-hook插件配置需要的action:"web.hook.rule.*.*",默认开启全部aciton，可以根据需要注释无用的action.
	 * 2.EMQ UI界面里配置各个aciton的webhook url会导致插件无法启动
	 * 3.EMQ-3.0.1版的web-hook插件存在bug，开启publish等类型action时，启动插件后会导致pub、sub收发消息失败，
	 * 具体参见：https://github.com/emqx/emqx-web-hook/issues/93
	 * EMQ-3.1-beta.2已修复，但依然不能开启"web.hook.rule.message.delivered"，否则无法发消息
	 * 4.EMQ UI-plugins界面里配置"Advanced Config"内，可选择特定action，参见：https://github.com/emqx/emqx/issues/2042
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/mqtt/webhook", method = { RequestMethod.POST, RequestMethod.GET })
	public EmqWebHookData webhook4MQTT(@RequestBody EmqWebHookData data, HttpServletRequest request,
			HttpServletResponse response) {
		if (data != null) {
			// login
			if (data.getAction().equals(WebhookConstants.ACTION_CONNECTED)) {

			}
			// logout
			if (data.getAction().equals(WebhookConstants.ACTION_DISCONNECTED)) {

			}
			// sub
			if (data.getAction().equals(WebhookConstants.ACTION_SUBSCRIBE)) {

			}
		}
		log.info("=======Mqtt web hook data:{}========", data);
		return data;
	}
}
