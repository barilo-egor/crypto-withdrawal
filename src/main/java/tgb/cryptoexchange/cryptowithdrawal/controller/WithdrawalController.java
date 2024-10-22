package tgb.cryptoexchange.cryptowithdrawal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tgb.cryptoexchange.cryptowithdrawal.service.balance.IBalanceService;
import tgb.cryptoexchange.enums.CryptoCurrency;
import tgb.cryptoexchange.web.ApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WithdrawalController {

    private final Map<CryptoCurrency, IBalanceService> balanceServiceMap;

    public WithdrawalController(List<IBalanceService> balanceServices) {
        balanceServiceMap = new HashMap<>();
        for (IBalanceService balanceService : balanceServices) {
            balanceServiceMap.put(balanceService.getCryptoCurrency(), balanceService);
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse> balance(@RequestParam CryptoCurrency cryptoCurrency) {
        return new ResponseEntity<>(ApiResponse.success(balanceServiceMap.get(cryptoCurrency).getBalance()), HttpStatus.OK);
    }
}
