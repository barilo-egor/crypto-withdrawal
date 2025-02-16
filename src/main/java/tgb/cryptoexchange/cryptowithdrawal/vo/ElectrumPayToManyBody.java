package tgb.cryptoexchange.cryptowithdrawal.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ElectrumPayToManyBody {

    private List<Object> outputs;

    private Integer feerate;
}
