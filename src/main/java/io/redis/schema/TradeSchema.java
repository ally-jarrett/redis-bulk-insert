package io.redis.schema;

import io.redisearch.Schema;
import io.redisearch.client.IndexDefinition;
import lombok.Getter;

@Getter
public class TradeSchema {

    public final static String TRADES_INDEX = "trades-idx";

    private Schema tradeSchema;

    public IndexDefinition indexDefinition = new IndexDefinition(IndexDefinition.Type.HASH)
            .setPrefixes(new String[]{"EOD_TRADES:"});

    public TradeSchema() {
        tradeSchema = new Schema();
        tradeSchema.addSortableTextField("valueDate", 1.0);
        tradeSchema.addSortableTextField("site", 1.0);
        tradeSchema.addSortableTextField("counterparty", 1.0);
        tradeSchema.addSortableTextField("tradeRef", 1.0);
        tradeSchema.addTextField("structureId", 1.0);
        tradeSchema.addTextField("tradeType", 1.0);
        tradeSchema.addTextField("tradeStatus", 1.0);
    }
}
