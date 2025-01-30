package tgb.cryptoexchange.cryptowithdrawal.service.wallet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tgb.cryptoexchange.enums.CryptoCurrency;

@Service
public class BitcoinWalletService extends ElectrumWalletService {

    @Value("${credentials.BITCOIN.rpcUser}")
    private String rpcUser;

    @Value("${credentials.BITCOIN.rpcPassword}")
    private String rpcPassword;

    @Value("${url.BITCOIN}")
    private String url;

    protected BitcoinWalletService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public String getUrl() {
        return url;
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
    public String getDirectory() {
        return ".electrum";
    }

    @Override
    public CryptoCurrency getCryptoCurrency() {
        return CryptoCurrency.BITCOIN;
    }
}
