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
    private Object loadedValue; // Store the loaded value
    private boolean readyForWriteBack;
    private boolean stallCyclesResolved;
    private int issueCycle;

    public int getIssueCycle() {
		return issueCycle;
	}



	public void setIssueCycle(int issueCycle) {
		this.issueCycle = issueCycle;
	}

 // Constructor
    public LoadBufferEntry(String tag, String address, int stallCycles ) {
        this.tag = tag;
        this.address = address;
        this.busy = false;
        this.isExecuting = false;
        this.startExecutionCycle = -1;
        this.endExecutionCycle = -1;
        this.executionRemainingCycles = -1;
        this.loadedValue = null;
        this.readyForWriteBack = false;
        this.stallCycles = stallCycles;
    }
    

    public void clear() {
        this.busy = false;
        this.instruction = null;
        this.address = "";
        this.issueCycle = -1;
        this.stallCycles = -1;
        this.startExecutionCycle = -1;
        this.endExecutionCycle = -1;
        this.readyForWriteBack = false;
        this.isExecuting = false;
    }
    
    public Boolean getIsExecuting() {
		return isExecuting;
	}


	public void setIsExecuting(Boolean isExecuting) {
		this.isExecuting = isExecuting;
	}


	public Object getLoadedValue() {
		return loadedValue;
	}


	public void setLoadedValue(Object loadedValue) {
		this.loadedValue = loadedValue;
	}


	public boolean isReadyForWriteBack() {
		return readyForWriteBack;
	}


	public void setReadyForWriteBack(boolean readyForWriteBack) {
		this.readyForWriteBack = readyForWriteBack;
	}


	public int getStallCycles() {
		return stallCycles;
	}


	public void setStallCycles(int stallCycles) {
		this.stallCycles = stallCycles;
	}

	private int stallCycles; // Tracks stall cycles due to cache miss or delays

	


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


	public void decrementExecutionRemainingCycles() {
		  if (executionRemainingCycles > 0) {
	            executionRemainingCycles--;
	        }		
	}
	
	public void incrementStallCycles() {
		this.stallCycles++;
	}

	public void setStallCyclesResolved(boolean b) {
		this.stallCyclesResolved = b;
	}

	public boolean isStallCyclesResolved() {
		return stallCyclesResolved;
	}
}
