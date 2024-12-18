package ReservationStations;
import Instruction.Instruction;

public class ReservationStationEntry {
    private String tag; // Unique tag for the reservation station entry
    private String op;  // Operation (ADD.D, MUL.D, etc.)
    private Object Vj;  // Value of operand 1 (if ready)
    private Object Vk;  // Value of operand 2 (if ready)
    private String Qj;  // Tag of instruction providing operand 1 (if not ready)
    private String Qk;  // Tag of instruction providing operand 2 (if not ready)
    private boolean busy; // Whether the reservation station is occupied
    private Instruction instruction;
    private int startExec; // Cycle we start executing
    private int endExec; // Cycle we stop executing
    private Boolean isExecuting; // Flag to detect already executing instructions. 
    private int executionRemainingCycles; // track number of cycles needed to finish this instruction. (left most field in the table.)
    private int issueCycle;
    private boolean readyForWriteBack = false;
    private int writeBackCycle = -1;

 // Getter and Setter
 public int getWriteBackCycle() {
     return writeBackCycle;
 }

 public void setWriteBackCycle(int writeBackCycle) {
     this.writeBackCycle = writeBackCycle;
 }

    
	public int getIssueCycle() {
		return issueCycle;
	}


	public void setIssueCycle(int issueCycle) {
		this.issueCycle = issueCycle;
	}

	public boolean isReadyForWriteBack() {
	    return readyForWriteBack;
	}

	public void setReadyForWriteBack(boolean readyForWriteBack) {
	    this.readyForWriteBack = readyForWriteBack;
	}
	// Constructor
    public ReservationStationEntry(String tag, String op) {
        this.tag = tag;
        this.op = op;
        this.Vj = null;
        this.Vk = null;
        this.Qj = null;
        this.Qk = null;
        this.busy = false;
        this.isExecuting = false;
        this.startExec = -1;
        this.endExec = -1;
        this.executionRemainingCycles = -1;
    }

    
    public Boolean getIsExecuting() {
		return isExecuting;
	}

	public void setIsExecuting(Boolean isExecuting) {
		this.isExecuting = isExecuting;
	}

	public int getExecutionRemainingCycles() {
		return executionRemainingCycles;
	}

	public void setExecutionRemainingCycles(int executionRemainingCycles) {
		this.executionRemainingCycles = executionRemainingCycles;
	}

	public Boolean isExecuting() {
		return isExecuting;
	}

	public void setExecuting(Boolean isExecuting) {
		this.isExecuting = isExecuting;
	}

	public int getStartExec() {
		return startExec;
	}

	public void setStartExec(int startExec) {
		this.startExec = startExec;
	}

	public int getEndExec() {
		return endExec;
	}

	public void setEndExec(int endExec) {
		this.endExec = endExec;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public void setVj(Object vj) {
		Vj = vj;
	}

	public void setVk(Object vk) {
		Vk = vk;
	}


    public void setTag(String tag) {
		this.tag = tag;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public void setQj(String qj) {
		Qj = qj;
	}

	public void setQk(String qk) {
		Qk = qk;
	}

	// Setters and Getters
    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setOperands(String Vj, String Vk) {
        this.Vj = Vj;
        this.Vk = Vk;
        this.Qj = null;
        this.Qk = null;
    }

    public void setOperandTags(String Qj, String Qk) {
        this.Qj = Qj;
        this.Qk = Qk;
    }

    public String getTag() {
        return tag;
    }

    public String getOp() {
        return op;
    }

    public Object getVj() {
        return Vj;
    }

    public Object getVk() {
        return Vk;
    }

    public String getQj() {
        return Qj;
    }

    public String getQk() {
        return Qk;
    }

    public void clear() {
        this.busy = false;
        this.op = null;
        this.Vj = null;
        this.Vk = null;
        this.Qj = null;
        this.Qk = null;
        this.isExecuting = false;
        this.startExec = -1;
        this.endExec = -1;
        this.executionRemainingCycles = 0;
        this.readyForWriteBack = false;
        this.writeBackCycle = -1;
    }

    
    public void decrementExecutionRemainingCycles() {
        if (executionRemainingCycles > 0) {
            executionRemainingCycles--;
        }
    }

}
