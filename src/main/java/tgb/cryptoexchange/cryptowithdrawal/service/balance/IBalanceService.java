package tgb.cryptoexchange.cryptowithdrawal.service.balance;

import tgb.cryptoexchange.CryptoCurrency;

import java.math.BigDecimal;

public interface IBalanceService {

    BigDecimal getBalance();

    CryptoCurrency getCryptoCurrency();
}
