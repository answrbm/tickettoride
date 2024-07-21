package ansarbektassov.model.payment;

import ansarbektassov.util.Currency;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailureResponse extends PaymentResponse {

    private String result;
    private BigDecimal lackOf;
    private Currency currency;
}
