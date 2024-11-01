package tgb.cryptoexchange.cryptowithdrawal.service.withdrawal;

import org.springframework.data.util.Pair;
import tgb.cryptoexchange.enums.CryptoCurrency;

import java.util.List;

public interface IWithdrawalService {

    String withdrawal(List<Pair<String, String>> addressAmountPairs);

    String withdrawal(String address, String amount);

    CryptoCurrency getCryptoCurrency();

}
