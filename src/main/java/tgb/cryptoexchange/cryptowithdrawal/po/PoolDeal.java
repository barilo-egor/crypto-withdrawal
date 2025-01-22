package tgb.cryptoexchange.cryptowithdrawal.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tgb.cryptoexchange.enums.DeliveryType;
import tgb.cryptoexchange.serialize.LocalDateTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /**
     * Дата и время добавления сделки в пул
     */
    @Column(nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime addDate;

    /**
     * Тип доставки
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryType deliveryType;

    @JsonIgnore
    public boolean isValid() {
        return Objects.nonNull(bot) && Objects.nonNull(pid) && Objects.nonNull(address) && Objects.nonNull(amount)
                && Objects.nonNull(addDate) && Objects.nonNull(deliveryType);
    }
}
