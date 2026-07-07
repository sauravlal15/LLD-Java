package com.saurav.lld.mockinterview3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point for mock-interview-3. Add domain types in this package (or
 * subpackages), not in the default package.
 *
 *
 *

 *  *Implement a basic system for a workplace social platform where employees can
 * give recognition feedback to each other. Each feedback event records:

 *  *fromEmployee: employee giving the feedback

 *  *toEmployee: employee receiving the feedback

 *  *message: short feedback message, up to 200 characters

 *  *Employees can give feedback multiple times to the same employee or to
 * different employees. Employee names are assumed to be unique and
 * case-sensitive. Feedback is only between two employees at a time; no group
 * feedback.

 *  *Implement:

 *  *1. Records a feedback event from one employee to another.

 *  *2. Get all feedback messages received by the specified employee, in the order
 * they were received.

 *  *3. Returns a summary of feedback interaction between two employees, with:

 *  *fromToCount: number of feedback events from fromEmployee to toEmployee

 *  *totalBetweenCount: total number of feedback events exchanged between the two
 * employees in either direction
 *

 *  *user actions / functional req: 1: Records a feedback event from one employee
 * to another. 2: Get all feedback messages received by the specified employee,
 * in the order they were received. 3: Returns a summary of feedback interaction
 * between two employees, with: fromToCount: number of feedback events from
 * fromEmployee to toEmployee totalBetweenCount: total number of feedback events
 * exchanged between the two employees in either direction

 *  *core functions:

 *  *recordFeedback(String fromEmpId, String toEmpId, String message) => boolean

 *  *getFeedback(String EmployeeID) => List<Feedback>
 *
feedbackSummary(String fromEmpId, String toEmpID) => [fromtToCount,
 * totalBetweencount]
 *

 *  *Models: WorkPlace Employee Message

 *  *Service Layer: WorkplaceService

 *  *StoreLayer: HashMap => for stroring records
 *
 *

 *  *Managers should be able to view feedback received by employees they manage,
 * in addition to their own feedback. Regular employees should only be able to
 * view their own feedback.

 *  *Implement these additional functions:

 *  *1. Assigns a direct manager to an employee: assignManager

 *  *2. Returns all feedback the viewer is allowed to see: getVisibleFeedback
 *
 */
class WorkPlace {

    String id;
    String name;

    public WorkPlace(
            String id,
            String name
    ) {
        this.id = id;
        this.name = name;
    }

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

class Message {

    String fid; // from employee who sent message
    String tid; // to employee who received message
    String id;
    String msg;

    public Message(
            String id,
            String msg
    ) {
        this.id = id;
        this.msg = msg;
    }
}

class WorkPlaceService {

    public Map<String, WorkPlace> workplaceRecord = new HashMap<>();
    public Map<String, Employee> employeeRecord = new HashMap<>();
    public Map<String, Map<String, Message>> toEmpMessageRecord = new HashMap<>();

    WorkPlaceService() {

    }

    public boolean recordFeedback(String fid, String tid, String message) {
        if (!this.employeeRecord.containsKey(fid)) {
            throw new IllegalArgumentException("from Employee Not exists");
        }

        if (!this.employeeRecord.containsKey(tid)) {
            throw new IllegalArgumentException("to Employee Not exists");
        }
        Map<String, Message> toEmployeemsg = new HashMap<>();

        Message msg = new Message(messageid, message);
        tmsg.put(fid, message);

        toEmpMessageRecord.put(tid, toEmployeemsg); // {toEmpMessage1: [{fromEid1, Message}, {fromEid2, Message}]}

    }

    public List<String, Message> getFeedback(String empId) {
        // emp is valid
        if (!this.employeeRecord.containsKey(empId)) {
            throw new IllegalArgumentException("Employee Not exists");
        }

        // fetch all message recevied to that employee
    }

    // ideally addEmployee should be in Employee Service class
    public boolean addEmployee(String wid, String eid, String ename) {
        employeeRecord.put(eid, new Employee(eid, ename));
        return true;
    }

    public boolean addWorkplace(String id, String name) {
        this.workplaceRecord.put(id, new WorkPlace(id, name));
        return true;
    }
}

public class Main {

    public static void main(String[] args) {
        System.out.println("mock-interview-3 ready.");

        WorkPlaceService workplaceService = new WorkPlaceService();

        workplaceService.addWorkplace("w1-id", "workplace1");

        workplaceService.addEmployee('w1-id'
        , "e1-id", "alice");
        workplaceService.addEmployee('w1-id'
        , "e2-id", "bob");
        workplaceService.addEmployee('w1-id'


    , "e3-id", "john");


        
    }
}

/*
f1id => {tid, msg}
tid => [{f1-id, msg}, {f2-id, msg}]


*/
