/*
You are asked to design a Parking Lot Management System in Java.
The parking lot should support the following requirements:
1. The parking lot has a fixed capacity with separate slots for different vehicle types (e.g., Car, Bike, Truck).
2. When a vehicle enters, the system should:
    Assign the nearest available parking slot that matches the vehicle type.
    Generate a ticket that contains a unique ID, the vehicle details, and the parking slot information.
3. When a vehicle exits, the system should:
    Free the slot.
    Close the ticket and calculate the parking duration (you can assume a fixed rate per hour for simplicity).
4. The system should provide:
    The ability to check available slots for each vehicle type.
    The ability to retrieve active tickets.
 */

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    static void main(){
        System.out.println("Parking Lot Management System");

        int free_car_slot = 1;
        int free_bike_slot = 2;
        int free_truck_slot = 3;

        ParkingLot parkingLot = new ParkingLot(free_car_slot, free_bike_slot, free_truck_slot);

        boolean condition = true;
        Tickets tickets = new Tickets();

        while (condition){
            System.out.println("------------------------------------------");
            System.out.println("1. Vehicle Parking");
            System.out.println("2. Vehicle Exit");
            System.out.println("3. Check Available Slots");
            System.out.println("4. Check Active Tickets");

            var scanner = new Scanner(System.in);
            System.out.print("Enter your choice (1\\2\\3\\4): ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.println("------------------------------------------");

            switch (choice){
                case 1:
                    System.out.println("1. Car\n2. Bike\n3. Truck");
                    System.out.print("Enter your choice (1\\2\\3) : ");
                    int vehicle_choice1 = scanner.nextInt();
                    scanner.nextLine();

                    Vehicle tempVehicle1;

                    switch (vehicle_choice1) {
                        case 1 -> tempVehicle1 = new Car("");
                        case 2 -> tempVehicle1 = new Bike("");
                        case 3 -> tempVehicle1 = new Truck("");
                        default -> {
                            System.out.println("Invalid Choice!!!");
                            continue;
                        }
                    }

                    if (!parkingLot.CheckSlotAvailability(tempVehicle1)) {
                        System.out.println("Sorry, no free slots available for this vehicle type!");
                        break;
                    }
                    else {
                        System.out.print("Enter vehicle number : ");
                        String vehicle_number = scanner.nextLine();
                        Vehicle vehicle;
                        Ticket ticket;

                        vehicle = switch (vehicle_choice1) {
                            case 1 -> new Car(vehicle_number);
                            case 2 -> new Bike(vehicle_number);
                            case 3 -> new Truck(vehicle_number);
                            default -> throw new IllegalArgumentException("Invalid choice");
                        };
                        ticket = new Ticket(vehicle);
                        tickets.AddTicket(ticket);
                        parkingLot.ParkSlot(vehicle);
                    }
                    System.out.println("Vehicle Parked!!!");
                    break;
                case 2:
                    System.out.print("Vehicles with ticket status active");
                    tickets.DisplayVehicles();
                    System.out.print("Enter vehicle number : ");
                    String vehicle_number = scanner.nextLine();

                    Object[] object = tickets.GetActiveTicket(vehicle_number);
                    if (object != null && object[0] != null && object[1] != null) {
                        tickets.CloseTicket((Ticket) object[0]);
                        parkingLot.FreeSlot((Vehicle) object[1]);
                        Ticket ticket = (Ticket) object[0];
                        System.out.println("Ticket details");
                        System.out.println("Vehicle number : " + ticket.vehicle.vehicle_number);
                        System.out.println("In time : " + ticket.in_time);
                        System.out.println("Out time : " + ticket.out_time);
                        System.out.println("Fixed fare : " + ticket.vehicle.ticket_price);
                        System.out.println("Fare for the vehicle : " + ((Vehicle) object[1]).ticket_price);
                        System.out.println("Vehicle Exited, Slot Freed and Ticket Closed!!!");
                    }else{
                        System.out.println("Vehicle not found in the lot!!!");
                    }
                    break;
                case 3:
                    System.out.println("1. Car\n2. Bike\n3. Truck");
                    System.out.print("Enter your choice (1\\2\\3) : ");
                    int vehicle_choice2 = scanner.nextInt();
                    scanner.nextLine();

                    Vehicle tempVehicle2;

                    switch (vehicle_choice2) {
                        case 1 -> tempVehicle2 = new Car("");
                        case 2 -> tempVehicle2 = new Bike("");
                        case 3 -> tempVehicle2 = new Truck("");
                        default -> {
                            System.out.println("Invalid Choice!!!");
                            continue;
                        }
                    }

                    parkingLot.CheckAvailableSlots(tempVehicle2);
                    break;
                case 4:
                    tickets.ActiveTickets();
                    break;
                default:
                    System.out.println("Invalid Choice!!!");
                    continue;
            }
            System.out.print("Do you want to continue (Y / N) : ");
            condition = scanner.nextLine().equals("Y");
            if (!condition){
                System.out.println("Exiting");
            }
        }
    }
}


