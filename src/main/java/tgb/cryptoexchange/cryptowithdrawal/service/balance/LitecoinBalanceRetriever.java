package tgb.cryptoexchange.cryptowithdrawal.service.balance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.enums.CryptoCurrency;

@Service
public class LitecoinBalanceRetriever extends ElectrumBalanceRetriever {

    @Value("${turn.LITECOIN:#{false}}")
    private boolean isLitecoinWithdrawalOn;

    @Value("${credentials.BITCOIN.rpcUser}")
    private String rpcUser;

    @Value("${credentials.BITCOIN.rpcPassword}")
    private String rpcPassword;

    @Value("${url.BITCOIN}")
    private String url;

    public LitecoinBalanceRetriever(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    public boolean isWithdrawalOn() {
        return isLitecoinWithdrawalOn;
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

    @Override
    public CryptoCurrency getCryptoCurrency() {
        return CryptoCurrency.LITECOIN;
    }
}
