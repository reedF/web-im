# web-im
using spring boot websocket socketIO.js rabbitmq to implement web-im

# spring-websocket-sockjs-stomp
web-im-stomp  


# netty-socketio
refer to:  
https://cloud.tencent.com/developer/article/1023996  
https://github.com/mrniko/netty-socketio-demo  
1.netty-socketio-server  
https://github.com/mrniko/netty-socketio  
2.java连接netty-socketio  
https://github.com/socketio/socket.io-client-java  
3.负载均衡    
https://blog.csdn.net/dumarkee/article/details/84282149  



#Redisson
https://www.jianshu.com/p/2b19dec72ab0  

# MQTT persistence plugin
https://github.com/reedF/silo

# MQTT nginx using njs as LB
1.使用njs时，njs-0.2.4之后API变化，
https://github.com/nginx/njs/issues/148  
http://nginx.org/en/docs/njs/changes.html#njs0.2.4  
以下API或属性无效：
s.fromUpstream,s.buffer ,s.OK, s.ABORT, s.AGAIN, s.DECLINED, s.ERROR  
新代码mqtt.js可参考：https://github.com/dusanb94/mainflux/blob/5cf7bb459a183823014d8aaf8764a3c400aef160/docker/ssl/authorization.js  
本地实现：https://github.com/reedF/web-im/blob/master/web-im-mqtt/src/test/resources/mqtt-new.js


# HAproxy
Docker hub:
https://hub.docker.com/_/haproxy/  
1.docker pull haproxy  
2.
docker run -d -p 21883:21883 --name haproxy -e LANG=en_US.UTF-8 -v /c/Users/haproxy/haproxy.cfg:/usr/local/etc/haproxy/haproxy.cfg -v /c/Users/haproxy/haproxy_mqtt.lua:/usr/local/etc/haproxy/haproxy_mqtt.lua  -v /c/Users/haproxy/mqtt.lua:/usr/local/etc/haproxy/mqtt.lua --net host haproxy:latest

3.

