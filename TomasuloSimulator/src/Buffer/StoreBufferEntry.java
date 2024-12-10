package Buffer;

public class StoreBufferEntry {
    private String tag;   // Unique identifier for the entry
    private String address; // Address of the store instruction
    private boolean busy;  // Whether the store buffer entry is occupied
    private Object value;  // Value to be stored (only relevant if available)
    private String Q;      // Tag of the instruction producing the value (if not ready)

    // Constructor
    public StoreBufferEntry(String tag, String address) {
        this.tag = tag;
        this.address = address;
        this.value = null; // Initially no value is available
        this.Q = null;     // Initially no instruction is producing the value
        this.busy = false;
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
        this.value = null;
        this.Q = null;
        this.busy = false;
    }
}
