package tgb.cryptoexchange.cryptowithdrawal.service.balance;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tgb.cryptoexchange.cryptowithdrawal.vo.GetBalanceElectrumResponse;
import tgb.cryptoexchange.enums.CryptoCurrency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class BitcoinBalanceService implements IBalanceService {

    @Value("${isDev:#{false}}")
    private boolean isDev;

    @Value("${turn.BITCOIN:#{false}}")
    private boolean isBitcoinWithdrawalOn;

    @Value("${credentials.BITCOIN.rpcUser}")
    private String rpcUser;

    @Value("${credentials.BITCOIN.rpcPassword}")
    private String rpcPassword;

    @Value("${url.BITCOIN}")
    private String url;

    @Override
    public BigDecimal getBalance() {
        if (isDev) {
            log.debug("Включен режим разработчика. Возвращается заглушка для баланса.");
            return new BigDecimal(500);
        }
        if (!isBitcoinWithdrawalOn) {
            throw new RuntimeException("Автовывод для " + CryptoCurrency.BITCOIN.name() + " выключен.");
        }
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(factory);
        String method = "getbalance";

        Map<String, Object> params = new HashMap<>();
        params.put("method", method);
        params.put("params", new Object[]{});
        params.put("id", 1);
        params.put("jsonrpc", "2.0");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(rpcUser, rpcPassword);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

        ResponseEntity<GetBalanceElectrumResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request, GetBalanceElectrumResponse.class);
        GetBalanceElectrumResponse response = responseEntity.getBody();
        if (Objects.isNull(response)) {
            throw new RuntimeException("Отсутствует объект ответа.");
        }
        if (Objects.nonNull(response.getResult()) && StringUtils.isNotBlank(response.getResult().getConfirmed())) {
            return new BigDecimal(response.getResult().getConfirmed());
        } else if (Objects.nonNull(response.getError())) {
            String message = "Ошибка получения баланса для " + CryptoCurrency.BITCOIN.name() + ". Сообщение от electrum: " + response.getError().getMessage();
            throw new RuntimeException(message);
        } else {
            String message = "В ответе при получении баланса отсутствуют confirmed и error.";
            log.error("{}\nОтвет: {}", message, response);
            throw new RuntimeException(message);
        }
    }

    @Override
    public CryptoCurrency getCryptoCurrency() {
        return CryptoCurrency.BITCOIN;
    }
}
