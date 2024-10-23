package tgb.cryptoexchange.cryptowithdrawal.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElectrumError {

    private Integer code;

    private String message;
}
