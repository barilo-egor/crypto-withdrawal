package tgb.cryptoexchange.cryptowithdrawal.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Класс, представляющий результат вывода сделок из пула.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PoolCompleteResult {
    /**
     * Идентификатор бота
     */
    private String bot;

    /**
     * Пиды сделок, выведенных из пула
     */
    private List<Long> pids;
}
