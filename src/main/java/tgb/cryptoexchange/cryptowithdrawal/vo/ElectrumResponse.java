package tgb.cryptoexchange.cryptowithdrawal.vo;

import lombok.Data;

@Data
public class ElectrumResponse {

    private String id;

    private Object result;

    private ElectrumError error;
}
