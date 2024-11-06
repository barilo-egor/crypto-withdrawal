package tgb.cryptoexchange.cryptowithdrawal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.IPoolDealService;
import tgb.cryptoexchange.cryptowithdrawal.po.PoolDeal;
import tgb.cryptoexchange.cryptowithdrawal.repository.PoolDealRepository;
import tgb.cryptoexchange.cryptowithdrawal.service.balance.IBalanceRetriever;
import tgb.cryptoexchange.cryptowithdrawal.service.withdrawal.IWithdrawalService;
import tgb.cryptoexchange.enums.CryptoCurrency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class PoolDealService implements IPoolDealService {

    private final Map<String, SseEmitter> emitters = new HashMap<>();

    private final PoolDealRepository poolDealRepository;

    private final Map<CryptoCurrency, IBalanceRetriever> balanceRetrieversMap;

    private final Map<CryptoCurrency, IWithdrawalService> withdrawalServiceMap;

    public PoolDealService(PoolDealRepository poolDealRepository,
            List<IBalanceRetriever> balanceServices,
            List<IWithdrawalService> withdrawalServices) {
        this.poolDealRepository = poolDealRepository;
        balanceRetrieversMap = new HashMap<>();
        for (IBalanceRetriever balanceService : balanceServices) {
            balanceRetrieversMap.put(balanceService.getCryptoCurrency(), balanceService);
        }
        withdrawalServiceMap = new HashMap<>();
        for (IWithdrawalService withdrawalService : withdrawalServices) {
            withdrawalServiceMap.put(withdrawalService.getCryptoCurrency(), withdrawalService);
        }
    }

    @Override
    public List<PoolDeal> findAll() {
        return poolDealRepository.findAll();
    }

    @Override
    public PoolDeal save(PoolDeal poolDeal) {
        synchronized (this) {
            return poolDealRepository.save(poolDeal);
        }
    }

    @Override
    public Long delete(String bot, Long pid) {
        PoolDeal poolDeal = poolDealRepository.findBy(
                        Example.of(PoolDeal.builder().bot(bot).pid(pid).build()),
                        FluentQuery.FetchableFluentQuery::all)
                .stream().findFirst().orElseThrow(() -> new RuntimeException("Сделка не найдена."));
        Long id = poolDeal.getId();
        synchronized (this) {
            poolDealRepository.delete(poolDeal);
        }
        return id;
    }

    @Override
    public void deleteAll() {
        synchronized (this) {
            poolDealRepository.deleteAll();
        }
    }

    @Override
    public String complete() {
        List<PoolDeal> poolDeals = poolDealRepository.findAll();
        BigDecimal sum = poolDeals.stream()
                .map(PoolDeal::getAmount)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        IBalanceRetriever balanceRetriever = balanceRetrieversMap.get(CryptoCurrency.BITCOIN);
        BigDecimal balance = balanceRetriever.getBalance();
        List<Pair<String, String>> pairs = poolDeals.stream()
                .map(poolDeal -> Pair.of(poolDeal.getAddress(), poolDeal.getAmount()))
                .toList();
        if (balance.compareTo(sum) < 0) {
            log.debug("На балансе недостаточно средств для вывода запрошенных сделок: {}", pairs);
            throw new RuntimeException("На балансе недостаточно средств для вывода всех сделок.");
        }
        IWithdrawalService withdrawalService = withdrawalServiceMap.get(CryptoCurrency.BITCOIN);
        String hash;
        synchronized (this) {
            hash = withdrawalService.withdrawal(pairs);
            deleteAll();
        }
        return hash;
    }

}
