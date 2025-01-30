package tgb.cryptoexchange.cryptowithdrawal.service.wallet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tgb.cryptoexchange.enums.CryptoCurrency;

@Service
public class LitecoinWalletService extends ElectrumWalletService {

    @Value("${credentials.LITECOIN.rpcUser}")
    private String rpcUser;

    @Value("${credentials.LITECOIN.rpcPassword}")
    private String rpcPassword;

    @Value("${url.LITECOIN}")
    private String url;

    protected LitecoinWalletService(RestTemplate restTemplate) {
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
        return ".electrum-ltc";
    }

    @Override
    public CryptoCurrency getCryptoCurrency() {
        return CryptoCurrency.LITECOIN;
    }
}
