package tgb.cryptoexchange.cryptowithdrawal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tgb.cryptoexchange.CryptoCurrency;

@RestController
@RequestMapping("/crypto_withdrawal")
public class WithdrawalController {

    @GetMapping("/balance")
    public ResponseEntity<?> balance(CryptoCurrency cryptoCurrency) {
        return null;
    }
}
