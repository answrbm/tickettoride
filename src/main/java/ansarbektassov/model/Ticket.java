package ansarbektassov.model;

import ansarbektassov.util.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bought_tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;
    @Column(name = "departure")
    private String departure;
    @Column(name = "arrival")
    private String arrival;
    @Column(name = "segments")
    private Integer segments;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Column(name = "traveller")
    private String traveller;
    @Column(name = "traveller_amount")
    private BigDecimal travellerAmount;
}
