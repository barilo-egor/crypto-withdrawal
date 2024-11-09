package tgb.cryptoexchange.cryptowithdrawal.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.IPoolDealService;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.kafka.IPoolTopicKafkaService;
import tgb.cryptoexchange.cryptowithdrawal.po.PoolDeal;
import tgb.cryptoexchange.cryptowithdrawal.vo.PoolOperation;

import java.util.List;

@Service
public class PoolTopicKafkaService implements IPoolTopicKafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final IPoolDealService poolDealService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public PoolTopicKafkaService(KafkaTemplate<String, String> kafkaTemplate, IPoolDealService poolDealService) {
        this.kafkaTemplate = kafkaTemplate;
        this.poolDealService = poolDealService;
    }

    @Override
    public <T> void put(PoolOperation poolOperation) {
        String object;
        try {
            object = objectMapper.writeValueAsString(poolOperation);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send("pool", "operation", object);
    }

    @Override
    public void put(String message) {
        kafkaTemplate.send("pool", "message", message);
    }


    @Scheduled(cron = "0 0/10 * * * ?")
    @Async
    @Override
    public void notifyDealsCount() {
        List<PoolDeal> poolDeals = poolDealService.findAll();
        if (!poolDeals.isEmpty()) {
            kafkaTemplate.send("pool", "message", "В пуле BTC на текущий момент находится "
                    + poolDeals.size() + " сделок.");
        }
    }
}
