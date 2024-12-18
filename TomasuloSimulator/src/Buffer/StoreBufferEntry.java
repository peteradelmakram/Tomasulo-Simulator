package Buffer;

import Instruction.Instruction;

public class StoreBufferEntry {
    private String tag;   // Unique identifier for the entry
    private String address; // Address of the store instruction
    private boolean busy;  // Whether the store buffer entry is occupied
    private Object value;  // Value to be stored (only relevant if available)
    private String Q;      // Tag of the instruction producing the value (if not ready)
    private int startExecutionCycle;
    private int endExecutionCycle;
    private int executionRemainingCycles;
    private Boolean isExecuting; 
    Instruction instruction;
    private int issueCycle;

    public int getIssueCycle() {
		return issueCycle;
	}



	public void setIssueCycle(int issueCycle) {
		this.issueCycle = issueCycle;
	}



	// Constructor
    public StoreBufferEntry(String tag, String address) {
        this.tag = tag;
        this.address = address;
        this.value = null; // Initially no value is available
        this.Q = null;     // Initially no instruction is producing the value
        this.busy = false;
        this.isExecuting = false;
        this.startExecutionCycle = -1;
        this.endExecutionCycle = -1;
        this.executionRemainingCycles = -1;
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
    public void setTag(String tag) {
		this.tag = tag;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	// Setters and Getters
    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setValue(Object value) {
        this.value = value;
        this.Q = null; // If value is set, we no longer need a producer tag
    }

    public void setQ(String Q) {
        this.Q = Q;
    }

    public String getTag() {
        return tag;
    }

    public String getAddress() {
        return address;
    }

    public Object getValue() {
        return value;
    }

    public String getQ() {
        return Q;
    }

    public void clear() {
        this.busy = false;
        this.instruction = null;
        this.address = "";
        this.issueCycle = -1;
        this.startExecutionCycle = -1;
        this.endExecutionCycle = -1;
        this.isExecuting = false;
        this.Q = null;
    }
    


	public void decrementExecutionRemainingCycles() {
		if(executionRemainingCycles > 0) {
			executionRemainingCycles--;
		}
	}
    
    
}
