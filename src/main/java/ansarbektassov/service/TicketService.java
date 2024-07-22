package ansarbektassov.service;

import ansarbektassov.dto.BoughtTicketRequestDTO;
import ansarbektassov.dto.BoughtTicketResponseDTO;
import ansarbektassov.dto.TicketResponseDTO;
import ansarbektassov.exception.TownNotFoundException;
import ansarbektassov.model.Ticket;
import ansarbektassov.model.payment.PaymentFailureResponse;
import ansarbektassov.model.payment.PaymentResponse;
import ansarbektassov.model.payment.PaymentSuccessResponse;
import ansarbektassov.repository.TicketRepository;
import ansarbektassov.util.Currency;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TicketService {

    private final RouteBuildService routeBuildService;
    private final TicketRepository ticketRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TicketService(RouteBuildService routeBuildService, TicketRepository ticketRepository, ModelMapper modelMapper) {
        this.routeBuildService = routeBuildService;
        this.ticketRepository = ticketRepository;
        this.modelMapper = modelMapper;
    }

    public List<BoughtTicketResponseDTO> getBoughtTicketsByTravellerName(BoughtTicketRequestDTO boughtTicketRequestDTO) {
        log.info("getBoughtTicketsByTravellerName() called");
        return ticketRepository.findByTraveller(boughtTicketRequestDTO.getTravellerName()).stream().map(ticket ->
                        modelMapper.map(ticket, BoughtTicketResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Persists the bought ticket to a database
     * <p>
     *     This method validates if towns in the ticket are exist and if other parameters,
     *     such as segments, price, currency are corresponding to the ticket from findTicket().
     *     In case everything is valid, the ticket is saved to a database and returns the payment status.
     * </p>
     * @param ticketToBuy the ticket the user want to buy
     * @return a new {@code PaymentResponse} representing the response about status of the ticket payment
     * @throws ResponseStatusException if the {@code ticketToBuy.getDeparture()}
     *         or {@code ticketToBuy.getArrival()} don't exist and if other parameters
     *         {@code segments}, {@code price}, {@code currency} is not corresponding to the available ticket
     */
    public PaymentResponse buyTicket(Ticket ticketToBuy) {
        log.info("buyTicket() called");
        try {
            routeBuildService.findTownByName(ticketToBuy.getDeparture());
            routeBuildService.findTownByName(ticketToBuy.getArrival());
        } catch (TownNotFoundException e) {
            log.warn("TicketService.buyTicket(): " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }

        TicketResponseDTO ticket = findTicket(ticketToBuy.getDeparture(),ticketToBuy.getArrival());
        Integer segments = ticketToBuy.getSegments();
        BigDecimal price = ticketToBuy.getPrice();
        Currency currency = ticketToBuy.getCurrency();

        if(!segments.equals(ticket.getSegments()) || !price.equals(ticket.getPrice()) || currency != ticket.getCurrency()) {
            String message = "Ticket state is not corresponding to the available ticket";
            log.warn("TicketService.buyTicket(): " + message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }

        BigDecimal travellerAmount = ticketToBuy.getTravellerAmount();
        BigDecimal change = travellerAmount.subtract(price);
        log.debug("change variable set: " + change);
        log.debug("change.compareTo(0) = " + change.compareTo(BigDecimal.ZERO));
        if(change.compareTo(BigDecimal.ZERO) >= 0) {
            ticketRepository.save(ticketToBuy);
            return new PaymentSuccessResponse("success",change,ticketToBuy.getCurrency());
        }
        return new PaymentFailureResponse("failure",change.abs(),ticketToBuy.getCurrency());
    }

    public TicketResponseDTO findTicket(String departure, String arrival) {
        log.info("findTicket() called");
        try {
            Integer segments = routeBuildService.getShortestTravel(departure, arrival);

            return new TicketResponseDTO(segments, calculatePrice(segments), Currency.GBP);
        } catch (TownNotFoundException e) {
            log.warn("TicketService.findTicket(): " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    /**
     * Calculate the price for the ticket
     * <p>
     *     This method calculates the price for the ticket depending on the segments.
     *     It starts calculations from counting 3 values and then proceed with 2 and 1.
     *     The price of the travel through 3 segments is 10 GBP.
     *     The price of the travel through 2 segments is 7 GBP.
     *     The price of the travel through 1 segment is 5 GBP.
     *     Depending on the given values it calculates the price.
     * </p>
     * @param segments the quantity of segments to calculate the price
     * @return a new {@code BigDecimal} representing the calculated price
     */
    private BigDecimal calculatePrice(Integer segments) {
        log.info("calculatePrice() called");
        BigDecimal price = new BigDecimal("0");
        int quantityOfThreeSegmentsPairs;
        while(segments > 0) {
            if (segments >= 3) {
                quantityOfThreeSegmentsPairs = segments / 3;
                price = price.add(BigDecimal.valueOf(quantityOfThreeSegmentsPairs * 10L));
                segments = segments - (quantityOfThreeSegmentsPairs * 3);
            } else if (segments == 2) {
                price = price.add(BigDecimal.valueOf(7L));
                segments -= 2;
            } else {
                price = price.add(BigDecimal.valueOf(5L));
                segments -= 1;
            }
        }
        return price;
    }

}
