package se.kth.iv1350.repairelectricbike.controller;

import se.kth.iv1350.repairelectricbike.integration.BikeDTO;
import se.kth.iv1350.repairelectricbike.integration.CustomerDTO;
import se.kth.iv1350.repairelectricbike.integration.CustomerRegistry;
import se.kth.iv1350.repairelectricbike.integration.Printer;
import se.kth.iv1350.repairelectricbike.integration.RegistryCreator;
import se.kth.iv1350.repairelectricbike.integration.RepairOrderDTO;
import se.kth.iv1350.repairelectricbike.integration.RepairOrderRegistry;
import se.kth.iv1350.repairelectricbike.integration.RepairTaskDTO;
import se.kth.iv1350.repairelectricbike.model.OrderState;
import se.kth.iv1350.repairelectricbike.model.RepairOrder;

/**
 * This is the application's only controller class. All calls from the
 * view to the model pass through here.
 */
public class Controller {
    private final CustomerRegistry customerRegistry;
    private final RepairOrderRegistry repairOrderRegistry;
    private final Printer printer;

    /**
     * Creates a new {@link Controller}.
     *
     * @param regCreator Used to obtain the registries.
     * @param printer    The printer used for repair order printouts.
     */
    public Controller(RegistryCreator regCreator, Printer printer) {
        this.customerRegistry = regCreator.getCustomerRegistry();
        this.repairOrderRegistry = regCreator.getRepairOrderRegistry();
        this.printer = printer;
    }

    /**
     * Looks up a customer by phone number.
     *
     * @param phoneNumber The phone number to search for.
     * @return The matching {@link CustomerDTO}, or {@code null} if not
     *         found.
     */
    public CustomerDTO findCustomer(String phoneNumber) {
        return customerRegistry.findCustomer(phoneNumber);
    }

    /**
     * Creates a new repair order for the specified customer and bike.
     *
     * @param description Short description of the reported problem.
     * @param customer    The customer owning the bike.
     * @param bike        The bike being repaired.
     * @return A DTO describing the newly created repair order.
     */
    public RepairOrderDTO createRepairOrder(String description, CustomerDTO customer, BikeDTO bike) {
        return repairOrderRegistry.createRepairOrder(description, customer, bike);
    }

    /**
     * Prints the repair order with the specified identifier.
     *
     * @param orderId The order to print.
     */
    public void printRepairOrder(int orderId) {
        RepairOrder order = repairOrderRegistry.findRepairOrder(orderId);
        if (order != null) {
            order.printRepairOrder(printer);
        }
    }

    /**
     * Retrieves all repair orders currently stored.
     *
     * @return An array of DTOs, one per stored order.
     */
    public RepairOrderDTO[] findAllRepairOrders() {
        return repairOrderRegistry.findAllRepairOrders();
    }

    /**
     * Records a diagnostic finding on the specified repair order.
     *
     * @param orderId          The order identifier.
     * @param diagnosticResult The finding to record.
     */
    public void addDiagnosticResult(int orderId, String diagnosticResult) {
        RepairOrder order = repairOrderRegistry.findRepairOrder(orderId);
        if (order != null) {
            order.addDiagnosticResult(diagnosticResult);
        }
    }

    /**
     * Adds a repair task to the specified repair order.
     *
     * @param orderId The order identifier.
     * @param task    The task to add.
     */
    public void addRepairTask(int orderId, RepairTaskDTO task) {
        RepairOrder order = repairOrderRegistry.findRepairOrder(orderId);
        if (order != null) {
            order.addRepairTask(task);
        }
    }

    /**
     * Updates the description of the specified repair order and marks
     * it as ready for customer approval.
     *
     * @param orderId        The order identifier.
     * @param newDescription The updated description.
     */
    public void updateRepairOrder(int orderId, String newDescription) {
        RepairOrder order = repairOrderRegistry.findRepairOrder(orderId);
        if (order != null) {
            order.update(newDescription);
        }
    }

    /**
     * Marks the specified repair order as accepted by the customer.
     *
     * @param orderId The order identifier.
     */
    public void acceptRepairOrder(int orderId) {
        RepairOrder order = repairOrderRegistry.findRepairOrder(orderId);
        if (order != null) {
            order.setState(OrderState.ACCEPTED);
        }
    }

    /**
     * Marks the specified repair order as rejected by the customer.
     *
     * @param orderId The order identifier.
     */
    public void rejectRepairOrder(int orderId) {
        RepairOrder order = repairOrderRegistry.findRepairOrder(orderId);
        if (order != null) {
            order.setState(OrderState.REJECTED);
        }
    }
}
