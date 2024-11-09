package tgb.cryptoexchange.cryptowithdrawal.interfaces.kafka;

import tgb.cryptoexchange.cryptowithdrawal.vo.PoolOperation;

public interface IPoolTopicKafkaService {

    <T> void put(PoolOperation poolOperation);

    void put(String message);
}
