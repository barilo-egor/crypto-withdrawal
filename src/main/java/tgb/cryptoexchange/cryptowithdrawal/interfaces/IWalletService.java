package tgb.cryptoexchange.cryptowithdrawal.interfaces;

import tgb.cryptoexchange.enums.CryptoCurrency;

public interface IWalletService {

    void loadWallet(String seedPhrase);

    CryptoCurrency getCryptoCurrency();



}
