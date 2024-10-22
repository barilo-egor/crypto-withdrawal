package tgb.cryptoexchange.cryptowithdrawal.service.balance;


import tgb.cryptoexchange.enums.CryptoCurrency;

import java.math.BigDecimal;

public interface IBalanceRetriever {

    BigDecimal getBalance();

    CryptoCurrency getCryptoCurrency();
}
