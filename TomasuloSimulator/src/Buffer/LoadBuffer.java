package Buffer;

import Instruction.Instruction;

public class LoadBuffer {
    private LoadBufferEntry[] buffer;
    private int currentTag;

    public LoadBuffer(int size, int missLatency) {
        buffer = new LoadBufferEntry[size];
        currentTag = 0;
        for (int i = 0; i < size; i++) {
            buffer[i] = new LoadBufferEntry(generateTag(), "", missLatency);
        }
    }

    // Generate a new unique tag for the instruction
    private String generateTag() {
        return "L" + currentTag++;
    }

    // Dispatch load instruction to the load buffer
    public String dispatchInstruction(Instruction instruction) {
        for (LoadBufferEntry entry : buffer) {
            if (!entry.isBusy()) {
                entry.setBusy(true);
                entry.setInstruction(instruction);
                return entry.getTag();
            }
        }
        return null; // No available load buffer entry
    }

    // Method to check for ready load instructions and execute them
    public void checkExecution() {
        for (LoadBufferEntry entry : buffer) {
            if (entry.isBusy()) {
                // Simulate the load operation
                System.out.println("Executing load at address: " + entry.getAddress());
                entry.clear(); // Instruction execution is complete
            }
        }
    }

    // Get the load buffer entries
    public LoadBufferEntry[] getBuffer() {
        return buffer;
    }
    
    public LoadBufferEntry getBuffer(String tag) {
    	 for (LoadBufferEntry entry : buffer) {
    		 if(entry.getTag().equals(tag)) {
    			 return entry;
    		 }
         }
    	 return null;
    }
    
    public boolean hasAvailableSlot(){
        for(LoadBufferEntry entry : buffer){
            if(!entry.isBusy()) return true;
        }
        return false;
    }
    
    public void printState() {
        System.out.println("Load Buffer:");
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != null) {
                System.out.printf("Entry %d -> Tag: %s, Address: %s, Busy: %b%n", 
                                  i, buffer[i].getTag(), buffer[i].getAddress(), buffer[i].isBusy());
            }
        }
    }

    public void updateAfterWriteBack(String tag, Object value) {
        for (LoadBufferEntry entry : buffer) {
            if (entry.isBusy() && entry.getAddress() != null && entry.getAddress().equals(tag)) {
                entry.setAddress(value.toString()); // Replace the tag with the actual address
            }
        }
    }

}
