package tgb.cryptoexchange.cryptowithdrawal.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.kafka.IPoolTopicKafkaService;
import tgb.cryptoexchange.cryptowithdrawal.vo.PoolCompleteResult;

import java.util.List;

@Service
public class PoolTopicKafkaService implements IPoolTopicKafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public PoolTopicKafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void complete(List<PoolCompleteResult> completeResults) {
        String object;
        try {
            object = objectMapper.writeValueAsString(completeResults);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send("pool", "complete", object);
    }

    @Override
    public void poolUpdated(String message) {
        kafkaTemplate.send("pool", "update", message);
    }
}
