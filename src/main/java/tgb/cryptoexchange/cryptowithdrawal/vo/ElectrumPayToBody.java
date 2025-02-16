package tgb.cryptoexchange.cryptowithdrawal.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ElectrumPayToBody {

    private String destination;

    private String amount;

    private Integer feerate;
}
