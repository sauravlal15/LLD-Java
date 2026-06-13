package com.saurav.bookingsystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/*
Movie Ticket Booking System

1. search/ filter
2. Select show
3. Choose seats 
4. Lock seats 
5. Payment 
6. 
 */
enum SeatType {
    PREMIUM, STANDARD, RECLINER
}

enum SeatStatus {
    AVAILABLE, NOT_AVAILABLE
}

enum PaymentStatus {
    SUCCESS, FAILED, REFUND
}

class Seat {

    String id;
    SeatType type;
    SeatStatus status;

    public Seat(SeatType type) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.status = SeatStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
    }

    public void reserve() {
        this.status = SeatStatus.NOT_AVAILABLE;
    }

    @Override
    public String toString() {
        return type + "(" + id.substring(0, 4) + ")";
    }
}

class Show {

    String id;
    List<Seat> seats;
    String name;
    long startTime;
    long endTime;

    public Show(List<Seat> seats, String name) {
        this.id = UUID.randomUUID().toString();
        this.seats = new ArrayList<>(seats);
        this.name = name;
        this.startTime = System.currentTimeMillis();
        this.endTime = System.currentTimeMillis() + (3 * 60 * 60 * 1000);
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> result = new ArrayList<>();
        for (Seat seat : seats) {
            if (seat.isAvailable()) {
                result.add(seat);
            }
        }
        return result;
    }

    public boolean reserveSeats(Seat[] selectedSeats) {
        for (Seat seat : selectedSeats) {
            if (!seat.isAvailable()) {
                return false;
            }
        }
        for (Seat seat : selectedSeats) {
            seat.reserve();
        }
        return true;
    }

    @Override
    public String toString() {
        return name + " (" + id.substring(0, 4) + ")";
    }
}

class Theatre {

    String id;
    String name;
    String city;
    final Map<String, Show> showRecords;

    public Theatre(String name, String city) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.city = city;
        this.showRecords = new HashMap<>();
    }

    public void addShow(Show show) {
        this.showRecords.put(show.id, show);
    }

    public Optional<Show> getShow(String showId) {
        return Optional.ofNullable(showRecords.get(showId));
    }

    public Collection<Show> getShows() {
        return showRecords.values();
    }

    @Override
    public String toString() {
        return name + " [" + city + "]";
    }
}

class Ticket {

    String id;
    Theatre theatre;
    Show show;
    List<Seat> seats;
    Integer amount;

    public Ticket(Theatre theatre, Show show, List<Seat> seats, Integer amount) {
        this.id = UUID.randomUUID().toString();
        this.theatre = theatre;
        this.show = show;
        this.seats = seats;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Ticket[" + id.substring(0, 4) + "] " + show.name + " @ " + theatre.name + " seats=" + seats + " amount=" + amount;
    }
}

interface PricingStrategy {

    Integer calculate(Theatre theatre, Seat[] seats);
}

class StandardPricingStrategy implements PricingStrategy {

    @Override
    public Integer calculate(Theatre theatre, Seat[] seats) {
        Integer total = 0;
        for (Seat s : seats) {
            switch (s.type) {
                case PREMIUM:
                    total += 200;
                    break;
                case RECLINER:
                    total += 250;
                    break;
                default:
                    total += 150;
                    break;
            }
        }
        return total;
    }
}

class Payment {

    String id;
    Integer amount;
    PaymentStatus paymentStatus;
    long timeStamp;

    public Payment(String id, Integer amount, PaymentStatus paymentStatus) {
        this.id = id;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.timeStamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Payment[" + id.substring(0, 4) + "] amount=" + amount + " status=" + paymentStatus;
    }
}

interface PaymentGateway {

    Payment charge(String ticketId, Integer amount);

    Payment refund(String ticketId, Integer amount);
}

class MockPaymentGateway implements PaymentGateway {

    @Override
    public Payment charge(String ticketId, Integer amount) {
        return new Payment(ticketId, amount, PaymentStatus.SUCCESS);
    }

    @Override
    public Payment refund(String ticketId, Integer amount) {
        return new Payment(ticketId, amount, PaymentStatus.REFUND);
    }
}

interface SearchStrategy {

    List<Theatre> search(List<Theatre> allTheatres, String query);
}

class SearchByShowName implements SearchStrategy {

