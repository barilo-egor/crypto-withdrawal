package tgb.cryptoexchange.cryptowithdrawal.service.balance;


import tgb.cryptoexchange.enums.CryptoCurrency;

import java.math.BigDecimal;

public interface IBalanceService {

    BigDecimal getBalance();

    CryptoCurrency getCryptoCurrency();
}
