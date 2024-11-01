package tgb.cryptoexchange.cryptowithdrawal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgb.cryptoexchange.controller.ApiController;
import tgb.cryptoexchange.cryptowithdrawal.service.balance.IBalanceRetriever;
import tgb.cryptoexchange.cryptowithdrawal.service.kafka.PoolTopicKafkaService;
import tgb.cryptoexchange.cryptowithdrawal.service.withdrawal.IWithdrawalService;
import tgb.cryptoexchange.cryptowithdrawal.vo.WithdrawalRequest;
import tgb.cryptoexchange.enums.CryptoCurrency;
import tgb.cryptoexchange.web.ApiResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WithdrawalController extends ApiController {

    private final Map<CryptoCurrency, IBalanceRetriever> balanceRetrieversMap;

    private final Map<CryptoCurrency, IWithdrawalService> withdrawalServiceMap;

    public WithdrawalController(List<IBalanceRetriever> balanceServices, List<IWithdrawalService> withdrawalServices) {
        balanceRetrieversMap = new HashMap<>();
        for (IBalanceRetriever balanceService : balanceServices) {
            balanceRetrieversMap.put(balanceService.getCryptoCurrency(), balanceService);
        }
        withdrawalServiceMap = new HashMap<>();
        for (IWithdrawalService withdrawalService : withdrawalServices) {
            withdrawalServiceMap.put(withdrawalService.getCryptoCurrency(), withdrawalService);
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> balance(@RequestParam CryptoCurrency cryptoCurrency) {
        IBalanceRetriever balanceRetriever = balanceRetrieversMap.get(cryptoCurrency);
        if (balanceRetriever == null) {
            return new ResponseEntity<>(ApiResponse.error(
                    ApiResponse.Error.builder().message("Автовывод для данной криптовалюты не предусмотрен.").build()),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ApiResponse.success(balanceRetrieversMap.get(cryptoCurrency).getBalance()),
                HttpStatus.OK);
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<ApiResponse<String>> withdrawal(@RequestBody WithdrawalRequest withdrawalRequest) {
        String transactionHash = withdrawalServiceMap.get(withdrawalRequest.cryptoCurrency())
                .withdrawal(withdrawalRequest.address(), withdrawalRequest.amount());
        return new ResponseEntity<>(ApiResponse.success(transactionHash), HttpStatus.OK);
    }

    @Autowired
    private PoolTopicKafkaService poolTopicKafkaService;

    @GetMapping("/send")
    public void send() {
        poolTopicKafkaService.sendMessage("pool-complete", "test " + System.currentTimeMillis());
    }

}
