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

	// Constructor
    public ReservationStationEntry(String tag, String op) {
        this.tag = tag;
        this.op = op;
        this.Vj = null;
        this.Vk = null;
        this.Qj = null;
        this.Qk = null;
        this.busy = false;
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
        this.Vj = null;
        this.Vk = null;
        this.Qj = null;
        this.Qk = null;
        this.busy = false;
    }
}
