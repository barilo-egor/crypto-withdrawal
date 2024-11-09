package tgb.cryptoexchange.cryptowithdrawal.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tgb.cryptoexchange.cryptowithdrawal.po.PoolDeal;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PoolOperation {

    private String operation;

    private List<PoolDeal> poolDeals;

    private String data;
}
