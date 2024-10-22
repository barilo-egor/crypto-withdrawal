package tgb.cryptoexchange.cryptowithdrawal.service.balance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.enums.CryptoCurrency;

@Service
@Slf4j
public class BitcoinBalanceRetriever extends ElectrumBalanceRetriever {

    @Value("${turn.BITCOIN:#{false}}")
    private boolean isBitcoinWithdrawalOn;

    @Value("${credentials.BITCOIN.rpcUser}")
    private String rpcUser;

    @Value("${credentials.BITCOIN.rpcPassword}")
    private String rpcPassword;

    @Value("${url.BITCOIN}")
    private String url;

    @Override
    public CryptoCurrency getCryptoCurrency() {
        return CryptoCurrency.BITCOIN;
    }

    @Override
    public boolean isWithdrawalOn() {
        return isBitcoinWithdrawalOn;
    }

    @Override
    public String getRpcUser() {
        return rpcUser;
    }

    @Override
    public String getRpcPassword() {
        return rpcPassword;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
