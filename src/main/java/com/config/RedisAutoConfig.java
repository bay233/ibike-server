package com.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedisAutoConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPool,
                                                         RedisClusterConfiguration jedisConfig) {
        JedisConnectionFactory factory = new JedisConnectionFactory(jedisConfig, jedisPool);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean   // 键[字符串]: 值[对象]
    public RedisTemplate redsiTemplate(RedisConnectionFactory conn) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(conn);
        template.afterPropertiesSet();
        return template;
    }

    @Bean     // 键[字符串]: 值[字符串]
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory conn) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(conn);
        template.afterPropertiesSet();
        return template;
    }

    @Configuration
    public static class JedisConf {
        @Value("${spring.redis.cluster.nodes}")
        private String nodes;
        @Value("${spring.redis.cluster.max-redirects}")
        private Integer maxRedirects;
        @Value("${spring.redis.password:}")
        private String password;
        @Value("${spring.redis.database:0}")
        private Integer database;

        //redis联接池相关配置
        @Value("${spring.redis.jedis.pool.max-active}")
        private Integer maxActive;
        @Value("${spring.redis.jedis.pool.max-idle}")
        private Integer maxIdle;
        @Value("${spring.redis.jedis.pool.max-wait}")
        private Long maxWait;
        @Value("${spring.redis.jedis.pool.min-idle}")
        private Integer minIdle;

        @Bean  //联接池的配置
        public JedisPoolConfig jedisPool() {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(maxIdle);
            jedisPoolConfig.setMaxWaitMillis(maxWait);
            jedisPoolConfig.setMaxTotal(maxActive);
            jedisPoolConfig.setMinIdle(minIdle);
            return jedisPoolConfig;
        }

        @Bean
        public RedisClusterConfiguration jedisConfig() {
            RedisClusterConfiguration config = new RedisClusterConfiguration();
            String[] sub = nodes.split(",");
            List<RedisNode> nodeList = new ArrayList<>(sub.length);
            String[] tmp;
            for (String s : sub) {
                tmp = s.split(":");
                // fixme 先不考虑异常配置的case
                nodeList.add(new RedisNode(tmp[0], Integer.valueOf(tmp[1])));
            }
            config.setClusterNodes(nodeList);
            config.setMaxRedirects(maxRedirects);
            config.setPassword(RedisPassword.of(password));
            return config;
        }
    }
}
