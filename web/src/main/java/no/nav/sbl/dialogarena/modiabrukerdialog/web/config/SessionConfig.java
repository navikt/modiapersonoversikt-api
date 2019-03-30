package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
@Configuration
public class SessionConfig {
    protected final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Bean
    public JedisConnectionFactory connectionFactory(
            @Value("${redis.sentinelmode}") boolean sentinelMode,
            @Value("${redis.host}") String host,
            @Value("${redis.port}") int port
    ) {

        if (sentinelMode) {
            return new JedisConnectionFactory(new RedisSentinelConfiguration()
                    .master("mymaster")
                    .sentinel(new RedisNode(host, port)));
        } else {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
            return new JedisConnectionFactory(redisStandaloneConfiguration);
        }
    }
}