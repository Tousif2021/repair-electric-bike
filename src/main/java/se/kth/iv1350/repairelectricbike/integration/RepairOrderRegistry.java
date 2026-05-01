package se.kth.iv1350.repairelectricbike.integration;

import java.util.ArrayList;
import java.util.List;
import se.kth.iv1350.repairelectricbike.model.RepairOrder;

/**
 * Handles access to stored repair orders. In a real application this
 * class would communicate with a database; here the orders are kept
 * in an in-memory list.
 */
public class RepairOrderRegistry {
    private final List<RepairOrder> repairOrders = new ArrayList<>();
    private int nextOrderId = 1;

    /**
     * Package-private constructor, called only by {@link RegistryCreator}.
     */
    RepairOrderRegistry() {
    }

    /**
     * Creates a new repair order, stores it, and returns a DTO view of it.
     *
     * @param description Short description of the reported problem.
     * @param customer    The customer owning the bike.
     * @param bike        The bike being brought in.
     * @return A DTO describing the newly created repair order.
     */
    public RepairOrderDTO createRepairOrder(String description, CustomerDTO customer, BikeDTO bike) {
        RepairOrder order = new RepairOrder(nextOrderId++, description, customer, bike, this);
        repairOrders.add(order);
        return order.toDTO();
    }

    /**
     * Returns all repair orders currently stored in the registry.
     *
     * @return An array of DTOs, one per stored order. The array is
     *         empty if no orders exist.
     */
    public RepairOrderDTO[] findAllRepairOrders() {
        RepairOrderDTO[] result = new RepairOrderDTO[repairOrders.size()];
        for (int i = 0; i < repairOrders.size(); i++) {
            result[i] = repairOrders.get(i).toDTO();
        }
        return result;
    }

    /**
     * Looks up the {@link RepairOrder} with the specified identifier.
     *
     * @param orderId The order identifier.
     * @return The matching {@link RepairOrder}, or {@code null} if no
     *         such order exists.
     */
    public RepairOrder findRepairOrder(int orderId) {
        for (RepairOrder order : repairOrders) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }
        return null;
    }

    /**
     * Persists changes made to the specified repair order. Since
     * storage is in-memory and the registry already holds the
     * reference, this implementation is a no-op. The method exists so
     * the model can signal "save" without knowing how storage is
     * implemented.
     *
     * @param repairOrder The order to save.
     */
    public void save(RepairOrder repairOrder) {
        // In-memory storage; the registry already holds the reference.
    }
}
