package tgb.cryptoexchange.cryptowithdrawal.service.balance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import tgb.cryptoexchange.cryptowithdrawal.vo.ElectrumError;
import tgb.cryptoexchange.cryptowithdrawal.vo.GetBalanceElectrumResponse;
import tgb.cryptoexchange.enums.CryptoCurrency;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class LitecoinBalanceRetrieverTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private LitecoinBalanceRetriever litecoinBalanceRetriever;

    @BeforeEach
    void setup() {
        when(restTemplateBuilder.setConnectTimeout(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        litecoinBalanceRetriever = Mockito.spy(new LitecoinBalanceRetriever(restTemplateBuilder));
    }

    @Test
    void testGetDevBalance() {
        when(litecoinBalanceRetriever.isDev()).thenReturn(true);
        BigDecimal actual = litecoinBalanceRetriever.getBalance();
        BigDecimal expected = new BigDecimal(500);
        assertEquals(actual, expected);
    }

    @Test
    void testGetBalanceWithOffWithdrawal() {
        when(litecoinBalanceRetriever.isDev()).thenReturn(false);
        when(litecoinBalanceRetriever.isWithdrawalOn()).thenReturn(false);
        assertThrows(RuntimeException.class, () -> litecoinBalanceRetriever.getBalance(),
                "Автовывод для " + CryptoCurrency.LITECOIN.name() + " выключен.");
    }

    @Test
    void testNullResponse() {
        when(litecoinBalanceRetriever.isDev()).thenReturn(false);
        when(litecoinBalanceRetriever.isWithdrawalOn()).thenReturn(true);
        when(litecoinBalanceRetriever.getRpcUser()).thenReturn("test");
        when(litecoinBalanceRetriever.getRpcPassword()).thenReturn("test");
        when(litecoinBalanceRetriever.getUrl()).thenReturn("test");
        ResponseEntity<GetBalanceElectrumResponse> response = new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        when(restTemplate.exchange(anyString(), any(), any(), eq(GetBalanceElectrumResponse.class))).thenReturn(response);
        assertThrows(RuntimeException.class, () -> litecoinBalanceRetriever.getBalance(), "Отсутствует объект ответа.");
    }

    @Test
    void testErrorResponse() {
        when(litecoinBalanceRetriever.isDev()).thenReturn(false);
        when(litecoinBalanceRetriever.isWithdrawalOn()).thenReturn(true);
        when(litecoinBalanceRetriever.getRpcUser()).thenReturn("test");
        when(litecoinBalanceRetriever.getRpcPassword()).thenReturn("test");
        when(litecoinBalanceRetriever.getUrl()).thenReturn("test");
        GetBalanceElectrumResponse getBalanceElectrumResponse = new GetBalanceElectrumResponse();
        ElectrumError error = new ElectrumError();
        String expectedError = "some error";
        error.setMessage(expectedError);
        getBalanceElectrumResponse.setError(error);
        ResponseEntity<GetBalanceElectrumResponse> response = new ResponseEntity<>(getBalanceElectrumResponse, HttpStatus.ACCEPTED);
        when(restTemplate.exchange(anyString(), any(), any(), eq(GetBalanceElectrumResponse.class))).thenReturn(response);
        assertThrows(RuntimeException.class, () -> litecoinBalanceRetriever.getBalance(),
                "Ошибка получения баланса для " + CryptoCurrency.LITECOIN.name()
                        + ". Сообщение от electrum: " + expectedError);
    }

    @Test
    void testNoResultNoErrorResponse() {
        when(litecoinBalanceRetriever.isDev()).thenReturn(false);
        when(litecoinBalanceRetriever.isWithdrawalOn()).thenReturn(true);
        when(litecoinBalanceRetriever.getRpcUser()).thenReturn("test");
        when(litecoinBalanceRetriever.getRpcPassword()).thenReturn("test");
        when(litecoinBalanceRetriever.getUrl()).thenReturn("test");
        GetBalanceElectrumResponse getBalanceElectrumResponse = new GetBalanceElectrumResponse();
        ResponseEntity<GetBalanceElectrumResponse> response = new ResponseEntity<>(getBalanceElectrumResponse, HttpStatus.ACCEPTED);
        when(restTemplate.exchange(anyString(), any(), any(), eq(GetBalanceElectrumResponse.class))).thenReturn(response);
        assertThrows(RuntimeException.class, () -> litecoinBalanceRetriever.getBalance(),
                "В ответе при получении баланса отсутствуют confirmed и error.");
    }

    @Test
    void testNoConfirmedResponse() {
        when(litecoinBalanceRetriever.isDev()).thenReturn(false);
        when(litecoinBalanceRetriever.isWithdrawalOn()).thenReturn(true);
        when(litecoinBalanceRetriever.getRpcUser()).thenReturn("test");
        when(litecoinBalanceRetriever.getRpcPassword()).thenReturn("test");
        when(litecoinBalanceRetriever.getUrl()).thenReturn("test");
        GetBalanceElectrumResponse getBalanceElectrumResponse = new GetBalanceElectrumResponse();
        GetBalanceElectrumResponse.Result result = new GetBalanceElectrumResponse.Result();
        result.setConfirmed("");
        getBalanceElectrumResponse.setResult(result);
        ResponseEntity<GetBalanceElectrumResponse> response = new ResponseEntity<>(getBalanceElectrumResponse, HttpStatus.ACCEPTED);
        when(restTemplate.exchange(anyString(), any(), any(), eq(GetBalanceElectrumResponse.class))).thenReturn(response);
        assertThrows(RuntimeException.class, () -> litecoinBalanceRetriever.getBalance(),
                "В ответе при получении баланса отсутствуют confirmed и error.");
    }

    @Test
    void testSuccessResponse() {
        when(litecoinBalanceRetriever.isDev()).thenReturn(false);
        when(litecoinBalanceRetriever.isWithdrawalOn()).thenReturn(true);
        when(litecoinBalanceRetriever.getRpcUser()).thenReturn("testUser");
        when(litecoinBalanceRetriever.getRpcPassword()).thenReturn("testPass");
        when(litecoinBalanceRetriever.getUrl()).thenReturn("testUrl");
        GetBalanceElectrumResponse getBalanceElectrumResponse = new GetBalanceElectrumResponse();
        GetBalanceElectrumResponse.Result result = new GetBalanceElectrumResponse.Result();
        result.setConfirmed("0.005");
        getBalanceElectrumResponse.setResult(result);
        ResponseEntity<GetBalanceElectrumResponse> response = new ResponseEntity<>(getBalanceElectrumResponse, HttpStatus.ACCEPTED);
        when(restTemplate.exchange(anyString(), any(), any(), eq(GetBalanceElectrumResponse.class))).thenReturn(response);
        assertEquals(new BigDecimal("0.005"), litecoinBalanceRetriever.getBalance());
        ArgumentCaptor<HttpEntity<Map<String, Object>>> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("testUrl"), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(), eq(GetBalanceElectrumResponse.class));
        HttpEntity<Map<String, Object>> httpEntity = httpEntityArgumentCaptor.getValue();
        Map<String, Object> body = httpEntity.getBody();
        assertNotNull(body);
        assertAll(
                () -> assertTrue(body.containsKey("method")),
                () -> assertEquals("getbalance", body.get("method")),
                () -> assertTrue(body.containsKey("params")),
                () -> assertTrue(body.containsKey("id")),
                () -> assertEquals(body.get("id"), 1),
                () -> assertTrue(body.containsKey("jsonrpc")),
                () -> assertEquals(body.get("jsonrpc"), "2.0")
        );
    }
}