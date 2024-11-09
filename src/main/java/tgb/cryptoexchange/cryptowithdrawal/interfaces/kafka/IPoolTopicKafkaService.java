package tgb.cryptoexchange.cryptowithdrawal.interfaces.kafka;


import tgb.cryptoexchange.cryptowithdrawal.vo.PoolComplete;

public interface IPoolTopicKafkaService {

    void complete(PoolComplete poolComplete);

    void poolUpdated(String message);
}
