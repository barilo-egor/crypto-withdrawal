package tgb.cryptoexchange.cryptowithdrawal.service.withdrawal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import tgb.cryptoexchange.cryptowithdrawal.vo.BroadcastElectrumRequest;
import tgb.cryptoexchange.cryptowithdrawal.vo.PayToElectrumRequest;
import tgb.cryptoexchange.cryptowithdrawal.vo.SingleTransactionElectrumResponse;
import tgb.cryptoexchange.enums.CryptoCurrency;

import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class ElectrumWithdrawalService implements IWithdrawalService {

    @Value("${isDev:#{false}")
    private boolean isDev;

    private int id = 1;

    private final RestTemplate restTemplate;

    private final HttpHeaders headers;

    public ElectrumWithdrawalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        headers = new HttpHeaders();
        String authHeader = "Basic " + java.util.Base64.getEncoder().encodeToString(getAuth().getBytes());
        headers.set("Authorization", authHeader);
        headers.set("Content-type", "application/json");
    }

    @Override
    public void withdrawal(String address, String amount) {
        if (isDev) {
            amount = getDevMinSum();
        }
        log.debug("Запрос на автовывод amount={}, address={}", amount, address);
        log.debug("Создание транзакции.");
        String signedTransaction = createTransaction(address, amount);
        log.debug("Отправка транзакции {} в сеть.", signedTransaction);
        broadcast(signedTransaction);
    }

    private void broadcast(String signedTransaction) {
        BroadcastElectrumRequest broadcastRequest = BroadcastElectrumRequest.builder()
                .id(String.valueOf(id++))
                .params(List.of(signedTransaction))
                .build();
        HttpEntity<BroadcastElectrumRequest> broadcastEntity = new HttpEntity<>(broadcastRequest, headers);
        SingleTransactionElectrumResponse response = restTemplate.exchange(
                getUrl(), HttpMethod.POST, broadcastEntity, SingleTransactionElectrumResponse.class
        ).getBody();
        if (Objects.isNull(response)) {
            throw new RuntimeException("Ответ отправки транзакции в сеть пуст.");
        }
        if (Objects.nonNull(response.getError())) {
            log.error("Ошибка при создании транзакции. Ответ: {}", response);
            throw new RuntimeException("Ошибка при создании транзакции. Код: " + response.getError().getCode()
                    + ", сообщение: " + response.getError().getMessage());
        }
        log.info("Транзакция отправлена. Ответ: {}", response);
    }

    private String createTransaction(String address, String amount) {
        HttpEntity<PayToElectrumRequest> entity = new HttpEntity<>(
                PayToElectrumRequest.builder()
                        .id(String.valueOf(id++))
                        .params(List.of(address, amount))
                        .build(),
                headers
        );
        SingleTransactionElectrumResponse response = restTemplate.exchange(
                getUrl(), HttpMethod.POST, entity, SingleTransactionElectrumResponse.class
        ).getBody();
        if (Objects.isNull(response)) {
            throw new RuntimeException("Ответ создания транзакции пуст.");
        }
        if (Objects.nonNull(response.getError())) {
            log.error("Ошибка при создании транзакции. Ответ: {}", response);
            throw new RuntimeException("Ошибка при создании транзакции. Код: " + response.getError().getCode()
                    + ", сообщение: " + response.getError().getMessage());
        }
        if (Objects.isNull(response.getResult())) {
            String message = "Отсутствует result при создании транзакции.";
            log.error("{}\nОтвет: {}", message, response);
            throw new RuntimeException(message);
        }
        String signedTransaction = response.getResult();
        log.debug("Транзакция создана: {}", signedTransaction);
        return signedTransaction;
    }

    public abstract String getUrl();

    public abstract String getAuth();

    public abstract String getDevMinSum();

    public abstract CryptoCurrency getCryptoCurrency();
}
