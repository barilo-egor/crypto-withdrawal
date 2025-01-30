package tgb.cryptoexchange.cryptowithdrawal.service.wallet;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.IWalletService;
import tgb.cryptoexchange.cryptowithdrawal.vo.CloseWalletElectrumRequest;
import tgb.cryptoexchange.cryptowithdrawal.vo.ElectrumResponse;
import tgb.cryptoexchange.cryptowithdrawal.vo.LoadWalletElectrumRequest;
import tgb.cryptoexchange.cryptowithdrawal.vo.RestoreWalletElectrumRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class ElectrumWalletService implements IWalletService {

    private final RestTemplate restTemplate;

    private HttpHeaders headers;

    private int id = 1;

    protected ElectrumWalletService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        headers = new HttpHeaders();
        headers.setBasicAuth(getRpcUser(), getRpcPassword());
        headers.set("Content-type", "application/json");
    }

    @Override
    public void loadWallet(String seedPhrase) {
        try {
            closeWallet();
            sleep();
        } catch (RuntimeException e) {
            log.warn("Кошелек уже закрыт или отсутствует: {}", e.getMessage());
        }
        deleteDefaultWalletFile();
        sleep();
        restoreWallet(seedPhrase);
        loadNewWallet();
    }

    private void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Задержка прервана.", e);
        }
    }

    private void closeWallet() {
        try {
            HttpEntity<CloseWalletElectrumRequest> entity = new HttpEntity<>(
                    CloseWalletElectrumRequest.builder()
                            .id(String.valueOf(id++))
                            .params(List.of())
                            .build(),
                    headers
            );
            ElectrumResponse response = restTemplate.exchange(
                    getUrl(), HttpMethod.POST, entity, ElectrumResponse.class
            ).getBody();
            validResponse(response, "закрытие кошелька");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при закрытии кошелька.",e);
        }
    }

    private void deleteDefaultWalletFile() {
        try {
            // Получаем путь к файлу default_wallet
            Path defaultWalletPath = Paths.get(System.getProperty("user.home"), getDirectory(), "wallets", "default_wallet");

            // Проверяем, существует ли файл
            if (Files.exists(defaultWalletPath)) {
                // Удаляем файл
                Files.delete(defaultWalletPath);
                log.info("Файл default_wallet успешно удален.");
            } else {
                log.warn("Файл default_wallet не найден.");
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении файла default_wallet.", e);
            throw new RuntimeException("Ошибка при удалении файла default_wallet.", e);
        }
    }

    private void restoreWallet(String seedPhrase) {
        try {
            HttpEntity<RestoreWalletElectrumRequest> entity = new HttpEntity<>(
                    RestoreWalletElectrumRequest.builder()
                            .id(String.valueOf(id++))
                            .params(List.of(seedPhrase))
                            .build(),
                    headers
            );
            ElectrumResponse response = restTemplate.exchange(
                    getUrl(), HttpMethod.POST, entity, ElectrumResponse.class
            ).getBody();
            validResponse(response, "восстановление кошелька");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при восстановлении кошелька.",e);
        }
    }

    private void loadNewWallet() {
        HttpEntity<LoadWalletElectrumRequest> entity = new HttpEntity<>(
                LoadWalletElectrumRequest.builder()
                        .id(String.valueOf(id++))
                        .params(List.of())
                        .build(),
                headers
        );
        ElectrumResponse response = restTemplate.exchange(
                getUrl(), HttpMethod.POST, entity, ElectrumResponse.class
        ).getBody();
        validResponse(response, "загрузка кошелька");
    }

    private void validResponse(ElectrumResponse response, String operation) {
        if (Objects.isNull(response)) {
            throw new RuntimeException("Ответ запроса \"" + operation + "\" пуст.");
        }
        if (Objects.nonNull(response.getError())) {
            log.error("Ошибка в ответе запроса \"{}\". Ответ: {}", operation, response);
            throw new RuntimeException("Ошибка в ответе запроса \"" + operation + "\". Код: " + response.getError().getCode()
                    + ", сообщение: " + response.getError().getMessage());
        }
        if (Objects.isNull(response.getResult())) {
            String message = "Отсутствует result при запросе \"" + operation + "\".";
            log.error("{}\nОтвет: {}", message, response);
            throw new RuntimeException(message);
        }
    }

    public abstract String getUrl();

    public abstract String getRpcUser();

    public abstract String getRpcPassword();

    public abstract String getDirectory();
}
