package tgb.cryptoexchange.cryptowithdrawal.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleTransactionElectrumResponse {

    private Integer id;

    private String jsonrpc;

    private String result;

    private ElectrumError error;
}
