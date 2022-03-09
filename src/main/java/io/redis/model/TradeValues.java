package io.redis.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class TradeValues {

    private long totalRecords;
    private List<Map<String, Object>> tradeValues = new ArrayList<>();
}
