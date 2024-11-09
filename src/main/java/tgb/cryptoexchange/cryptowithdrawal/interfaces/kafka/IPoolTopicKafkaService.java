package tgb.cryptoexchange.cryptowithdrawal.interfaces.kafka;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import tgb.cryptoexchange.cryptowithdrawal.vo.PoolOperation;

public interface IPoolTopicKafkaService {

    <T> void put(PoolOperation poolOperation);

    void put(String message);

    @Scheduled(cron = "0 0/10 * * * ?")
    @Async
    void notifyDealsCount();
}
