package tgb.cryptoexchange.cryptowithdrawal.service.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tgb.cryptoexchange.enums.CryptoCurrency;

@Service
public class BitcoinWithdrawalService extends ElectrumWithdrawalService implements IWithdrawalService {

    @Value("${url.BITCOIN}")
    private String url;

    @Value("${credentials.BITCOIN.rpcUser}")
    private String username;

    @Value("${credentials.BITCOIN.rpcPassword}")
    private String password;

    public BitcoinWithdrawalService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getAuth() {
        return username + ":" + password;
    }

    @Override
    public String getDevMinSum() {
        return "0.00000546";
    }

    @Override
    public CryptoCurrency getCryptoCurrency() {
        return CryptoCurrency.BITCOIN;
    }

}
