package tgb.cryptoexchange.cryptowithdrawal.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tgb.cryptoexchange.enums.DeliveryType;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PoolDeal {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Идентификатор бота
     */
    @Column(nullable = false)
    private String bot;

    /**
     * Номер заявки в боте
     */
    @Column(nullable = false)
    private Long pid;

    /**
     * Адрес отправки криптовалюты
     */
    @Column(nullable = false)
    private String address;

    /**
     * Сумма отправки криптовалюты
     */
    @Column(nullable = false)
    private String amount;

    @Column(nullable = false)
    private LocalDate addDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryType deliveryType;

    @JsonIgnore
    public boolean isValid() {
        return Objects.nonNull(bot) && Objects.nonNull(pid) && Objects.nonNull(address) && Objects.nonNull(amount)
                && Objects.nonNull(addDate) && Objects.nonNull(deliveryType);
    }
}
