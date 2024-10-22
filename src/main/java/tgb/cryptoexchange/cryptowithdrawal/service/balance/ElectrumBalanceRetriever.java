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
public abstract class ElectrumBalanceRetriever implements IBalanceRetriever {

    @Value("${isDev:#{false}}")
    private boolean isDev;

    public BigDecimal getBalance() {
        if (isDev) {
            log.debug("Включен режим разработчика. Возвращается заглушка для баланса.");
            return new BigDecimal(500);
        }
        if (!isWithdrawalOn()) {
            throw new RuntimeException("Автовывод для " + getCryptoCurrency().name() + " выключен.");
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
        headers.setBasicAuth(getRpcUser(), getRpcPassword());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

        ResponseEntity<GetBalanceElectrumResponse> responseEntity =
                restTemplate.exchange(getUrl(), HttpMethod.POST, request, GetBalanceElectrumResponse.class);
        GetBalanceElectrumResponse response = responseEntity.getBody();
        if (Objects.isNull(response)) {
            throw new RuntimeException("Отсутствует объект ответа.");
        }
        if (Objects.nonNull(response.getResult()) && StringUtils.isNotBlank(response.getResult().getConfirmed())) {
            return new BigDecimal(response.getResult().getConfirmed());
        } else if (Objects.nonNull(response.getError())) {
            String message = "Ошибка получения баланса для " + CryptoCurrency.BITCOIN.name()
                    + ". Сообщение от electrum: " + response.getError().getMessage();
            throw new RuntimeException(message);
        } else {
            String message = "В ответе при получении баланса отсутствуют confirmed и error.";
            log.error("{}\nОтвет: {}", message, response);
            throw new RuntimeException(message);
        }
    }

    public abstract boolean isWithdrawalOn();

    public abstract String getRpcUser();

    public abstract String getRpcPassword();

    public abstract String getUrl();
}
