package Instruction;

public class Instruction {
    private String operation;
    private String destination;
    private String source1;
    private String source2;

    // Constructor for instruction with two sources
    public Instruction(String operation, String destination, String source1, String source2) {
        this.operation = operation;
        this.destination = destination;
        this.source1 = source1;
        this.source2 = source2;
    }

    // Constructor for instruction with one source (e.g., loads or stores)
    public Instruction(String operation, String destination, String source1) {
        this(operation, destination, source1, null);
    }

    public String getOperation() {
        return operation;
    }

    public String getDestination() {
        return destination;
    }

    public String getSource1() {
        return source1;
    }

    public String getSource2() {
        return source2;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "operation='" + operation + '\'' +
                ", destination='" + destination + '\'' +
                ", source1='" + source1 + '\'' +
                ", source2='" + source2 + '\'' +
                '}';
    }
}
