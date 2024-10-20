package tgb.cryptoexchange.cryptowithdrawal.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBalanceElectrumResponse {

    private Integer id;

    private String jsonrpc;

    private Result result;

    private ElectrumError error;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private String confirmed;

        private String unconfirmed;
    }
}
