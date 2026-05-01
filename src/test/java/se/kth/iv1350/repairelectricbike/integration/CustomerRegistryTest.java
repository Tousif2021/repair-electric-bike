package se.kth.iv1350.repairelectricbike.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for the {@link CustomerRegistry} class. Verifies customer
 * lookup by phone number including the not-found path.
 */
public class CustomerRegistryTest {
    private CustomerRegistry instance;

    @BeforeEach
    public void setUp() {
        // The RegistryCreator is the only way to instantiate the
        // registry since its constructor is package-private.
        instance = new RegistryCreator().getCustomerRegistry();
    }

    @Test
    public void findCustomerReturnsCustomerWhenNumberExists() {
        CustomerDTO result = instance.findCustomer("070-1234567");
        assertNotNull(result,
                "findCustomer should return a customer for a known number.");
        assertEquals("Tousif Dewan", result.getName(),
                "The correct customer should be returned.");
        assertEquals("070-1234567", result.getPhoneNumber());
    }

    @Test
    public void findCustomerReturnsNullWhenNumberDoesNotExist() {
        CustomerDTO result = instance.findCustomer("000-0000000");
        assertNull(result,
                "findCustomer should return null when the phone number is unknown.");
    }

    @Test
    public void findCustomerMatchesExactlyOnPhoneNumber() {
        CustomerDTO exact = instance.findCustomer("070-1234567");
        CustomerDTO almost = instance.findCustomer("070-1234568");
        assertNotNull(exact, "Exact match should be found.");
        assertNull(almost, "Near-match should NOT be returned.");
    }

    @Test
    public void findCustomerCanLocateSecondSampleCustomer() {
        CustomerDTO result = instance.findCustomer("072-1234567");
        assertNotNull(result);
        assertEquals("Andreas Wissel", result.getName());
    }
}
