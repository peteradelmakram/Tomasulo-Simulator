package InstructionParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import Instruction.Instruction;
import InstructionMemory.InstructionMemory;

public class InstructionParser {
    private Queue<Instruction> instructionQueue = new LinkedList<>();
    private InstructionMemory instructionMemory;
    private int currentIndex = 0; // To store instructions sequentially in memory

    
    public InstructionParser(String filePath) throws IOException {
        instructionMemory = new InstructionMemory();
        parseInstructions(filePath);
    }
    // Method to parse a file and load instructions into the queue
    public void parseInstructions(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line.trim());
            }
        }
    }

    // Method to parse a line and create the corresponding Instruction
    private void parseLine(String line) {
        if (line.isEmpty() || line.startsWith("#")) {
            // Skip empty lines or comments
            return;
        }

        // Split the line by one or more spaces/tabs
        String[] parts = line.split("\\s+"); 
        String operation = parts[0];

        // Create instruction object
        Instruction instruction = null;

        // Trim commas from destination and source fields
        if (parts[1].endsWith(",")) {
            parts[1] = parts[1].substring(0, parts[1].length() - 1); // Remove comma from destination/source1
        }
        if (parts.length > 2 && parts[2].endsWith(",")) {
            parts[2] = parts[2].substring(0, parts[2].length() - 1); // Remove comma from source2
        }

        // Handle the different instruction formats
        if (operation.equals("L.D") || operation.equals("S.D") || operation.equals("L.S") || operation.equals("S.S")) {
            // Load/Store instructions (destination, source)
            String destination = parts[1];
            String source = parts.length > 2 ? parts[2] : null; // For operations like L.D  F1, 0(R2)
            instruction = new Instruction(operation, destination, source);
        } else if (operation.equals("ADD.D") || operation.equals("SUB.D") || operation.equals("MUL.D") || operation.equals("DIV.D") ||
                   operation.equals("ADD.S") || operation.equals("SUB.S") || operation.equals("MUL.S") || operation.equals("DIV.S")) {
            // Arithmetic instructions (destination, source1, source2)
            String destination = parts[1];
            String source1 = parts[2];
            String source2 = parts[3];
            instruction = new Instruction(operation, destination, source1, source2);
        } else if (operation.equals("DADDI") || operation.equals("DSUBI")) {
            // DADDI/DSUBI (destination, source1, immediate)
            String destination = parts[1];
            String source1 = parts[2];
            String source2 = parts[3]; // immediate value
            instruction = new Instruction(operation, destination, source1, source2);
        } else if (operation.equals("BNE") || operation.equals("BEQ")) {
            // Branch instructions (source1, source2, label)
            String source1 = parts[1];
            String source2 = parts[2];
            String label = parts[3];
            instruction = new Instruction(operation, source1, source2, label);
        } else if (operation.equals("LW") || operation.equals("LD") || operation.equals("SW") || operation.equals("SD")) {
            // Load/Store instructions (destination, source)
            String destination = parts[1];
            String source = parts.length > 2 ? parts[2] : null;
            instruction = new Instruction(operation, destination, source);
        }

        if (instruction != null) {
            instructionQueue.offer(instruction);
            instructionMemory.loadInstruction(currentIndex, instruction); // Store the instruction at the current index
            currentIndex++; // Move to the next index
        }
    }


    public Queue<Instruction> getInstructionQueue() {
        return instructionQueue;
    }
    
    public InstructionMemory getInstructionMemory() {
        return instructionMemory;
    }

    // Main method for testing
    public static void main(String[] args) {
        
        
    }
}
