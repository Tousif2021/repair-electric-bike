package se.kth.iv1350.repairelectricbike.startup;

import se.kth.iv1350.repairelectricbike.controller.Controller;
import se.kth.iv1350.repairelectricbike.integration.Printer;
import se.kth.iv1350.repairelectricbike.integration.RegistryCreator;
import se.kth.iv1350.repairelectricbike.view.View;


public class Main {

    /**
     * Application entry point.
     *
     * @param args Command-line arguments, currently unused.
     */
    public static void main(String[] args) {
        RegistryCreator regCreator = new RegistryCreator();
        Printer printer = new Printer();
        Controller controller = new Controller(regCreator, printer);
        View view = new View(controller);
        view.execution();
    }
}
