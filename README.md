# Repair Electric Bike — IV1350 Seminar 3

Java implementation of the **Repair Electric Bike** case study, translating the Seminar 2 design into a working program with unit tests.

**Course:** IV1350 Object-Oriented Design, KTH
**Author:** Tousif Dewan (`tsdewan@kth.se`)

---

## Project Structure

```
repair-electric-bike/
├── pom.xml                        — Maven build file
├── README.md                      — this file
└── src/
    ├── main/java/se/kth/iv1350/repairelectricbike/
    │   ├── startup/
    │   │   └── Main.java          — application entry point
    │   ├── view/
    │   │   └── View.java          — placeholder view with hard-coded calls
    │   ├── controller/
    │   │   └── Controller.java    — only controller, holds all system operations
    │   ├── model/
    │   │   ├── Amount.java
    │   │   ├── DiagnosticReport.java
    │   │   ├── OrderState.java
    │   │   ├── RepairOrder.java
    │   │   └── RepairTask.java
    │   └── integration/
    │       ├── BikeDTO.java
    │       ├── CustomerDTO.java
    │       ├── CustomerRegistry.java
    │       ├── Printer.java
    │       ├── RegistryCreator.java
    │       ├── RepairOrderDTO.java
    │       ├── RepairOrderRegistry.java
    │       └── RepairTaskDTO.java
    └── test/java/se/kth/iv1350/repairelectricbike/
        ├── controller/
        │   └── ControllerTest.java
        ├── model/
        │   ├── AmountTest.java
        │   ├── DiagnosticReportTest.java
        │   ├── RepairOrderTest.java
        │   └── RepairTaskTest.java
        └── integration/
            ├── CustomerRegistryTest.java
            ├── RegistryCreatorTest.java
            └── RepairOrderRegistryTest.java
```

## Prerequisites

- **JDK 21** or higher (Java 25 works too — the `pom.xml` targets Java 21 bytecode).
- **Maven 3.8+** (or use the Maven support built into VS Code / IntelliJ / Eclipse).

Check your versions:
```bash
java -version
mvn -version
```

## Build

From the project root:

```bash
mvn compile
```

## Run

```bash
mvn exec:java
```

Or, without Maven:

```bash
mvn compile
java -cp target/classes se.kth.iv1350.repairelectricbike.startup.Main
```

## Run Unit Tests

```bash
mvn test
```

Maven will download JUnit 5 on first run and execute all test classes. A successful run prints `BUILD SUCCESS` with a test summary at the end.

## Running in VS Code

1. Open the project folder in VS Code.
2. Install the **Extension Pack for Java** if you don't already have it.
3. VS Code auto-detects the `pom.xml` and imports it as a Maven project.
4. Open `Main.java` and click the **▶ Run** link above the `main` method.
5. Open any `*Test.java` file and click the **▶ Run Test** link above the class or above individual `@Test` methods.

## Design Notes

The implementation follows the Seminar 2 design with a five-layer architecture: `startup`, `view`, `controller`, `model`, and `integration`. Key design decisions from Seminar 2 are preserved:

- `RepairOrder` is responsible for its own persistence (`registry.save(this)` after every modification) and its own printout (`printRepairOrder(Printer)`). This avoids a spider-in-the-web Controller.
- DTOs are immutable (all fields `final`, no setters), which is both simpler and safer.
- `RegistryCreator` hides the individual registries from the startup and controller layers.
- Registry constructors are **package-private**, so registries can only be created by `RegistryCreator`.

See the Seminar 3 report for a full discussion.
