package se.kth.iv1350.repairelectricbike.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.kth.iv1350.repairelectricbike.integration.BikeDTO;
import se.kth.iv1350.repairelectricbike.integration.CustomerDTO;
import se.kth.iv1350.repairelectricbike.integration.Printer;
import se.kth.iv1350.repairelectricbike.integration.RegistryCreator;
import se.kth.iv1350.repairelectricbike.integration.RepairOrderDTO;
import se.kth.iv1350.repairelectricbike.integration.RepairOrderRegistry;
import se.kth.iv1350.repairelectricbike.integration.RepairTaskDTO;
import se.kth.iv1350.repairelectricbike.model.Amount;
import se.kth.iv1350.repairelectricbike.model.OrderState;
import se.kth.iv1350.repairelectricbike.model.RepairOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Tests for the {@link Controller} class. Each test wires up a fresh
 * {@link RegistryCreator} and {@link Printer} to provide the controller
 * with a clean, isolated environment. The tests cover every system
 * operation in the SSD.
 */
public class ControllerTest {
    private Controller controller;
    private RegistryCreator regCreator;
    private RepairOrderRegistry orderRegistry;
    private BikeDTO sampleBike;

    @BeforeEach
    public void setUp() {
        regCreator = new RegistryCreator();
        orderRegistry = regCreator.getRepairOrderRegistry();
        controller = new Controller(regCreator, new Printer());
        sampleBike = new BikeDTO("Kawasaki", "Pro Mountain Bike", "0001");
    }

    @Test
    public void findCustomerReturnsMatchingCustomer() {
        CustomerDTO result = controller.findCustomer("070-1234567");
        assertNotNull(result, "findCustomer should return a customer for a known number.");
        assertEquals("Tousif Dewan", result.getName());
    }

    @Test
    public void findCustomerReturnsNullForUnknownNumber() {
        CustomerDTO result = controller.findCustomer("000-0000000");
        assertNull(result, "findCustomer should return null for an unknown number.");
    }

    @Test
    public void createRepairOrderReturnsDTOReflectingInput() {
        CustomerDTO customer = controller.findCustomer("070-1234567");
        RepairOrderDTO order = controller.createRepairOrder("Flat tyre", customer, sampleBike);
        assertNotNull(order);
        assertEquals("Flat tyre", order.getDescription());
        assertEquals(customer, order.getCustomer());
        assertEquals(sampleBike, order.getBike());
        assertEquals("CREATED", order.getState());
    }

    @Test
    public void createRepairOrderStoresTheOrderInRegistry() {
        CustomerDTO customer = controller.findCustomer("070-1234567");
        RepairOrderDTO dto = controller.createRepairOrder("Broken bell", customer, sampleBike);
        RepairOrder stored = orderRegistry.findRepairOrder(dto.getOrderId());
        assertNotNull(stored, "The created order should be retrievable from the registry.");
        assertEquals("Broken bell", stored.getDescription());
    }

    @Test
    public void findAllRepairOrdersReturnsAllCreatedOrders() {
        CustomerDTO customer = controller.findCustomer("070-1234567");
        controller.createRepairOrder("First", customer, sampleBike);
        controller.createRepairOrder("Second", customer, sampleBike);
        RepairOrderDTO[] all = controller.findAllRepairOrders();
        assertEquals(2, all.length);
    }

    @Test
    public void findAllRepairOrdersReturnsEmptyArrayWhenNoOrders() {
        RepairOrderDTO[] all = controller.findAllRepairOrders();
        assertEquals(0, all.length,
                "findAllRepairOrders should return an empty array when no orders exist.");
    }

    @Test
    public void addDiagnosticResultRecordsFindingOnCorrectOrder() {
        CustomerDTO customer = controller.findCustomer("070-1234567");
        RepairOrderDTO dto = controller.createRepairOrder("Issue", customer, sampleBike);
        controller.addDiagnosticResult(dto.getOrderId(), "Cable loose");
        RepairOrder stored = orderRegistry.findRepairOrder(dto.getOrderId());
        assertEquals(1, stored.getDiagnosticReport().getFindings().size());
        assertEquals("Cable loose", stored.getDiagnosticReport().getFindings().get(0));
    }

    @Test
    public void addDiagnosticResultOnUnknownIdIsSilentlyIgnored() {
        assertDoesNotThrow(
                () -> controller.addDiagnosticResult(9999, "Ignored"),
                "Calling addDiagnosticResult on an unknown ID should not throw.");
    }

    @Test
    public void addRepairTaskAddsTaskToCorrectOrder() {
        CustomerDTO customer = controller.findCustomer("070-1234567");
        RepairOrderDTO dto = controller.createRepairOrder("Issue", customer, sampleBike);
        RepairTaskDTO task = new RepairTaskDTO("Replace cable", new Amount(150));
        controller.addRepairTask(dto.getOrderId(), task);
        RepairOrder stored = orderRegistry.findRepairOrder(dto.getOrderId());
        assertEquals(1, stored.getRepairTasks().size());
        assertEquals(150.0, stored.getTotalCost().getAmount(), 0.0001);
    }

