package ansarbektassov.controller;

import ansarbektassov.dto.RouteDTO;
import ansarbektassov.dto.TicketResponseDTO;
import ansarbektassov.dto.TicketRequestDTO;
import ansarbektassov.dto.BoughtTicketResponseDTO;
import ansarbektassov.dto.BoughtTicketRequestDTO;
import ansarbektassov.model.Ticket;
import ansarbektassov.model.payment.PaymentResponse;
import ansarbektassov.service.TicketService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final ModelMapper modelMapper;

    @Autowired
    public TicketController(TicketService ticketService, ModelMapper modelMapper) {
        this.ticketService = ticketService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/ticket")
    public TicketResponseDTO findTicketToBuy(@RequestBody RouteDTO routeDTO) {
        return ticketService.findTicket(routeDTO.getDeparture(), routeDTO.getArrival());
    }

    @GetMapping
    public List<BoughtTicketResponseDTO> findBoughtTicketsByTravellerName(@RequestBody BoughtTicketRequestDTO boughtTicketRequestDTO) {
        return ticketService.getBoughtTicketsByTravellerName(boughtTicketRequestDTO);
    }

    @PostMapping
    public PaymentResponse buyTicket(@RequestBody TicketRequestDTO ticketRequestDTO) {
        Ticket ticket = modelMapper.map(ticketRequestDTO, Ticket.class);
        return ticketService.buyTicket(ticket);
    }
}
