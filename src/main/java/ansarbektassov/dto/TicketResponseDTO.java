package ansarbektassov.dto;

import ansarbektassov.util.Currency;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDTO {

    private Integer segments;
    private BigDecimal price;
    private Currency currency;
}
