package ExecutionTable;

import Instruction.Instruction;

public class ExecutionTableEntry {
    private Instruction instruction;
    private String destination;
    private String source1;
    private String source2;
    private int issueCycle;
    private int startExecutionCycle;
    private int endExecutionCycle;
    private int writeBackCycle;

    public ExecutionTableEntry(Instruction instruction) {
        this.instruction = instruction;
        this.destination = instruction.getDestination();
        this.source1 = instruction.getSource1();
        this.source2 = instruction.getSource2();
        this.issueCycle = -1;
        this.startExecutionCycle = -1;
        this.endExecutionCycle = -1;
        this.writeBackCycle = -1;
    }

    // Getters and Setters for all fields
    public Instruction getInstruction() { return instruction; }
    public void setInstruction(Instruction instruction) { this.instruction = instruction; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getSource1() { return source1; }
    public void setSource1(String source1) { this.source1 = source1; }

    public String getSource2() { return source2; }
    public void setSource2(String source2) { this.source2 = source2; }

    public int getIssueCycle() { return issueCycle; }
    public void setIssueCycle(int issueCycle) { this.issueCycle = issueCycle; }

    public int getStartExecutionCycle() { return startExecutionCycle; }
    public void setStartExecutionCycle(int startExecutionCycle) { this.startExecutionCycle = startExecutionCycle; }

    public int getEndExecutionCycle() { return endExecutionCycle; }
    public void setEndExecutionCycle(int endExecutionCycle) { this.endExecutionCycle = endExecutionCycle; }

    public int getWriteBackCycle() { return writeBackCycle; }
    public void setWriteBackCycle(int writeBackCycle) { this.writeBackCycle = writeBackCycle; }
    public String toString() {
        return "Instruction: " + instruction.getOperation() + ", " +
               "Destination: " + destination + ", " +
               "Source1: " + source1 + ", " +
               "Source2: " + source2 + ", " +
               "IssueCycle: " + issueCycle + ", " +
               "StartExecutionCycle: " + startExecutionCycle + ", " +
               "EndExecutionCycle: " + endExecutionCycle + ", " +
               "WriteBackCycle: " + writeBackCycle;
    }
}
