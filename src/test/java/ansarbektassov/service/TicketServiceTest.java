package ansarbektassov.service;

import ansarbektassov.dto.BoughtTicketRequestDTO;
import ansarbektassov.dto.BoughtTicketResponseDTO;
import ansarbektassov.dto.TicketResponseDTO;
import ansarbektassov.model.Ticket;
import ansarbektassov.model.payment.PaymentResponse;
import ansarbektassov.model.payment.PaymentSuccessResponse;
import ansarbektassov.repository.TicketRepository;
import ansarbektassov.util.Currency;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TicketServiceTest {

    private static TicketService ticketService;
    private static TicketRepository ticketRepository;

    @BeforeAll
    public static void init() {
        ticketRepository = mock(TicketRepository.class);
        ModelMapper modelMapper = new ModelMapper();
        RouteBuildService routeBuildService = new RouteBuildService();
        ticketService = new TicketService(routeBuildService, ticketRepository, modelMapper);
    }

    @Test
    public void findTicket_ExistedDepartureAndArrival_FoundTickets() {
        TicketResponseDTO actualTicketResponseDTO = ticketService.findTicket("London","Bristol");
        TicketResponseDTO expectedTicketResponseDTO = new TicketResponseDTO();
        expectedTicketResponseDTO.setSegments(7);
        expectedTicketResponseDTO.setCurrency(Currency.GBP);
        expectedTicketResponseDTO.setPrice(new BigDecimal(25));

        assertEquals(actualTicketResponseDTO.getPrice(),expectedTicketResponseDTO.getPrice());
        assertEquals(actualTicketResponseDTO.getCurrency(),expectedTicketResponseDTO.getCurrency());
        assertEquals(actualTicketResponseDTO.getSegments(),expectedTicketResponseDTO.getSegments());
    }

    @Test
    public void findTicket_NotExistedDepartureAndArrival_NotFoundTickets() {
        assertThrows(ResponseStatusException.class,() -> ticketService.findTicket("Londo","Bristo"));
    }

    @Test
    public void getBoughtTicketsByTravellerName_ExistedUser_FoundTickets() {
        List<Ticket> tickets = List.of(new Ticket(1L,"London","Bristol",7,new BigDecimal(25),
                Currency.GBP,"Adam Smith",new BigDecimal(30)));
        when(ticketRepository.findByTraveller("Adam Smith")).thenReturn(tickets);
        BoughtTicketResponseDTO boughtTicket = ticketService
                .getBoughtTicketsByTravellerName(new BoughtTicketRequestDTO("Adam Smith")).get(0);
        assertEquals(tickets.get(0).getTicketId(),boughtTicket.getTicketId());
        assertEquals(tickets.get(0).getTraveller(),boughtTicket.getTraveller());
        assertEquals(tickets.get(0).getTravellerAmount(),boughtTicket.getTravellerAmount());
        assertEquals(tickets.get(0).getCurrency(),boughtTicket.getCurrency());
        assertEquals(tickets.get(0).getArrival(),boughtTicket.getArrival());
        assertEquals(tickets.get(0).getDeparture(),boughtTicket.getDeparture());
        assertEquals(tickets.get(0).getPrice(),boughtTicket.getPrice());
        assertEquals(tickets.get(0).getSegments(),boughtTicket.getSegments());
    }

    @Test
    public void getBoughtTicketsByTravellerName_NotExistedUser_NotFoundTickets() {
        when(ticketRepository.findByTraveller("Bob Smith")).thenReturn(Collections.emptyList());
        List<BoughtTicketResponseDTO> boughtTicket = ticketService
                .getBoughtTicketsByTravellerName(new BoughtTicketRequestDTO("Bob Smith"));
        assertEquals(boughtTicket.size(),0);
    }

    @Test
    public void buyTicket_ValidTicket_TicketIsBought() {
        Ticket ticketToBuy = new Ticket(1L,"London","Bristol",7,new BigDecimal(25),
                Currency.GBP,"Adam Smith",new BigDecimal(25));
        PaymentSuccessResponse expectedResponse = new PaymentSuccessResponse("success",new BigDecimal(5),
                Currency.GBP);
        when(ticketRepository.save(ticketToBuy)).thenReturn(ticketToBuy);
        PaymentResponse paymentResponse = ticketService.buyTicket(ticketToBuy);
        assertEquals(paymentResponse.getResult(),expectedResponse.getResult());
    }

    @Test
    public void buyTicket_InvalidTicketNotExistedTown_TicketIsNotBought() {
        Ticket ticketToBuy = new Ticket(1L,"Londo","Bristol",7,new BigDecimal(25),
                Currency.GBP,"Adam Smith",new BigDecimal(25));
        assertThrows(ResponseStatusException.class,() -> ticketService.buyTicket(ticketToBuy));
    }

    @Test
    public void buyTicket_InvalidTicketNotValidSegments_TicketIsNotBought() {
        Ticket ticketToBuy = new Ticket(1L,"London","Bristol",6,new BigDecimal(25),
                Currency.GBP,"Adam Smith",new BigDecimal(25));
        assertThrows(ResponseStatusException.class,() -> ticketService.buyTicket(ticketToBuy));
    }

    @Test
    public void buyTicket_InvalidTicketNotValidPrice_TicketIsNotBought() {
        Ticket ticketToBuy = new Ticket(1L,"London","Bristol",7,new BigDecimal(24),
                Currency.GBP,"Adam Smith",new BigDecimal(25));
        assertThrows(ResponseStatusException.class,() -> ticketService.buyTicket(ticketToBuy));
    }

    @Test
    public void buyTicket_InvalidTicketNotValidCurrency_TicketIsNotBought() {
        Ticket ticketToBuy = new Ticket(1L,"London","Bristol",7,new BigDecimal(25),
                Currency.KZT,"Adam Smith",new BigDecimal(25));
        assertThrows(ResponseStatusException.class,() -> ticketService.buyTicket(ticketToBuy));
    }

    @Test
    public void buyTicket_NotEnoughMoney_TicketIsNotBought() {
        Ticket ticketToBuy = new Ticket(1L,"London","Bristol",7,new BigDecimal(25),
                Currency.KZT,"Adam Smith",new BigDecimal(20));
        assertThrows(ResponseStatusException.class,() -> ticketService.buyTicket(ticketToBuy));
    }


}
