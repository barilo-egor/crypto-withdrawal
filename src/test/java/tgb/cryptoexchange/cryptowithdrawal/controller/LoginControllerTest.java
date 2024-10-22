package tgb.cryptoexchange.cryptowithdrawal.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginControllerTest {

    @SpyBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Value("${security.username}")
    private String username;

    @Value("${security.password}")
    private String password;

    @BeforeEach
    public void setUp() {
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    public void testWithoutCredentials() throws Exception {
        mockMvc.perform(post("/authenticate"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testWithNotValidCredentials() throws Exception {
        mockMvc.perform(post("/authenticate")
                        .param("username", "invalid")
                        .param("password", "invalid"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testWithValidCredentials() throws Exception {
        mockMvc.perform(post("/authenticate")
                .param("username", username)
                .param("password", password))
                .andExpect(status().isOk());
    }
}