class ParkingLot{
    int num_cars;
    int num_bikes;
    int num_trucks;
    int occupied_car_slot_count = 0;
    int occupied_bike_slot_count = 0;
    int occupied_truck_slot_count = 0;
    char[] car_slot;
    char[] bike_slot;
    char[] truck_slot;

    public ParkingLot(int num_cars, int num_bikes, int num_trucks){
        this.num_cars = num_cars;
        this.num_bikes = num_bikes;
        this.num_trucks = num_trucks;
        this.car_slot = new char[num_cars];
        this.bike_slot = new char[num_bikes];
        this.truck_slot = new char[num_trucks];

        for (int i = 0; i < num_cars; i++) car_slot[i] = 'Y';
        for (int i = 0; i < num_bikes; i++) bike_slot[i] = 'Y';
        for (int i = 0; i < num_trucks; i++) truck_slot[i] = 'Y';
    }

    boolean CheckSlotAvailability(Vehicle vehicle){
        if (vehicle.getClass() == Car.class){
            return occupied_car_slot_count < num_cars;
        }
        else if (vehicle.getClass() == Bike.class){
            return occupied_bike_slot_count < num_bikes;
        }
        else if (vehicle.getClass() == Truck.class){
            return occupied_truck_slot_count < num_trucks;
        }
        return false;
    }

    void ParkSlot(Vehicle vehicle){
        if (vehicle.getClass() == Car.class){
            for (int i = 0; i < num_cars; i++){
                if (car_slot[i] == 'Y'){
                    car_slot[i] = 'N';
                    vehicle.slot_number = i;
                    break;
                }
            }
            occupied_car_slot_count += 1;
        }
        else if (vehicle.getClass() == Bike.class){
            for (int i = 0; i < num_bikes; i++){
                if (bike_slot[i] == 'Y'){
                    bike_slot[i] = 'N';
                    vehicle.slot_number = i;
                    break;
                }
            }
            occupied_bike_slot_count += 1;
        }
        else if (vehicle.getClass() == Truck.class){
            for (int i = 0; i < num_trucks; i++){
                if (truck_slot[i] == 'Y'){
                    truck_slot[i] = 'N';
                    vehicle.slot_number = i;
                    break;
                }
            }
            occupied_truck_slot_count += 1;
        }
    }

    void FreeSlot(Vehicle vehicle){
        if (vehicle.getClass() == Car.class){
            car_slot[vehicle.slot_number] = 'Y';
            occupied_car_slot_count -= 1;
        }
        else if (vehicle.getClass() == Bike.class){
            bike_slot[vehicle.slot_number] = 'Y';
            occupied_bike_slot_count -= 1;
        }
        else if (vehicle.getClass() == Truck.class){
            truck_slot[vehicle.slot_number] = 'Y';
            occupied_truck_slot_count -= 1;
        }
    }

