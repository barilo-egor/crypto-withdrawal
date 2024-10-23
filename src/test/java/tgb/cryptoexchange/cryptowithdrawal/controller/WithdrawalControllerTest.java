package tgb.cryptoexchange.cryptowithdrawal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tgb.cryptoexchange.cryptowithdrawal.service.JwtUtil;
import tgb.cryptoexchange.enums.CryptoCurrency;
import tgb.cryptoexchange.web.ApiResponse;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WithdrawalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${security.username}")
    private String username;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testReturnBalance() throws Exception {
        String token = jwtUtil.generateToken(username);
        ApiResponse<BigDecimal> expected = new ApiResponse<>();
        expected.setSuccess(true);
        expected.setData(new BigDecimal(500));
        mockMvc.perform(get("/balance")
                .header("Authorization", "Bearer " + token)
                .param("cryptoCurrency", CryptoCurrency.BITCOIN.name()))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expected)));
    }

    @Test
    public void testUnknownCryptoCurrency() throws Exception {
        String token = jwtUtil.generateToken(username);
        ApiResponse<BigDecimal> expected = new ApiResponse<>();
        expected.setSuccess(false);
        expected.setError(ApiResponse.Error.builder().message("Автовывод для данной криптовалюты не предусмотрен.").build());
        mockMvc.perform(get("/balance")
                        .header("Authorization", "Bearer " + token)
                        .param("cryptoCurrency", CryptoCurrency.MONERO.name()))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expected)));
    }
}