package tgb.cryptoexchange.cryptowithdrawal.service.withdrawal;

import tgb.cryptoexchange.enums.CryptoCurrency;

public interface IWithdrawalService {

    void withdrawal(String address, String amount);

    CryptoCurrency getCryptoCurrency();

}