    @Override
    public List<Theatre> search(List<Theatre> allTheatres, String showName) {
        List<Theatre> result = new ArrayList<>();
        for (Theatre theatre : allTheatres) {
            for (Show show : theatre.getShows()) {
                if (show.name.equalsIgnoreCase(showName)) {
                    result.add(theatre);
                    break;
                }
            }
        }
        return result;
    }
}

class SearchByCity implements SearchStrategy {

    @Override
    public List<Theatre> search(List<Theatre> allTheatres, String city) {
        List<Theatre> result = new ArrayList<>();
        for (Theatre theatre : allTheatres) {
            if (theatre.city.equalsIgnoreCase(city)) {
                result.add(theatre);
            }
        }
        return result;
    }
}

class BookingService {

    final Map<String, Theatre> theatreRecords = new HashMap<>();
    final Map<String, Payment> paymentRecords = new HashMap<>();
    final Map<String, Ticket> ticketRecords = new HashMap<>();
    SearchStrategy searchStrategy;
    PaymentGateway paymentGateway;
    PricingStrategy pricingStrategy;

    public BookingService(SearchStrategy sg, PaymentGateway pg, PricingStrategy ps) {
        this.searchStrategy = sg;
        this.paymentGateway = pg;
        this.pricingStrategy = ps;
    }

    public Ticket bookShow(Show show, Theatre theatre, Seat[] seats) {
        if (!this.theatreRecords.containsKey(theatre.id)) {
            throw new IllegalArgumentException("Theatre does not exist!");
        }
        if (!theatre.getShow(show.id).isPresent()) {
            throw new IllegalArgumentException("Show does not belong to this theatre!");
        }
        if (!show.reserveSeats(seats)) {
            throw new IllegalArgumentException("One or more selected seats are unavailable.");
        }

        Integer amount = this.pricingStrategy.calculate(theatre, seats);
        Ticket ticket = new Ticket(theatre, show, Arrays.asList(seats), amount);
        Payment payment = this.paymentGateway.charge(ticket.id, amount);
        this.paymentRecords.put(payment.id, payment);
        return ticket;
    }

    public List<Theatre> search(String query) {
        List<Theatre> theatres = new ArrayList<>(this.theatreRecords.values());
        return this.searchStrategy.search(theatres, query);
    }

    public Payment cancelBooking(Ticket ticket) {
        if (!this.ticketRecords.containsKey(ticket.id)) {
            throw new IllegalArgumentException("Ticket id not exists!");
        }
        for (Seat seat : ticket.seats) {
            seat.release();
        }
        Payment payment = this.paymentGateway.refund(ticket.id, ticket.amount);
        return payment;
    }

    public void addTheatre(Theatre theatre) {
        this.theatreRecords.put(theatre.id, theatre);
    }
}

public class Main {

    public static void main(String[] args) {
        Theatre theatre1 = new Theatre("IMAX", "Hyderabad");
        Theatre theatre2 = new Theatre("PVR", "Hyderabad");

        Show avengers = new Show(
                Arrays.asList(new Seat(SeatType.PREMIUM), new Seat(SeatType.STANDARD), new Seat(SeatType.STANDARD)),
                "Avengers: Endgame"
        );
        Show matrix = new Show(
                Arrays.asList(new Seat(SeatType.RECLINER), new Seat(SeatType.STANDARD), new Seat(SeatType.STANDARD)),
                "The Matrix"
        );

        theatre1.addShow(avengers);
        theatre2.addShow(matrix);

        SearchByCity searchByCity = new SearchByCity();
        StandardPricingStrategy pricing = new StandardPricingStrategy();
        MockPaymentGateway mockPaymentGateway = new MockPaymentGateway();

        BookingService bookingService = new BookingService(searchByCity, mockPaymentGateway, pricing);
        bookingService.addTheatre(theatre1);
        bookingService.addTheatre(theatre2);

        System.out.println("Searching theatres in Hyderabad...");
        List<Theatre> found = bookingService.search("Hyderabad");
        for (Theatre theatre : found) {
            System.out.println("- " + theatre);
            for (Show show : theatre.getShows()) {
                System.out.println("    show: " + show + " available seats=" + show.getAvailableSeats());
            }
        }

        System.out.println();
        Seat[] selectedSeats = new Seat[]{avengers.getAvailableSeats().get(0), avengers.getAvailableSeats().get(1)};
        Ticket ticket = bookingService.bookShow(avengers, theatre1, selectedSeats);

        System.out.println("Booking complete:");
        System.out.println(ticket);
    }
}
