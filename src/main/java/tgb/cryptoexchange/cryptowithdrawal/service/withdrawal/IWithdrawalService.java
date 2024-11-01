package tgb.cryptoexchange.cryptowithdrawal.service.withdrawal;

import tgb.cryptoexchange.enums.CryptoCurrency;

public interface IWithdrawalService {

    String withdrawal(String address, String amount);

    CryptoCurrency getCryptoCurrency();

}
