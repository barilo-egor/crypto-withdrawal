package tgb.cryptoexchange.cryptowithdrawal.interfaces;

import tgb.cryptoexchange.cryptowithdrawal.po.PoolDeal;

import java.util.List;

public interface IPoolDealService {

    List<PoolDeal> findAll();

    PoolDeal save(PoolDeal poolDeal);

    Long delete(String bot, Long pid);

    String complete();

}
