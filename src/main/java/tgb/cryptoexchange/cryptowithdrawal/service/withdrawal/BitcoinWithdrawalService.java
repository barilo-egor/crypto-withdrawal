package tgb.cryptoexchange.cryptowithdrawal.service.withdrawal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tgb.cryptoexchange.enums.CryptoCurrency;

@Service
public class BitcoinWithdrawalService extends ElectrumWithdrawalService implements IWithdrawalService {

    @Value("${turn.BITCOIN:#{false}}")
    private boolean isBitcoinWithdrawalOn;

    @Value("${url.BITCOIN}")
    private String url;

    @Value("${credentials.BITCOIN.rpcUser}")
    private String rpcUser;

    @Value("${credentials.BITCOIN.rpcPassword}")
    private String rpcPassword;

    @Value("${minSum.BITCOIN}")
    private String minSum;

    public BitcoinWithdrawalService(RestTemplate restTemplate) {
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
    public String getDevMinSum() {
        return minSum;
    }

    @Override
    public CryptoCurrency getCryptoCurrency() {
        return CryptoCurrency.BITCOIN;
    }

    @Override
    public boolean isOn() {
        return isBitcoinWithdrawalOn;
    }

}
