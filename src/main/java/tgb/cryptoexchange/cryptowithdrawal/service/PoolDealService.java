package tgb.cryptoexchange.cryptowithdrawal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.IPoolDealService;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.kafka.IPoolTopicKafkaService;
import tgb.cryptoexchange.cryptowithdrawal.po.PoolDeal;
import tgb.cryptoexchange.cryptowithdrawal.repository.PoolDealRepository;
import tgb.cryptoexchange.cryptowithdrawal.service.balance.IBalanceRetriever;
import tgb.cryptoexchange.cryptowithdrawal.service.withdrawal.IWithdrawalService;
import tgb.cryptoexchange.cryptowithdrawal.vo.PoolComplete;
import tgb.cryptoexchange.cryptowithdrawal.vo.PoolCompleteResult;
import tgb.cryptoexchange.enums.CryptoCurrency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class PoolDealService implements IPoolDealService {

    private final PoolDealRepository poolDealRepository;

    private final Map<CryptoCurrency, IBalanceRetriever> balanceRetrieversMap;

    private final Map<CryptoCurrency, IWithdrawalService> withdrawalServiceMap;

    private final IPoolTopicKafkaService poolTopicKafkaService;

    public PoolDealService(PoolDealRepository poolDealRepository,
                           List<IBalanceRetriever> balanceServices,
                           List<IWithdrawalService> withdrawalServices, IPoolTopicKafkaService poolTopicKafkaService) {
        this.poolDealRepository = poolDealRepository;
        this.poolTopicKafkaService = poolTopicKafkaService;
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
            log.debug("Сохранение сделки в пул: {}", poolDeal);
            poolDeal = poolDealRepository.save(poolDeal);
            poolTopicKafkaService.poolUpdated("В пул была добавлена сделка бота " + poolDeal.getBot() + " №" + poolDeal.getPid());
            return poolDeal;
        }
    }

    @Override
    public Long delete(Long id) {
        log.debug("Удаление сделки id={}", id);
        PoolDeal poolDeal = poolDealRepository.findById(id).orElseThrow(() -> new RuntimeException("Сделка не найдена."));
        synchronized (this) {
            poolDealRepository.delete(poolDeal);
            poolTopicKafkaService.poolUpdated("Из пула была удалена сделка бота " + poolDeal.getBot() + " №" + poolDeal.getPid());
            log.debug("Сделка bot={} pid={} успешно удалена.", poolDeal.getBot(), poolDeal.getPid());
        }
        return id;
    }

    @Override
    public void deleteAll() {
        synchronized (this) {
            log.debug("Очищение пула.");
            poolDealRepository.deleteAll();
            poolTopicKafkaService.poolUpdated("Пул был очищен.");
            log.debug("Пул успешно очищен.");
        }
    }

    @Override
    public String complete() {
        log.debug("Завершение пула.");
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
            Map<String, List<PoolDeal>> sortedDeals = poolDeals.stream()
                    .collect(Collectors.groupingBy(PoolDeal::getBot, TreeMap::new, Collectors.toList()));
            PoolComplete poolComplete = PoolComplete.builder()
                    .hash(hash)
                    .results(sortedDeals.entrySet().stream()
                            .map(entry -> PoolCompleteResult.builder()
                                    .bot(entry.getKey())
                                    .pids(entry.getValue().stream().map(PoolDeal::getPid).toList())
                                    .build())
                            .toList())
                    .build();
            poolTopicKafkaService.complete(poolComplete);
            poolTopicKafkaService.poolUpdated("Пул был завершен.");
            deleteAll();
            log.debug("Пул успешно завершен, сделки удалены.");
        }
        return hash;
    }

}
