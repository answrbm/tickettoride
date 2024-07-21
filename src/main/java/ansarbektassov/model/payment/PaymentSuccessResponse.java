package ansarbektassov.model.payment;

import ansarbektassov.util.Currency;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSuccessResponse extends PaymentResponse {

    private String result;
    private BigDecimal change;
    private Currency currency;
}
