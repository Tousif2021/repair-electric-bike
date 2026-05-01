package se.kth.iv1350.repairelectricbike.integration;

/**
 * Responsible for instantiating all registries. Hides the existence of
 * individual registry classes from the startup and controller layers.
 */
public class RegistryCreator {
    private final CustomerRegistry customerRegistry = new CustomerRegistry();
    private final RepairOrderRegistry repairOrderRegistry = new RepairOrderRegistry();

    /**
     * @return The {@link CustomerRegistry}.
     */
    public CustomerRegistry getCustomerRegistry() {
        return customerRegistry;
    }

    /**
     * @return The {@link RepairOrderRegistry}.
     */
    public RepairOrderRegistry getRepairOrderRegistry() {
        return repairOrderRegistry;
    }
}
