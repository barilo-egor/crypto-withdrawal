package tgb.cryptoexchange.cryptowithdrawal.service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "pool", groupId = "rce")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("Получено сообщение: " + record.value() + " с offset: " + record.offset());
    }
}