    @Test
    public void addRepairTaskOnUnknownIdIsSilentlyIgnored() {
        RepairTaskDTO task = new RepairTaskDTO("Ignored", new Amount(1));
        assertDoesNotThrow(
                () -> controller.addRepairTask(9999, task),
                "Calling addRepairTask on an unknown ID should not throw.");
    }

    @Test
    public void updateRepairOrderChangesDescriptionAndState() {
        CustomerDTO customer = controller.findCustomer("070-1234567");
        RepairOrderDTO dto = controller.createRepairOrder("Initial", customer, sampleBike);
        controller.updateRepairOrder(dto.getOrderId(), "Updated description");
        RepairOrder stored = orderRegistry.findRepairOrder(dto.getOrderId());
        assertEquals("Updated description", stored.getDescription());
        assertEquals(OrderState.READY_FOR_APPROVAL, stored.getState(),
                "updateRepairOrder should transition the state to READY_FOR_APPROVAL.");
    }

    @Test
    public void updateRepairOrderOnUnknownIdIsSilentlyIgnored() {
        assertDoesNotThrow(
                () -> controller.updateRepairOrder(9999, "Should be ignored"),
                "Calling updateRepairOrder on an unknown ID should not throw.");
    }

    @Test
    public void printRepairOrderOnUnknownIdIsSilentlyIgnored() {
        assertDoesNotThrow(
                () -> controller.printRepairOrder(9999),
                "Calling printRepairOrder on an unknown ID should not throw.");
    }

    @Test
    public void printRepairOrderOnExistingOrderDoesNotThrow() {
        CustomerDTO customer = controller.findCustomer("070-1234567");
        RepairOrderDTO dto = controller.createRepairOrder("Print me", customer, sampleBike);
        assertDoesNotThrow(
                () -> controller.printRepairOrder(dto.getOrderId()),
                "Calling printRepairOrder on an existing order should not throw. "
                        + "We do not assert on System.out content, since the "
                        + "assignment says output-only methods are not tested.");
    }

    @Test
    public void acceptRepairOrderSetsStateToAccepted() {
        CustomerDTO customer = controller.findCustomer("070-1234567");
        RepairOrderDTO dto = controller.createRepairOrder("Work", customer, sampleBike);
        controller.acceptRepairOrder(dto.getOrderId());
        RepairOrder stored = orderRegistry.findRepairOrder(dto.getOrderId());
        assertEquals(OrderState.ACCEPTED, stored.getState());
    }

    @Test
    public void rejectRepairOrderSetsStateToRejected() {
        CustomerDTO customer = controller.findCustomer("070-1234567");
        RepairOrderDTO dto = controller.createRepairOrder("Work", customer, sampleBike);
        controller.rejectRepairOrder(dto.getOrderId());
        RepairOrder stored = orderRegistry.findRepairOrder(dto.getOrderId());
        assertEquals(OrderState.REJECTED, stored.getState());
    }

    @Test
    public void acceptRepairOrderOnUnknownIdIsSilentlyIgnored() {
        assertDoesNotThrow(
                () -> controller.acceptRepairOrder(9999),
                "Calling acceptRepairOrder on an unknown ID should not throw.");
    }

    @Test
    public void rejectRepairOrderOnUnknownIdIsSilentlyIgnored() {
        assertDoesNotThrow(
                () -> controller.rejectRepairOrder(9999),
                "Calling rejectRepairOrder on an unknown ID should not throw.");
    }

    @Test
    public void fullScenarioEndsInAcceptedStateWithCorrectTotal() {
        // Receptionist
        CustomerDTO customer = controller.findCustomer("070-1234567");
        RepairOrderDTO dto = controller.createRepairOrder(
                "Battery will not charge", customer, sampleBike);

        // Technician
        controller.addDiagnosticResult(dto.getOrderId(), "Battery cells dead");
        controller.addRepairTask(dto.getOrderId(),
                new RepairTaskDTO("Replace battery pack", new Amount(3500)));
        controller.addRepairTask(dto.getOrderId(),
                new RepairTaskDTO("Fix charger port", new Amount(450)));
        controller.updateRepairOrder(dto.getOrderId(), "Replace battery + fix port");

        // Customer accepts
        controller.acceptRepairOrder(dto.getOrderId());

        RepairOrder stored = orderRegistry.findRepairOrder(dto.getOrderId());
        assertEquals(OrderState.ACCEPTED, stored.getState());
        assertEquals(3950.0, stored.getTotalCost().getAmount(), 0.0001);
        assertEquals(1, stored.getDiagnosticReport().getFindings().size());
        assertEquals(2, stored.getRepairTasks().size());
    }
}
