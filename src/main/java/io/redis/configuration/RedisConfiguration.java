package io.redis.configuration;

import io.redis.schema.TradeSchema;
import io.redisearch.client.Client;
import io.redisearch.client.Commands;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisDataException;

import static io.redis.schema.TradeSchema.TRADES_INDEX;

@Slf4j
@Configuration
@Data
@ComponentScan
public class RedisConfiguration {

    @Autowired
    private RedisProperties props;

    @Bean(destroyMethod = "close")
    public JedisPool setupJedisPool() {
        return new JedisPool(new JedisPoolConfig(),
                props.getHost(),
                props.getPort(),
                Protocol.DEFAULT_TIMEOUT,
                props.getPassword());
    }

    @Bean(destroyMethod = "close")
    public Jedis setupJedis(JedisPool jedisPool) {
        return jedisPool.getResource();
    }

    @Bean(destroyMethod = "close")
    public Client redisSearch(JedisPool jedisPool, TradeSchema ts) {
        Client client = new Client(TRADES_INDEX, jedisPool);

        // Probably over-engineered but nvm
        boolean createIndex = false;
        try {
            // Attempt to get Index details, will throw exception if it doesnt exist
            Object response = jedisPool.getResource().sendCommand(Commands.Command.INFO, TRADES_INDEX);
        } catch (JedisDataException e) {
            if (e.getMessage().contains("Unknown Index name")) {
                createIndex = true;
            }
        }

        if (createIndex) {
            log.info("Creating {} Index in Redis", TRADES_INDEX);
            try {
                // Create index
                client.createIndex(ts.getTradeSchema(),
                        Client.IndexOptions.defaultOptions().setDefinition(ts.getIndexDefinition()));
            } catch (JedisDataException jde) {
                jde.printStackTrace();
            }
        }
        return client;
    }

    @Bean
    @Scope("singleton")
    public TradeSchema tradeSchema() {
        return new TradeSchema();
    }

}
