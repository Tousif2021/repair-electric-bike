package se.kth.iv1350.repairelectricbike.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import se.kth.iv1350.repairelectricbike.integration.BikeDTO;
import se.kth.iv1350.repairelectricbike.integration.CustomerDTO;
import se.kth.iv1350.repairelectricbike.integration.Printer;
import se.kth.iv1350.repairelectricbike.integration.RepairOrderDTO;
import se.kth.iv1350.repairelectricbike.integration.RepairOrderRegistry;
import se.kth.iv1350.repairelectricbike.integration.RepairTaskDTO;

/**
 * Represents one particular repair order for one particular bike.
 * Knows its own state and is responsible for persisting itself by
 * calling {@link RepairOrderRegistry#save(RepairOrder)} after each
 * modification.
 */
public class RepairOrder {
    private final int orderId;
    private String description;
    private final CustomerDTO customer;
    private final BikeDTO bike;
    private final DiagnosticReport diagnosticReport = new DiagnosticReport();
    private final List<RepairTask> repairTasks = new ArrayList<>();
    private OrderState state;
    private final RepairOrderRegistry registry;

    /**
     * Creates a new repair order in state {@link OrderState#CREATED}.
     *
     * @param orderId     Unique order identifier.
     * @param description Short description of the reported problem.
     * @param customer    The owner of the bike.
     * @param bike        The bike being brought in.
     * @param registry    The registry used to persist changes.
     */
    public RepairOrder(int orderId, String description, CustomerDTO customer,
                       BikeDTO bike, RepairOrderRegistry registry) {
        this.orderId = orderId;
        this.description = description;
        this.customer = customer;
        this.bike = bike;
        this.registry = registry;
        this.state = OrderState.CREATED;
    }

    /**
     * Records a diagnostic finding and persists the order.
     *
     * @param finding The diagnostic finding.
     */
    public void addDiagnosticResult(String finding) {
        diagnosticReport.addFinding(finding);
        registry.save(this);
    }

    /**
     * Adds a repair task and persists the order.
     *
     * @param taskDTO The task to add.
     */
    public void addRepairTask(RepairTaskDTO taskDTO) {
        repairTasks.add(new RepairTask(taskDTO));
        registry.save(this);
    }

    /**
     * Updates the description of this order and marks it as ready for
     * customer approval. Persists the change.
     *
     * @param newDescription Updated description of the work to be done.
     */
    public void update(String newDescription) {
        this.description = newDescription;
        this.state = OrderState.READY_FOR_APPROVAL;
        registry.save(this);
    }

    /**
     * Sets the state of this order and persists the change.
     *
     * @param newState The new state.
     */
    public void setState(OrderState newState) {
        this.state = newState;
        registry.save(this);
    }

    /**
     * Prints this repair order using the provided printer.
     *
     * @param printer The printer to use.
     */
    public void printRepairOrder(Printer printer) {
        printer.printRepairOrder(this);
    }

    /**
     * @return The sum of the costs of all repair tasks in this order.
     */
    public Amount getTotalCost() {
        Amount total = new Amount(0);
        for (RepairTask task : repairTasks) {
            total = total.plus(task.getCost());
        }
        return total;
    }

    /**
     * Creates an immutable snapshot of this order's data for transport
     * across layer boundaries.
     *
     * @return A {@link RepairOrderDTO} representing this order.
     */
    public RepairOrderDTO toDTO() {
        return new RepairOrderDTO(orderId, description, state.name(),
                customer, bike, getTotalCost());
    }

    /**
     * @return The order identifier.
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * @return The current description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The customer.
     */
    public CustomerDTO getCustomer() {
        return customer;
    }

    /**
     * @return The bike.
     */
    public BikeDTO getBike() {
        return bike;
    }

    /**
     * @return The current state.
     */
    public OrderState getState() {
        return state;
    }

    /**
     * @return The diagnostic report.
     */
    public DiagnosticReport getDiagnosticReport() {
        return diagnosticReport;
    }

    /**
     * @return An unmodifiable view of the repair tasks.
     */
    public List<RepairTask> getRepairTasks() {
        return Collections.unmodifiableList(repairTasks);
    }
}
