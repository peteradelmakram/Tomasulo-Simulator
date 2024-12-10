package Buffer;

import Instruction.Instruction;

public class LoadBufferEntry {
    private String tag;   // Unique identifier for the entry
    private String address; // Address of the load instruction
    private boolean busy;  // Whether the load buffer entry is occupied
    private int startExecutionCycle;
    private int endExecutionCycle;
    private int executionRemainingCycles;
    private Boolean isExecuting; 
    Instruction instruction;
    

	// Constructor
    public LoadBufferEntry(String tag, String address) {
        this.tag = tag;
        this.address = address;
        this.busy = false;
    }


	public Instruction getInstruction() {
		return instruction;
	}


	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}


    public int getStartExecutionCycle() {
		return startExecutionCycle;
	}

	public Boolean isExecuting() {
		return isExecuting;
	}

	public void setExecuting(Boolean isExecuting) {
		this.isExecuting = isExecuting;
	}

	public void setStartExecutionCycle(int startExecutionCycle) {
		this.startExecutionCycle = startExecutionCycle;
	}

	public int getEndExecutionCycle() {
		return endExecutionCycle;
	}

	public void setEndExecutionCycle(int endExecutionCycle) {
		this.endExecutionCycle = endExecutionCycle;
	}

    
    public int getExecutionRemainingCycles() {
		return executionRemainingCycles;
	}


	public void setExecutionRemainingCycles(int executionRemainingCycles) {
		this.executionRemainingCycles = executionRemainingCycles;
	}


	// Setters and Getters
    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTag() {
        return tag;
    }

    public String getAddress() {
        return address;
    }

    public void clear() {
        this.busy = false;
    }

	public void decrementExecutionRemainingCycles() {
		  if (executionRemainingCycles > 0) {
	            executionRemainingCycles--;
	        }		
	}
}
