package tgb.cryptoexchange.cryptowithdrawal.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PayToElectrumRequest extends ElectrumRequest {

    private ElectrumPayToBody params;

    private final String method = "payto";
}
