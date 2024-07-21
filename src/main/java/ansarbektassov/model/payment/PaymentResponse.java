package ansarbektassov.model.payment;

import ansarbektassov.util.Currency;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class PaymentResponse {

    private String result;
    private Currency currency;
}
