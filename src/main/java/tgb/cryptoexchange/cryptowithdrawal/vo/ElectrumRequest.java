package tgb.cryptoexchange.cryptowithdrawal.vo;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ElectrumRequest {

    private final String jsonrpc = "2.0";

    private final String id;
}
