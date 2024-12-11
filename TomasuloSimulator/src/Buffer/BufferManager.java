package Buffer;

import Instruction.Instruction;

public class BufferManager {
    private LoadBuffer loadBuffer;
    private StoreBuffer storeBuffer;

    public BufferManager(int loadSize, int storeSize, int missLatency) {
        this.loadBuffer = new LoadBuffer(loadSize, missLatency);
        this.storeBuffer = new StoreBuffer(storeSize);
    }

    // Dispatch instructions to the appropriate buffer
    public String dispatchInstruction(Instruction instruction) {
        if (instruction.getOperation().equals("LW") || instruction.getOperation().equals("LD")||
        	instruction.getOperation().equals("L.D") || instruction.getOperation().equals("L.S")) {
        		return loadBuffer.dispatchInstruction(instruction);
            
        } else if (instruction.getOperation().equals("SW") || instruction.getOperation().equals("SD")|| 
        		   instruction.getOperation().equals("S.D") || instruction.getOperation().equals("S.S")) {
           
        	return storeBuffer.dispatchInstruction(instruction);
        }
        return null;
    }

    
    
    // Check the load and store buffers for ready instructions and execute them
    public void executeInstructions() {
        loadBuffer.checkExecution();
        storeBuffer.checkExecution();
    }
    
    public boolean hasAvailableSlots(Instruction instruction) {
        if (instruction.getOperation().toLowerCase().startsWith("l")) {
            return loadBuffer.hasAvailableSlot();
        } else if (instruction.getOperation().toLowerCase().startsWith("s")) {
            return storeBuffer.hasAvailableSlot();
        }
        return false; // Should not happen.
    }
    
    public LoadBufferEntry getLoadBuffer(String tag) {
    	return loadBuffer.getBuffer(tag);
    }
    
    public StoreBufferEntry getStoreBuffer(String tag) {
    	return storeBuffer.getBuffer(tag);
    }
    
    public LoadBuffer getLoadBuffers() {
    	return loadBuffer;
    }
    
    public StoreBuffer getStoreBuffers() {
    	return storeBuffer;
    }
    
    public void printState() {
        loadBuffer.printState();
        storeBuffer.printState();
    }
}
