package ansarbektassov.dto;

import ansarbektassov.util.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoughtTicketResponseDTO {

    private Long ticketId;
    private String departure;
    private String arrival;
    private Integer segments;
    private BigDecimal price;
    private Currency currency;
    private String traveller;
    private BigDecimal travellerAmount;
}
