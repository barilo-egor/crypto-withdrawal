package tgb.cryptoexchange.cryptowithdrawal.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PayToManyElectrumRequest extends ElectrumRequest {

    private final String method = "paytomany";
}