    void CheckAvailableSlots(Vehicle vehicle){
        if (vehicle.getClass() == Car.class) {
            for (int i = 0; i < num_cars; i++) {
                if (car_slot[i] == 'Y') {
                    System.out.println("The car slot" + " " + (i + 1) + " " + "is free");
                } else {
                    System.out.println("The car slot" + " " + (i + 1) + " " + "is occupied");
                }
            }
        }
        else if (vehicle.getClass() == Bike.class) {
            for (int i = 0; i < num_bikes; i++) {
                if (bike_slot[i] == 'Y') {
                    System.out.println("The bike slot" + " " + (i + 1) + " " + "is free");
                } else {
                    System.out.println("The bike slot" + " " + (i + 1) + " " + "is occupied");
                }
            }
        }
        else if (vehicle.getClass() == Truck.class) {
            for (int i = 0; i < num_trucks; i++) {
                if (truck_slot[i] == 'Y') {
                    System.out.println("The truck slot" + " " + (i + 1) + " " + "is free");
                } else {
                    System.out.println("The truck slot" + " " + (i + 1) + " " + "is occupied");
                }
            }
        }
    }
}


class Vehicle{
    String vehicle_number;
    int slot_number;
    float ticket_price;

    public Vehicle(String vehicle_number, float ticket_price){
        this.vehicle_number = vehicle_number;
        this.ticket_price = ticket_price;
    }
}


class Car extends Vehicle{
    private static final float TICKET_PRICE = 25.0f;

    public Car(String vehicle_number) {
        super(vehicle_number, TICKET_PRICE);
    }
}


class Bike extends Vehicle{
    private static final float TICKET_PRICE = 10.5f;

    public Bike(String vehicle_number) {
        super(vehicle_number, TICKET_PRICE);
    }
}


class Truck extends Vehicle{
    private static final float TICKET_PRICE = 50.0f;

    public Truck(String vehicle_number) {
        super(vehicle_number, TICKET_PRICE);
    }
}


class Ticket {
    static ArrayList<Integer> collection_ticket_id = new ArrayList<>();
    int ticket_id;
    Vehicle vehicle;
    LocalDateTime in_time;
    LocalDateTime out_time;
    float ticket_price = 0;
    String status;

    public Ticket(Vehicle vehicle) {
        if (collection_ticket_id.isEmpty()) {
            collection_ticket_id.add(100);
        }

        this.ticket_id = collection_ticket_id.getLast() + 1;
        collection_ticket_id.add(this.ticket_id);

        this.vehicle = vehicle;
        this.in_time = LocalDateTime.now();
        this.status = "Active";
    }
}


class Tickets{
    ArrayList<Ticket> tickets = new ArrayList<>();

    public void AddTicket(Ticket ticket){
        tickets.add(ticket);
    }

    public void CloseTicket(Ticket ticket){
        for (Ticket t : this.tickets){
            if (t.equals(ticket)){
                t.out_time = LocalDateTime.now();
                t.ticket_price = Duration.between(t.in_time, t.out_time).toHours() * t.vehicle.ticket_price;
                t.status = "Closed";
                break;
            }
        }
    }

    public Object[] GetActiveTicket(String vehicle_number){
        for (Ticket t : this.tickets){
            if (t.vehicle.vehicle_number.equals(vehicle_number)){
                return new Object[]{ t, t.vehicle };
            }
        }
        return null;
    }

    public void ActiveTickets(){
        boolean found = false;

        for (Ticket t : this.tickets) {
            if (t.status.equals("Active")) {
                System.out.println(t.ticket_id + " --- " + t.vehicle.vehicle_number + " --- " + t.in_time.toLocalTime());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No Active Tickets!!!");
        }
    }

    public void DisplayVehicles(){
        for (Ticket t: this.tickets){
            if (t.status.equals("Active")){
                System.out.println(t.vehicle.vehicle_number);
            }
        }
    }
}