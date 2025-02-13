package tgb.cryptoexchange.cryptowithdrawal.service.withdrawal;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import tgb.cryptoexchange.cryptowithdrawal.vo.*;
import tgb.cryptoexchange.enums.CryptoCurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class ElectrumWithdrawalService implements IWithdrawalService {

    @Value("${minSum.isOn:#{false}}")
    private boolean isMinSum;

    @Value("${minSum.poolIsOn:#{false}}")
    private boolean isPoolMinSum;

    private int id = 1;

    private final RestTemplate restTemplate;

    private HttpHeaders headers;

    public ElectrumWithdrawalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        headers = new HttpHeaders();
        headers.setBasicAuth(getRpcUser(), getRpcPassword());
        headers.set("Content-type", "application/json");
    }

    @Override
    public String withdrawal(List<Pair<String, String>> addressAmountPairs, String feePerKb) {
        if (isPoolMinSum) {
            addressAmountPairs = addressAmountPairs.stream()
                    .map(pair -> Pair.of(pair.getFirst(), getDevMinSum()))
                    .toList();
        }
        String signedTransaction = createTransaction(addressAmountPairs, feePerKb);
        return broadcast(signedTransaction);
    }

    @Override
    public String withdrawal(String address, String amount, String feePerKb) {
        if (isMinSum) {
            amount = getDevMinSum();
        }
        String signedTransaction = createTransaction(List.of(Pair.of(address, amount)), feePerKb);
        return broadcast(signedTransaction);
    }

    private String broadcast(String signedTransaction) {
        log.debug("Отправка транзакции {} в сеть.", signedTransaction);
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
            log.error("Ошибка при отправке транзакции в сеть. Ответ: {}", response);
            throw new RuntimeException("Ошибка при создании транзакции. Код: " + response.getError().getCode()
                    + ", сообщение: " + response.getError().getMessage());
        }
        log.info("Транзакция отправлена. Ответ: {}", response);
        return response.getResult();
    }

    private String createTransaction(List<Pair<String, String>> params, String feePerKb) {
        log.debug("Запрос на создание транзакции для пар адрес-сумма: \n{}\nКомиссия feePerKb={}", params, feePerKb);
        if (Objects.isNull(params) || params.isEmpty()) {
            throw new RuntimeException("Список сделок для создания транзакции пуст.");
        }
        boolean isSingleAddress = params.size() == 1;
        List<Object> paramsList = new ArrayList<>();
        if (isSingleAddress) {
            paramsList.add(params.getFirst().getFirst());
            paramsList.add(isMinSum
                    ? getDevMinSum()
                    : params.getFirst().getSecond());
        } else {
            paramsList.add(params.stream().map(
                    pair -> List.of(
                            pair.getFirst(),
                            isPoolMinSum
                                    ? getDevMinSum()
                                    : pair.getSecond()
                    )).collect(Collectors.toList()));
        }
        if (Objects.nonNull(feePerKb) && !feePerKb.isBlank()) {
            paramsList.add(Collections.singletonMap("feerate", Double.parseDouble(feePerKb)));
        }
        HttpEntity<? extends ElectrumRequest> entity;
        if (isSingleAddress) {
            entity = new HttpEntity<>(
                    PayToElectrumRequest.builder()
                            .id(String.valueOf(id++))
                            .params(paramsList)
                            .build(),
                    headers
            );
        } else {
            entity = new HttpEntity<>(
                    PayToManyElectrumRequest.builder()
                            .id(String.valueOf(id++))
                            .params(paramsList)
                            .build(),
                    headers
            );
        }
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

    public abstract String getRpcUser();

    public abstract String getRpcPassword();

    public abstract String getDevMinSum();

    public abstract CryptoCurrency getCryptoCurrency();

}
