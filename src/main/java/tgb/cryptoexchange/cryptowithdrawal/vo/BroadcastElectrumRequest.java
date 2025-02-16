package tgb.cryptoexchange.cryptowithdrawal.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BroadcastElectrumRequest extends ElectrumRequest {

    private List<Object> params;

    private final String method = "broadcast";
}
