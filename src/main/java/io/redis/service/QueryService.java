package io.redis.service;

import com.google.common.collect.ImmutableMap;
import io.redis.model.TradeValues;
import io.redis.utils.SearchUtil;
import io.redisearch.Query;
import io.redisearch.SearchResult;
import io.redisearch.client.Client;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;

@Data
@Slf4j
@Service
public class QueryService {

    @Autowired
    SearchUtil searchUtil;

    @Autowired
    Jedis jedis;

    @Autowired
    Client redisSearch;

    // TODO - Query Builder Factory
    private static final String QUERY_CP = "@counterparty:";
    private static final String QUERY_SITE = "@site:";
    private static final String QUERY_DATE = "@date:";


    public Set<String> restaurantKeys = new HashSet<>();
    private static String KEY_PREFIX = "restaurant";

    public TradeValues getRecordsByCounterParty(String counterparty, int limit, int offset) {
        String queryString = searchUtil.escapeMetaCharacters(QUERY_CP + counterparty);

        Query query = new Query(queryString);
        query.limit(limit, offset);
        log.info("Query String: {}", queryString);

        long startTime = System.nanoTime();
        SearchResult results = redisSearch.search(query);

        long duration = (System.nanoTime() - startTime) / 1000000;
        log.info("Query String: {} :: took {}ms", queryString, duration);
        TradeValues tv = new TradeValues();
        tv.setTotalRecords(results.totalResults);
        results.docs.stream().forEach(d -> {
            tv.getTradeValues().add(ImmutableMap.copyOf(d.getProperties()));
        });

        return tv;
    }

}
