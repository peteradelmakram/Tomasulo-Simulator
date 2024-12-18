package Buffer;

import Instruction.Instruction;

public class StoreBuffer {
    private StoreBufferEntry[] buffer;
    private int currentTag;

    public StoreBuffer(int size) {
        buffer = new StoreBufferEntry[size];
        currentTag = 0;
        for (int i = 0; i < size; i++) {
            buffer[i] = new StoreBufferEntry(generateTag(), "");
        }
    }

    // Generate a new unique tag for the instruction
    private String generateTag() {
        return "S" + currentTag++;
    }

    // Dispatch store instruction to the store buffer
    public String dispatchInstruction(Instruction instruction) {
        for (StoreBufferEntry entry : buffer) {
            if (!entry.isBusy()) {
                entry.setBusy(true);
                entry.setInstruction(instruction);
                return entry.getTag();

            }
        }
        return null; // No available store buffer entry
    }

    // Method to check for ready store instructions and execute them
    public void checkExecution() {
        for (StoreBufferEntry entry : buffer) {
            if (entry.isBusy() && entry.getQ() == null) {
                // If value is ready (Q is null), simulate store operation
                System.out.println("Executing store at address: " + entry.getAddress() + " with value: " + entry.getValue());
                entry.clear(); // Instruction execution is complete
            }
        }
    }

    // Get the store buffer entries
    public StoreBufferEntry[] getBuffer() {
        return buffer;
    }
    
    public StoreBufferEntry getBuffer(String tag) {
    	  for (StoreBufferEntry entry : buffer) {
    		  if(entry.getTag().equals(tag)) {
     			 return entry;
     		 }
          }
          return null; 
    }
    
    
    public boolean hasAvailableSlot(){
        for(StoreBufferEntry entry : buffer){
            if(!entry.isBusy()) return true;
        }
        return false;
    }
    
    public void printState() {
        System.out.println("Store Buffer:");
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != null) {
                System.out.printf("Entry %d -> Tag: %s, Address: %s, Value: %s, Q: %s, Busy: %b%n", 
                                  i, buffer[i].getTag(), buffer[i].getAddress(), buffer[i].getValue(), buffer[i].getQ(), buffer[i].isBusy());
            }
        }
    }

    public void updateAfterWriteBack(String tag, Object value) {
    	 for (StoreBufferEntry entry : buffer) {
             if (entry.isBusy() && entry.getAddress() != null && entry.getQ() != null && entry.getQ().equals(tag)) {
                 entry.setValue(value.toString());
                 entry.setQ(null);// Replace the tag with the actual address
             }
         }		
	}
}
