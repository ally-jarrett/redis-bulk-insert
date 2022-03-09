package io.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class DataLoader implements CommandLineRunner {

    @Value("${spring.redis.data.insert-on-startup:false}")
    private boolean load;

    @Autowired
    Jedis jedis;

    private static List<String> counterPartiesCommon = List.of("WORL", "BBIB", "AH24", "NASDQ");
    private static List<String> counterParties = List.of("LCHSA", "EUCC", "KDPW", "EUREX", "BMEC", "OMIC", "AECH",
            "SKDD", "GB10", "CCP1", "FR55", "EU99", "DE00", "UA66", "IT22");

    @Override
    public void run(String... args) {

        if (load) {

            int recordCounter = 0;
            int iterations = 10000;


            while (iterations > 0) {

                int hashPerPipeline = 100;
                Pipeline p = jedis.pipelined();

                // Add 1-- hashes per pipeline
                while (hashPerPipeline > 0) {

                    Map<String, String> hash;
                    if ((recordCounter % 2) == 0) {
                        hash = this.randomTradeRecord(counterPartiesCommon);
                    } else {
                        hash = this.randomTradeRecord(counterParties);
                    }

                    p.hmset("EOD_TRADES:" + hash.get("tradeRef"), hash);
                    hashPerPipeline--;
                    recordCounter++;
                }
                log.info("Adding: {} records to redis", recordCounter);
                p.sync();
                iterations--;
            }
            log.info("Function finished, {} keys added, total keys in Redis :: {}", recordCounter, jedis.dbSize());
        } else {
            log.info("Skipping data load");
        }
    }

    /**
     * Assume basic structure: 4 ints 1 char 11 ints
     *
     * @return
     */
    public String randomTradeRef() {
        return RandomStringUtils.randomNumeric(4) +
                RandomStringUtils.randomAlphabetic(1).toUpperCase() +
                RandomStringUtils.randomNumeric(11);
    }

    public Map<String, String> randomTradeRecord(List<String> counterParties) {

        int randomElementIndex = ThreadLocalRandom.current().nextInt(counterParties.size());

        return Map.of(
                "structureId", "",
                "date", "2022-01-07",
                "tradeStatus", "DM000",
                "quicFormat", "[\"LOH_0011Z00029429631_FXPB LOH,FXForward,LOH_AH24,LOH_AH24-ISDA-1-8937-CSA-1020048,,2021/12/20,100216.6,,,,MD,EEPE,Y,LOH,,N,,N,,,N,Y,2022/01/27,100216.6,IR_USD_NT.Yield.USD,FX_USD.Exchange.USD,7500000.15,IR_RUB_NT.Yield.RUB,FX_RUB.Exchange.USD,,NONE,,,,,,,,,,,,,,,,\"]",
                "tradeRef", this.randomTradeRef(),
                "marketDataCurves", "[\"IR_USD_NT:Yield\", \"FX_USD:Exchange\", \"FX_RUB:Exchange\", \"IR_RUB_NT:Yield\"]",
                "tradeType", "NOND_FWD_FX",
                "counterparty", counterParties.get(randomElementIndex),
                "site", "LOH");
    }

}