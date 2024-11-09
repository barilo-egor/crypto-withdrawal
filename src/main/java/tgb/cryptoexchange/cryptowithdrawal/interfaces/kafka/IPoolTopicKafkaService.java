package tgb.cryptoexchange.cryptowithdrawal.interfaces.kafka;


import tgb.cryptoexchange.cryptowithdrawal.vo.PoolCompleteResult;

import java.util.List;

public interface IPoolTopicKafkaService {

    void complete(List<PoolCompleteResult> completeResults);

    void poolUpdated(String message);
}
