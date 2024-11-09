package tgb.cryptoexchange.cryptowithdrawal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.IPoolDealService;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.kafka.IPoolTopicKafkaService;
import tgb.cryptoexchange.cryptowithdrawal.po.PoolDeal;
import tgb.cryptoexchange.cryptowithdrawal.repository.PoolDealRepository;
import tgb.cryptoexchange.cryptowithdrawal.service.balance.IBalanceRetriever;
import tgb.cryptoexchange.cryptowithdrawal.service.withdrawal.IWithdrawalService;
import tgb.cryptoexchange.cryptowithdrawal.vo.PoolOperation;
import tgb.cryptoexchange.enums.CryptoCurrency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            poolTopicKafkaService.put("В пул была добавлена сделка бота " + poolDeal.getBot() + " №" + poolDeal.getPid());
            return poolDeal;
        }
    }

    @Override
    public Long delete(Long id) {
        log.debug("Удаление сделки id={}", id);
        PoolDeal poolDeal = poolDealRepository.findById(id).orElseThrow(() -> new RuntimeException("Сделка не найдена."));
        synchronized (this) {
            poolDealRepository.delete(poolDeal);
            PoolOperation poolOperation = PoolOperation.builder().operation("delete").poolDeals(List.of(poolDeal)).build();
            poolTopicKafkaService.put(poolOperation);
            poolTopicKafkaService.put("Из пула была удалена сделка бота " + poolDeal.getBot() + " №" + poolDeal.getPid());
            log.debug("Сделка bot={} pid={} успешно удалена.", poolDeal.getBot(), poolDeal.getPid());
        }
        return id;
    }

    @Override
    public void deleteAll() {
        synchronized (this) {
            log.debug("Очищение пула.");
            List<PoolDeal> poolDeals = poolDealRepository.findAll();
            PoolOperation poolOperation = PoolOperation.builder().poolDeals(poolDeals).operation("clear").build();
            poolDealRepository.deleteAll();
            poolTopicKafkaService.put(poolOperation);
            poolTopicKafkaService.put("Пул был очищен.");
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
            PoolOperation poolOperation = PoolOperation.builder()
                    .operation("complete")
                    .poolDeals(poolDeals)
                    .data(hash)
                    .build();
            poolTopicKafkaService.put(poolOperation);
            poolTopicKafkaService.put("Пул был завершен.");
            deleteAll();
            log.debug("Пул успешно завершен, сделки удалены.");
        }
        return hash;
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    @Async
    @Override
    public void notifyDealsCount() {
        List<PoolDeal> poolDeals = poolDealRepository.findAll();
        if (!poolDeals.isEmpty()) {
            poolTopicKafkaService.put("В пуле BTC на текущий момент находится " + poolDeals.size() + " сделок.");
        }
    }
}
