package tgb.cryptoexchange.cryptowithdrawal.vo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class ElectrumRequest {

    private final String jsonrpc = "2.0";

    private final String id;

    private final List<Object> params;
}
