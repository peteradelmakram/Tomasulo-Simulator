package InstructionMemory;

import Instruction.Instruction;

public class InstructionMemory {
    private Instruction[] instructionMemory;
    private static final int MEMORY_SIZE = 1024; // Define the size of the instruction memory (1024 instructions as an example)

    public InstructionMemory() {
        instructionMemory = new Instruction[MEMORY_SIZE];
    }

    public void loadInstruction(int index, Instruction instruction) {
        if (index >= 0 && index < MEMORY_SIZE) {
            instructionMemory[index] = instruction;
        } else {
            System.out.println("Error: Instruction index out of bounds!");
        }
    }

    public Instruction fetchInstruction(int index) {
        if (index >= 0 && index < MEMORY_SIZE) {
            return instructionMemory[index];
        } else {
            System.out.println("Error: Instruction index out of bounds!");
            return null;
        }
    }

    public boolean hasInstructionAt(int index) {
        return index >= 0 && index < MEMORY_SIZE && instructionMemory[index] != null;
    }

    public Instruction[] getInstructionMemory() {
        return instructionMemory;
    }

    public int getNextIndex(int currentIndex) {
        return currentIndex + 1; // Assuming sequential instructions
    }
    
    public int getSize() {
    	return MEMORY_SIZE;
    }
}
