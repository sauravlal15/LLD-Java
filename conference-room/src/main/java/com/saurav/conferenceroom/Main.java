package com.saurav.conferenceroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*
Design and implement a thread safe in-memory conference room reservation system for a company with multiple office buildings.

Employees should be able to search and reserve conference rooms across buildings based on time, capacity, and room features. The system should prevent double-booking, 
handle concurrent reservation requests, and support reservation cancellation.

You are expected to write clean, executable code with proper classes, abstractions, and tests.

Functional Requirements
Buildings and rooms
The system has multiple buildings. Each building has multiple conference rooms.

Assume buildings and rooms are preconfigured at system startup.

Reserve a room
The system should:

Find an available room and reserve it if available. 

Room availability check:
This should return true if the room is available.
Use half-open interval semantics.

Cancel reservation: Cancel a reservation. 

List reservations for employee: Return all non-cancelled reservations for that employee sorted by start time.

Employee
Room
Building




 */
enum RoomType {
    XXL, XL, L, M, S
}

class Employee {

    String id;
    String name;

    public Employee(
            String id,
            String name
    ) {
        this.id = id;
        this.name = name;
    }
}

class Room {

    public List<TimeRange> slotBooked = new ArrayList<>();
    String id;
    RoomType type;

    public Room(
            String id,
            RoomType type
    ) {
        this.id = id;
        this.type = type;
    }

    boolean isAvailable(TimeRange timeRange) {
        for (TimeRange slot : slotBooked) {
            if (!slot.nonOverlap(timeRange)) {
                return false;
            }
        }
        return true;
    }
}

class TimeRange {

    long startTime;
    long endTime;

    public TimeRange(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // [2,3] => [1,2]
    boolean nonOverlap(TimeRange timeRange) {
        return timeRange.startTime >= this.endTime || timeRange.endTime <= this.startTime;
    }
}

class Building {

    final Map<String, Room> roomRecord = new HashMap<>();
    String id;

    public Building(String id) {
        this.id = id;
    }

    public void addRoom(Room room) {
        roomRecord.put(room.id, room);
    }
}

class Booking {

    String id;
    long createdAt;

    Booking(
            Employee eid,
            Building building,
            Room room,
            TimeRange timeRange
    ) {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
    }
}

// orchastrator class 
class BookingService {

    private final Map<String, Building> buildingRecord = new HashMap<>();
    private final Map<String, Booking> bookingRecord = new HashMap<>();

    public BookingService() {

    }

    // - find available room
    // - reserve room
    // - cancel room
    public List<Room> searchRoom(String id, TimeRange timeRange) {
        if (!this.buildingRecord.containsKey(id)) {
            throw new IllegalArgumentException("Building id not exists!");
        }

        Building building = this.buildingRecord.get(id);
        List<Room> rooms = new ArrayList<>(building.roomRecord.values());

        List<Room> availableRooms = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isAvailable(timeRange)) {
                availableRooms.add(r);
            }
        }
        return availableRooms;
    }

    public Booking reserveRoom(Employee eid, String bid, String rid, TimeRange timeRange) {
        if (!this.buildingRecord.containsKey(bid)) {
            throw new IllegalArgumentException("Building id is invalid");
        }
        Building building = this.buildingRecord.get(bid);

        if (!building.roomRecord.containsKey(rid)) {
            throw new IllegalArgumentException("Room id is invalid!");
        }

        Room room = building.roomRecord.get(rid);

        if (!room.isAvailable(timeRange)) {
            throw new IllegalArgumentException("slot is unavailable");
        }

        room.slotBooked.add(timeRange);
        Booking booking = new Booking(eid, building, room, timeRange);
        this.bookingRecord.put(booking.id, booking);

        return booking;
    }

    public void cancelBooking(Booking booking) {

    }

    public void addBuilding(Building building) {
        this.buildingRecord.put(building.id, building);
    }

}

public class Main {

    public static void main(String[] args) {

        Employee e1 = new Employee("e1", "alice");
        Employee e2 = new Employee("e2", "bob");

        Room r1 = new Room("r1", RoomType.XL);
        Room r2 = new Room("r2", RoomType.M);

        Building b1 = new Building("b1");
        Building b2 = new Building("b2");

        b1.addRoom(r1);
        b1.addRoom(r2);

        BookingService bookingService = new BookingService();

        bookingService.addBuilding(b1);
        bookingService.addBuilding(b2);

        TimeRange t1 = new TimeRange(1, 2);
        TimeRange t2 = new TimeRange(4, 5);

        List<Room> rooms = bookingService.searchRoom(b1.id, t1);

        if (rooms.isEmpty()) {
            System.out.println("No Room is available");
        }

        Room room = rooms.get(0);

        Booking booking = bookingService.reserveRoom(e1, b1.id, room.id, t2);

        System.out.println("Booking details: " + booking.id);

        Booking booking1 = bookingService.reserveRoom(e2, b1.id, room.id, t1);

        System.out.println("Booking details: " + booking1.id);

        bookingService.cancelBooking(booking);

    }
}
