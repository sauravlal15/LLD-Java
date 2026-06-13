package com.saurav.parkinglot;

import java.util.*;

enum VehicleType {
    CAR,
    BIKE,
    TRUCK
}

enum PaymentStatus {
    SUCCESS, FAILED
}

enum SpotStatus {
    OCCUPIED, AVAILABLE
}

abstract class Vehicle {

    String licenseNo;
    VehicleType type;

    Vehicle(String licenseNo, VehicleType type) {
        this.licenseNo = licenseNo;
        this.type = type;
    }
}

class Car extends Vehicle {

    Car(String licenseNo) {
        super(licenseNo, VehicleType.CAR);
    }
}

class Bike extends Vehicle {

    Bike(String licenseNo) {
        super(licenseNo, VehicleType.BIKE);
    }
}

class Truck extends Vehicle {

    Truck(String licenseNo) {
        super(licenseNo, VehicleType.TRUCK);
    }
}

class Spot {

    String id;
    VehicleType type;
    SpotStatus status;

    public Spot(String id, VehicleType type) {
        this.id = id;
        this.type = type;
        this.status = SpotStatus.AVAILABLE;
    }
}

class Ticket {

    String id;
    final Spot spot;
    final Vehicle vehicle;
    final long entryTime;

    public Ticket(Spot _spot, Vehicle _vehicle) {
        this.id = UUID.randomUUID().toString();
        this.spot = _spot;
        this.vehicle = _vehicle;
        this.entryTime = System.currentTimeMillis();
    }

    public void release() {
        spot.status = SpotStatus.AVAILABLE;
    }
}

class Payment {

    String id;
    Integer amount;
    PaymentStatus status;
    long timeStamp;

    public Payment(
            String id,
            Integer amount,
            PaymentStatus status
    ) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.timeStamp = System.currentTimeMillis();
    }
}

interface PricingStrategy {

    Integer calculate(Ticket ticket);
}

class HourlyPricing implements PricingStrategy {

    @Override
    public Integer calculate(Ticket ticket) {
        VehicleType type = ticket.vehicle.type;
        long exitTime = System.currentTimeMillis();
        long entryTime = ticket.entryTime;
        Integer hours = (int) Math.max(1, Math.ceil((exitTime - entryTime) / 3600000.0));

        int rate = switch (type) {
            case BIKE ->
                20;
            case CAR ->
                50;
            case TRUCK ->
                50;
        };
        return rate * hours;
    }

}

class FlatPricing implements PricingStrategy {

    @Override
    public Integer calculate(Ticket ticket) {
        VehicleType type = ticket.vehicle.type;
        long exitTime = System.currentTimeMillis();
        long entryTime = ticket.entryTime;
        Integer hour = (int) Math.max(1, Math.ceil((exitTime - entryTime) / 3600000.0));
        int rate = switch (type) {
            case CAR ->
                50;
            case BIKE ->
                20;
            case TRUCK ->
                80;
        };
        return hour * rate;
    }

}

interface PaymentGateway {

    Payment charge(String ticketId, Integer amount);
}

class MockPaymentGateway implements PaymentGateway {

    private static Integer counter = 0;

    @Override
    public Payment charge(String ticketId, Integer amount) {
        return new Payment("Payment-" + ++MockPaymentGateway.counter, amount, PaymentStatus.SUCCESS);
    }

}

class ParkingLot {

    private final List<Spot> spots;
    private final Map<String, Ticket> ticketRecords;
    private PricingStrategy pricingStrategy;

    ParkingLot(
            PricingStrategy ps
    ) {
        spots = new ArrayList<>();
        ticketRecords = new HashMap<>();
        this.pricingStrategy = ps;
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        Spot availableSpot = findAvailableSpot(vehicle.type);
        if (availableSpot == null) {
            System.out.println("No spot is available");
            return null;
        }
        availableSpot.status = SpotStatus.OCCUPIED;

        Ticket ticket = new Ticket(availableSpot, vehicle);
        ticketRecords.put(ticket.id, ticket);
        return ticket;
    }

    public Payment unparkVehicle(Ticket ticket) {
        if (ticket == null || !this.ticketRecords.containsKey(ticket.id)) {
            throw new IllegalArgumentException("Ticket does not exist");
        }
        Integer price = this.pricingStrategy.calculate(ticket);
        Payment payment = new Payment(UUID.randomUUID().toString(), price, PaymentStatus.SUCCESS);

        ticket.release();
        ticketRecords.remove(ticket.id);

        return payment;
    }

    public void addSpot(Spot spot) {
        spots.add(spot);
    }

    public Spot findAvailableSpot(VehicleType type) {
        for (Spot spot : spots) {
            if (spot.type == type && spot.status == SpotStatus.AVAILABLE) {
                return spot;
            }
        }
        return null;
    }
}

public class Main {

    public static void main(String[] args) {
        Car car = new Car("WB-1234");
        Bike bike = new Bike("BIKE-1234");
        Truck truck = new Truck("TRUCK-1234");

        HourlyPricing hourlyPricing = new HourlyPricing();
        ParkingLot parkingLot = new ParkingLot(hourlyPricing);

        parkingLot.addSpot(new Spot("C-1", VehicleType.CAR));
        parkingLot.addSpot(new Spot("B-1", VehicleType.BIKE));
        parkingLot.addSpot(new Spot("T-1", VehicleType.TRUCK));

        Ticket carTicket = parkingLot.parkVehicle(car);
        Ticket bikeTicket = parkingLot.parkVehicle(bike);
        Ticket truckTicket = parkingLot.parkVehicle(truck);

        if (carTicket != null) {
            Payment carPayment = parkingLot.unparkVehicle(carTicket);
            System.out.println("Car payment: " + carPayment.amount);
        }
    }
}
