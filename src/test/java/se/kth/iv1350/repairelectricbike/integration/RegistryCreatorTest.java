package se.kth.iv1350.repairelectricbike.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for the {@link RegistryCreator} class. Ensures the creator
 * exposes usable registries and returns the same instance on repeated
 * calls (so data written through one reference is visible through the
 * next).
 */
public class RegistryCreatorTest {
    private RegistryCreator instance;

    @BeforeEach
    public void setUp() {
        instance = new RegistryCreator();
    }

    @Test
    public void getCustomerRegistryReturnsNonNull() {
        assertNotNull(instance.getCustomerRegistry(),
                "getCustomerRegistry must return a valid registry.");
    }

    @Test
    public void getRepairOrderRegistryReturnsNonNull() {
        assertNotNull(instance.getRepairOrderRegistry(),
                "getRepairOrderRegistry must return a valid registry.");
    }

    @Test
    public void getCustomerRegistryReturnsSameInstanceEachCall() {
        CustomerRegistry first = instance.getCustomerRegistry();
        CustomerRegistry second = instance.getCustomerRegistry();
        assertSame(first, second,
                "Repeated calls should return the same registry instance, "
                        + "so all callers share the same data.");
    }

    @Test
    public void getRepairOrderRegistryReturnsSameInstanceEachCall() {
        RepairOrderRegistry first = instance.getRepairOrderRegistry();
        RepairOrderRegistry second = instance.getRepairOrderRegistry();
        assertSame(first, second,
                "Repeated calls should return the same registry instance.");
    }

    @Test
    public void customerRegistryIsUsableAfterCreation() {
        CustomerDTO customer = instance.getCustomerRegistry().findCustomer("070-1234567");
        assertNotNull(customer,
                "A registry returned by RegistryCreator must be fully initialized.");
    }
}
