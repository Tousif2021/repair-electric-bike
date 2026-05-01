package se.kth.iv1350.repairelectricbike.integration;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles access to stored customer data. In a real application this
 * class would communicate with a database; here it keeps a small
 * in-memory list of sample customers.
 */
public class CustomerRegistry {
    private final List<CustomerDTO> customers = new ArrayList<>();

    /**
     * Package-private constructor, called only by {@link RegistryCreator}.
     */
    CustomerRegistry() {
        addSampleCustomers();
    }

    /**
     * Looks up a customer by phone number.
     *
     * @param phoneNumber The phone number to search for.
     * @return The matching {@link CustomerDTO}, or {@code null} if no
     *         customer with that phone number exists.
     */
    public CustomerDTO findCustomer(String phoneNumber) {
        for (CustomerDTO customer : customers) {
            if (customer.getPhoneNumber().equals(phoneNumber)) {
                return customer;
            }
        }
        return null;
    }

    private void addSampleCustomers() {
        customers.add(new CustomerDTO("Tousif Dewan", "tsdewan@kth.se", "070-1234567"));
        customers.add(new CustomerDTO("Andreas Wissel", "andreas12@xyz.com", "072-1234567"));
    }
}
