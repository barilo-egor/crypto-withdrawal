package tgb.cryptoexchange.cryptowithdrawal.vo;

import tgb.cryptoexchange.enums.CryptoCurrency;

import java.math.BigDecimal;

public record WithdrawalRequest(CryptoCurrency cryptoCurrency, String amount, String address) {}
