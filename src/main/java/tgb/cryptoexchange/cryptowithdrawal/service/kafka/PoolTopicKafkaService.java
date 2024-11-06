package tgb.cryptoexchange.cryptowithdrawal.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.kafka.IPoolTopicKafkaService;

@Service
public class PoolTopicKafkaService implements IPoolTopicKafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public PoolTopicKafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendUpdate() {
        kafkaTemplate.send("pool", "pool", "update");
    }
}
