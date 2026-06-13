package com.saurav.elevatordesign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

/*
Elevator Design

The building have multiple floors
Request comes from multiple floors: up/down buttons
Inside elevator user select buttons
Elevator has maximum weight and capacity

Extended:
There will be display board that shows which elevator is coming

Elevator
Request
ElevatorController

 */
enum ElevatorState {
    IDLE, MOVING
}

enum Direction {
    UP, DOWN, IDLE
}

enum RequestType {
    PICK_UP, PICK_DOWN
}

class Request {

    Integer floor;
    RequestType type;
    long timeStamp;

    public Request(Integer floor, RequestType type) {
        this.floor = floor;
        this.type = type;
        this.timeStamp = System.currentTimeMillis();
    }

}

class Elevator {

    String id;
    Integer currentFloor;
    Direction direction;
    ElevatorState state;

    PriorityQueue<Integer> upRequests = new PriorityQueue<>(); // -> minHeap: process lowest floor while going up
    PriorityQueue<Integer> downRequests = new PriorityQueue<>(Collections.reverseOrder()); // -> maxHeap: process highest floor while going down

    public Elevator(String id) {
        this.id = id;
        this.currentFloor = 0;
        this.direction = Direction.IDLE;
        this.state = ElevatorState.IDLE;
    }

    public void updateState(ElevatorState _state) {
        this.state = _state;
    }

    public void addRequest(Request request) {
        if (request.floor < 0) {
            throw new IllegalArgumentException("Floor is not valid!");
        }
        if (request.type == RequestType.PICK_UP) {
            upRequests.add(request.floor);
        } else {
            downRequests.add(request.floor);
        }

        if (state == ElevatorState.IDLE) {
            direction = request.type == RequestType.PICK_UP ? Direction.UP : Direction.DOWN;
            state = ElevatorState.MOVING;
        }
    }

    // Call this in a loop until it returns false
    public boolean step() {
        Integer target = nextTarget();
        if (target == null) {
            state = ElevatorState.IDLE;
            direction = Direction.IDLE;
            return false;                       // nothing left to do
        }

        if (Objects.equals(currentFloor, target)) {
            openDoors(target);                  // serve this floor
            pollTarget();                       // remove from queue
            switchDirectionIfNeeded();
            return true;
        }

        // Move one floor toward target
        currentFloor += (target > currentFloor) ? 1 : -1;
        direction = (target > currentFloor) ? Direction.UP : Direction.DOWN;
        System.out.printf("[%s] moving %s -> floor %d%n", id, direction, currentFloor);
        return true;
    }

    public boolean isBusy() {
        return (!upRequests.isEmpty() || !downRequests.isEmpty());
    }

    public Integer requestSize() {
        return upRequests.size() + downRequests.size();
    }

    private Integer nextTarget() {
        if (direction == Direction.UP || direction == Direction.IDLE) {
            if (!upRequests.isEmpty()) {
                return upRequests.peek();
            }
            if (!downRequests.isEmpty()) {
                return downRequests.peek();
            }
        } else {
            if (!downRequests.isEmpty()) {
                return downRequests.peek();
            }
            if (!upRequests.isEmpty()) {
                return upRequests.peek();
            }
        }
        return null;
    }

    private void pollTarget() {
        if (direction == Direction.UP && !upRequests.isEmpty()
                && Objects.equals(upRequests.peek(), currentFloor)) {
            upRequests.poll();
        } else if (direction == Direction.DOWN && !downRequests.isEmpty()
                && Objects.equals(downRequests.peek(), currentFloor)) {
            downRequests.poll();
        } else {
            upRequests.remove(currentFloor);
            downRequests.remove(currentFloor);
        }
    }

    private void switchDirectionIfNeeded() {
        if (direction == Direction.UP && upRequests.isEmpty()
                && !downRequests.isEmpty()) {
            direction = Direction.DOWN;
        }
        if (direction == Direction.DOWN && downRequests.isEmpty()
                && !upRequests.isEmpty()) {
            direction = Direction.UP;
        }
    }

    private void openDoors(int floor) {
        System.out.printf("[%s] *** DOORS OPEN at floor %d ***%n", id, floor);
    }
}

class ElevatorController {

    List<Elevator> elevators;

    public ElevatorController() {
        this.elevators = new ArrayList<>();
    }

    public void requestElevator(Integer floor, RequestType type) {
        Request request = new Request(floor, type);
        Elevator best = findBestElevator(floor, type);
        if (best == null) {
            System.out.println("No Elevator Available!");
        }
        best.addRequest(request);
    }

    private Elevator findBestElevator(int floor, RequestType type) {
        // 1. nearest idle
        // 2. least loaded

        Elevator best = null;
        int minDist = Integer.MAX_VALUE;
        for (Elevator e : elevators) {
            if (e.state == ElevatorState.IDLE) {
                int dist = Math.abs(e.currentFloor - floor);
                if (dist < minDist) {
                    minDist = dist;
                    best = e;
                }
            }
        }
        if (best != null) {
            return best;
        }

        best = elevators.get(0);
        for (Elevator e : elevators) {
            if (e.requestSize() < best.requestSize()) {
                best = e;
            }
        }
        return best;
    }

    // Run simulation until all elevators are idle
    public void run() {
        boolean anyBusy = true;
        while (anyBusy) {
            anyBusy = false;
            for (Elevator e : elevators) {
                if (e.isBusy() || e.step()) {
                    anyBusy = true;
                }
            }
        }
    }
}

public class Main {

    public static void main(String[] args) {

        System.out.println("=== START Main ===");

        Elevator e1 = new Elevator("ele-1");
        Elevator e2 = new Elevator("ele-2");

        ElevatorController controller = new ElevatorController();
        controller.elevators.add(e1);
        controller.elevators.add(e2);

        controller.requestElevator(2, RequestType.PICK_DOWN);
        controller.requestElevator(7, RequestType.PICK_DOWN);
        controller.requestElevator(9, RequestType.PICK_DOWN);

        controller.run();

    }
}
