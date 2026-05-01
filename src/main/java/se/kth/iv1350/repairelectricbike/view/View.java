package se.kth.iv1350.repairelectricbike.view;

import se.kth.iv1350.repairelectricbike.controller.Controller;
import se.kth.iv1350.repairelectricbike.integration.BikeDTO;
import se.kth.iv1350.repairelectricbike.integration.CustomerDTO;
import se.kth.iv1350.repairelectricbike.integration.RepairOrderDTO;
import se.kth.iv1350.repairelectricbike.integration.RepairTaskDTO;
import se.kth.iv1350.repairelectricbike.model.Amount;

/**
 * Placeholder for the real view. Contains hard-coded calls to the
 * controller and prints everything the controller returns.
 */
public class View {
    private final Controller contr;

    /**
     * Creates a new {@link View} connected to the given controller.
     *
     * @param contr The controller to call.
     */
    public View(Controller contr) {
        this.contr = contr;
    }

    /**
     * Runs a full execution: receptionist registers a repair order,
     * technician records diagnostics and tasks, customer accepts the
     * work. The name {@code execution} matches the corresponding
     * message in the Seminar 2 startup sequence diagram.
     */
    public void execution() {
        System.out.println(">> Receptionist looks up customer by phone number 070-1234567");
        CustomerDTO customer = contr.findCustomer("070-1234567");
        System.out.println("Controller returned: " + formatCustomer(customer));

        System.out.println("\n>> Receptionist looks up customer by phone number 000-0000000");
        CustomerDTO missing = contr.findCustomer("000-0000000");
        System.out.println("Controller returned: " + formatCustomer(missing));

        System.out.println("\n>> Receptionist registers a new repair order");
        BikeDTO bike = new BikeDTO("Kawasaki", "Pro Mountain Bike", "0012");
        RepairOrderDTO order = contr.createRepairOrder(
                "Battery will not charge", customer, bike);
        System.out.println("Controller returned: " + formatOrder(order));

        System.out.println("\n>> Receptionist prints the repair order");
        contr.printRepairOrder(order.getOrderId());

        System.out.println(">> Technician retrieves all repair orders");
        RepairOrderDTO[] allOrders = contr.findAllRepairOrders();
        System.out.println("Controller returned " + allOrders.length + " order(s):");
        for (RepairOrderDTO o : allOrders) {
            System.out.println("  - " + formatOrder(o));
        }

        System.out.println("\n>> Technician records diagnostic findings");
        contr.addDiagnosticResult(order.getOrderId(), "Battery cells are dead");
        contr.addDiagnosticResult(order.getOrderId(), "Charger port is loose");

        System.out.println(">> Technician adds repair tasks");
        contr.addRepairTask(order.getOrderId(),
                new RepairTaskDTO("Replace battery pack", new Amount(3500.00)));
        contr.addRepairTask(order.getOrderId(),
                new RepairTaskDTO("Fix charger port", new Amount(450.00)));

        System.out.println(">> Technician updates and finalizes the order");
        contr.updateRepairOrder(order.getOrderId(),
                "Replace battery pack and repair loose charger port");

        System.out.println(">> Final printout before customer decision");
        contr.printRepairOrder(order.getOrderId());

        System.out.println(">> Customer accepts the repair order");
        contr.acceptRepairOrder(order.getOrderId());

        System.out.println(">> Final printout after acceptance");
        contr.printRepairOrder(order.getOrderId());
    }

    private String formatCustomer(CustomerDTO c) {
        if (c == null) {
            return "null (customer not found)";
        }
        return c.getName() + " <" + c.getEmail() + "> " + c.getPhoneNumber();
    }

    private String formatOrder(RepairOrderDTO o) {
        if (o == null) {
            return "null";
        }
        return "Order #" + o.getOrderId() + " [" + o.getState() + "] "
                + o.getDescription() + " (total: " + o.getTotalCost() + ")";
    }
}
