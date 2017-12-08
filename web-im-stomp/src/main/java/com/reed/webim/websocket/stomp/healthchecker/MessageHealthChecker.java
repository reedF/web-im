package com.reed.webim.websocket.stomp.healthchecker;

import java.util.Date;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * health checker
 * @author reed
 *
 */
@Component
public class MessageHealthChecker implements HealthIndicator {

    /** 
     * 在/health接口调用的时候，返回多一个属性："MessageHealthChecker":{"status":"UP","hello":"world"} 
     */
    @Override
    public Health health() {
        try {
            long count = check();
            if (count == 1) {
                return Health.up().withDetail("Hello", "world:" + count).build();
            } else {
                return Health.unknown().withDetail("BadRequest", "see you:" + count).build();
            }
        } catch (Exception e) {
            return Health.down(e).withDetail("GameOver", "Bye!").build();
        }
    }

    private long check() throws RuntimeException {
        long t = new Date().getTime() % 3;
        if (t == 0) {
            throw new RuntimeException("Not work well");
        }
        return t;
    }
}
