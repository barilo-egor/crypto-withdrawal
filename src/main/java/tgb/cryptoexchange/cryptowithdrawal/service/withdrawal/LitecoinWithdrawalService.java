package tgb.cryptoexchange.cryptowithdrawal.service.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tgb.cryptoexchange.enums.CryptoCurrency;

@Service
public class LitecoinWithdrawalService extends ElectrumWithdrawalService {

    @Value("${url.LITECOIN}")
    private String url;

    @Value("${credentials.LITECOIN.rpcUser}")
    private String rpcUser;

    @Value("${credentials.LITECOIN.rpcPassword}")
    private String rpcPassword;

    public LitecoinWithdrawalService(RestTemplate restTemplate) {
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
        return "0.001";
    }

    @Override
    public CryptoCurrency getCryptoCurrency() {
        return CryptoCurrency.LITECOIN;
    }

}
