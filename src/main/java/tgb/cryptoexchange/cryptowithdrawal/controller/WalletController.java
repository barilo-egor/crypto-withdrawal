package tgb.cryptoexchange.cryptowithdrawal.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tgb.cryptoexchange.controller.ApiController;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.IWalletService;
import tgb.cryptoexchange.enums.CryptoCurrency;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class WalletController extends ApiController {

    private final Map<CryptoCurrency, IWalletService> walletServiceMap;

    public WalletController(List<IWalletService> walletServices) {
        walletServiceMap = new HashMap<>();
        for (IWalletService walletService : walletServices) {
            walletServiceMap.put(walletService.getCryptoCurrency(), walletService);
        }
    }

    @PostMapping("/wallet/{cryptoCurrency}")
    public ResponseEntity<?> replace(@PathVariable CryptoCurrency cryptoCurrency, @RequestBody String seedPhrase) {
        log.debug("Запрос на замену кошелька для криптовалюты {}", Objects.nonNull(cryptoCurrency) ? cryptoCurrency.name() : "null");
        IWalletService walletService = walletServiceMap.get(cryptoCurrency);
        if (seedPhrase == null || seedPhrase.isBlank() || walletService == null) {
            log.error("Сид фраза пуста, либо не найден обработчик замены кошелька для данной криптовалюты.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        walletService.loadWallet(seedPhrase);
        log.debug("Кошелек успешно заменен.");
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
