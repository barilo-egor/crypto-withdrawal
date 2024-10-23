package tgb.cryptoexchange.cryptowithdrawal.service.balance;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tgb.cryptoexchange.cryptowithdrawal.vo.GetBalanceElectrumResponse;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public abstract class ElectrumBalanceRetriever implements IBalanceRetriever {

    @Value("${isDev:#{false}}")
    private boolean isDev;

    private final RestTemplate restTemplate;

    public boolean isDev() {
        return isDev;
    }

    public ElectrumBalanceRetriever(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(5000))
                .build();
    }

    public BigDecimal getBalance() {
        if (isDev()) {
            log.debug("Включен режим разработчика. Возвращается заглушка для баланса.");
            return new BigDecimal(500);
        }
        if (!isWithdrawalOn()) {
            throw new RuntimeException("Автовывод для " + getCryptoCurrency().name() + " выключен.");
        }
        return makeRequest();
    }

    private BigDecimal makeRequest() {
        GetBalanceElectrumResponse response = restTemplate.exchange(
                getUrl(), HttpMethod.POST, buildRequest(), GetBalanceElectrumResponse.class
        ).getBody();
        if (Objects.isNull(response)) {
            throw new RuntimeException("Отсутствует объект ответа.");
        }
        if (Objects.nonNull(response.getError())) {
            String message = "Ошибка получения баланса для " + getCryptoCurrency().name()
                    + ". Сообщение от electrum: " + response.getError().getMessage();
            throw new RuntimeException(message);
        }
        if (Objects.isNull(response.getResult()) || StringUtils.isBlank(response.getResult().getConfirmed())) {
            String message = "В ответе при получении баланса отсутствуют confirmed и error.";
            log.error("{}\nОтвет: {}", message, response);
            throw new RuntimeException(message);
        }
        return new BigDecimal(response.getResult().getConfirmed());
    }

    private HttpEntity<Map<String, Object>> buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(getRpcUser(), getRpcPassword());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(getParams(), headers);
        return request;
    }

    private Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("method", "getbalance");
        params.put("params", new Object[]{});
        params.put("id", 1);
        params.put("jsonrpc", "2.0");
        return params;
    }

    public abstract boolean isWithdrawalOn();

    public abstract String getRpcUser();

    public abstract String getRpcPassword();

    public abstract String getUrl();
}
