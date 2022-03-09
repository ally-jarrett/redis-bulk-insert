package io.redis.api;

import io.redis.service.QueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/query")
public class QueryController {

    @Autowired
    QueryService queryService;

    @GetMapping(value = "/counterparty", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getByCounterParty(@RequestParam("query") String query,
                                            @RequestParam("offset") int offset,
                                            @RequestParam("limit") int limit) {

        if (StringUtils.isEmpty(query)) {
            return ResponseEntity.badRequest().body("Query Param required");
        }

        log.info("Getting Records by Counterparty :: `{}` ", query);
        return ResponseEntity.ok(queryService.getRecordsByCounterParty(query, offset, limit));
    }

}
