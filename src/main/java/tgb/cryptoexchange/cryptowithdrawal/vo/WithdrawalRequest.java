package tgb.cryptoexchange.cryptowithdrawal.vo;

import lombok.Builder;
import lombok.Data;
import tgb.cryptoexchange.enums.CryptoCurrency;

@Data
@Builder
public class WithdrawalRequest {

    private CryptoCurrency cryptoCurrency;

    private String amount;

    private String address;

    private String fee;
}

