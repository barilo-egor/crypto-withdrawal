package tgb.cryptoexchange.cryptowithdrawal.interfaces;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import tgb.cryptoexchange.cryptowithdrawal.po.PoolDeal;

import java.util.List;

public interface IPoolDealService {

    List<PoolDeal> findAll();

    PoolDeal save(PoolDeal poolDeal);

    Long delete(PoolDeal poolDeal);

    void deleteAll();

    String complete();

    @Scheduled(cron = "0 0/10 * * * ?")
    @Async
    void notifyDealsCount();
}
