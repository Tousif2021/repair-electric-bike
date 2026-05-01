package se.kth.iv1350.repairelectricbike.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.kth.iv1350.repairelectricbike.model.RepairOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for the {@link RepairOrderRegistry} class. Covers creation,
 * lookup, listing and the no-op save method.
 */
public class RepairOrderRegistryTest {
    private RepairOrderRegistry instance;
    private CustomerDTO customer;
    private BikeDTO bike;

    @BeforeEach
    public void setUp() {
        instance = new RegistryCreator().getRepairOrderRegistry();
        customer = new CustomerDTO("Test", "t@e.com", "070-0000000");
        bike = new BikeDTO("Brand", "Model", "SN-001");
    }

    @Test
    public void newRegistryHasNoOrders() {
        RepairOrderDTO[] orders = instance.findAllRepairOrders();
        assertEquals(0, orders.length,
                "A new registry should contain zero orders.");
    }

    @Test
    public void createRepairOrderReturnsDTOWithMatchingFields() {
        RepairOrderDTO dto = instance.createRepairOrder("Broken brake", customer, bike);
        assertNotNull(dto);
        assertEquals("Broken brake", dto.getDescription());
        assertEquals(customer, dto.getCustomer());
        assertEquals(bike, dto.getBike());
        assertEquals("CREATED", dto.getState(),
                "A freshly created order should be in state CREATED.");
    }

    @Test
    public void createRepairOrderAssignsIncreasingIds() {
        RepairOrderDTO first = instance.createRepairOrder("Order 1", customer, bike);
        RepairOrderDTO second = instance.createRepairOrder("Order 2", customer, bike);
        RepairOrderDTO third = instance.createRepairOrder("Order 3", customer, bike);
        assertEquals(first.getOrderId() + 1, second.getOrderId(),
                "Order IDs should increase by one.");
        assertEquals(second.getOrderId() + 1, third.getOrderId(),
                "Order IDs should increase by one.");
    }

    @Test
    public void findAllRepairOrdersReturnsAllCreatedOrders() {
        instance.createRepairOrder("Order 1", customer, bike);
        instance.createRepairOrder("Order 2", customer, bike);
        RepairOrderDTO[] orders = instance.findAllRepairOrders();
        assertEquals(2, orders.length,
                "findAllRepairOrders should return one DTO per stored order.");
    }

    @Test
    public void findRepairOrderReturnsOrderWithMatchingId() {
        RepairOrderDTO dto = instance.createRepairOrder("Find me", customer, bike);
        RepairOrder found = instance.findRepairOrder(dto.getOrderId());
        assertNotNull(found, "The order should be findable by its ID.");
        assertEquals(dto.getOrderId(), found.getOrderId());
        assertEquals("Find me", found.getDescription());
    }

    @Test
    public void findRepairOrderReturnsNullForUnknownId() {
        instance.createRepairOrder("Exists", customer, bike);
        RepairOrder found = instance.findRepairOrder(9999);
        assertNull(found,
                "findRepairOrder should return null for an unknown ID.");
    }

    @Test
    public void saveDoesNotAlterExistingOrders() {
        RepairOrderDTO dto = instance.createRepairOrder("Save test", customer, bike);
        RepairOrder order = instance.findRepairOrder(dto.getOrderId());
        instance.save(order);
        RepairOrder stillThere = instance.findRepairOrder(dto.getOrderId());
        assertNotNull(stillThere,
                "save() must not remove or mutate existing orders.");
        assertEquals(order.getOrderId(), stillThere.getOrderId());
    }
}
