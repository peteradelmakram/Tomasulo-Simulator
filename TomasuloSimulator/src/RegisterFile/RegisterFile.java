package RegisterFile;
import java.util.HashMap;
import java.util.Map;

public class RegisterFile {
    // Inner class to represent a register
    public static class Register {
        private String name; // Name of the register (e.g., R1, F1)
        private String tag;     // Reservation station tag
        private Object value;   // Value of the register (can be Integer or Float)

        public Register() {
            this.tag = null;
            this.value = null;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    // Maps for integer and floating-point registers
    private final Map<String, Register> integerRegisters;
    private final Map<String, Register> floatingPointRegisters;

    // Constructor
    public RegisterFile(int numIntegerRegisters, int numFloatingPointRegisters, int initInteger, float initFloat) {
        integerRegisters = new HashMap<>();
        floatingPointRegisters = new HashMap<>();

        // Initialize integer registers (R0 to R[numIntegerRegisters - 1]) with initInteger and tag "0"
        for (int i = 0; i < numIntegerRegisters; i++) {
            Register reg = new Register();
            reg.setValue(initInteger);
            reg.setTag("0"); // Setting the initial tag
            String regName = "R" + i;
            reg.setName(regName);
            integerRegisters.put(regName, reg);
        }

        // Initialize floating-point registers (F0 to F[numFloatingPointRegisters - 1]) with initFloat and tag "0"
        for (int i = 0; i < numFloatingPointRegisters; i++) {
            Register reg = new Register();
            reg.setValue(initFloat);
            reg.setTag("0"); // Setting the initial tag
            String regName = "F" + i;
            reg.setName(regName);
            floatingPointRegisters.put(regName, reg);
        }
    }



    // Methods to interact with registers
    public Object getRegisterValue(String regName) {
        if (integerRegisters.containsKey(regName)) {
            return integerRegisters.get(regName).getValue();
        } else if (floatingPointRegisters.containsKey(regName)) {
            return floatingPointRegisters.get(regName).getValue();
        } else {
            throw new IllegalArgumentException("Invalid register name: " + regName);
        }
    }

    public void setRegisterValue(String regName, Object value) {
        if (integerRegisters.containsKey(regName)) {
            integerRegisters.get(regName).setValue(value);
        } else if (floatingPointRegisters.containsKey(regName)) {
            floatingPointRegisters.get(regName).setValue(value);
        } else {
            throw new IllegalArgumentException("Invalid register name: " + regName);
        }
    }

    public String getRegisterTag(String regName) {
        if (integerRegisters.containsKey(regName)) {
            return integerRegisters.get(regName).getTag();
        } else if (floatingPointRegisters.containsKey(regName)) {
            return floatingPointRegisters.get(regName).getTag();
        } else {
            throw new IllegalArgumentException("Invalid register name: " + regName);
        }
    }

    public void setRegisterTag(String regName, String tag) {
        if (integerRegisters.containsKey(regName)) {
            integerRegisters.get(regName).setTag(tag);
        } else if (floatingPointRegisters.containsKey(regName)) {
            floatingPointRegisters.get(regName).setTag(tag);
        } else {
            throw new IllegalArgumentException("Invalid register name: " + regName);
        }
    }

    // Debugging method to display the state of the register file
    public void displayRegisterFile() {
        System.out.println("Integer Registers:");
        for (String regName : integerRegisters.keySet()) {
            Register reg = integerRegisters.get(regName);
            System.out.println(regName + ": Value=" + reg.getValue() + ", Tag=" + reg.getTag());
        }

        System.out.println("Floating-Point Registers:");
        for (String regName : floatingPointRegisters.keySet()) {
            Register reg = floatingPointRegisters.get(regName);
            System.out.println(regName + ": Value=" + reg.getValue() + ", Tag=" + reg.getTag());
        }
    }
    
    public void printState() {
        System.out.println("Integer Registers:");
        for (Map.Entry<String, Register> entry : integerRegisters.entrySet()) {
            System.out.printf("%s -> Tag: %s, Value: %s%n", entry.getKey(), entry.getValue().getTag(), entry.getValue().getValue());
        }

        System.out.println("Floating-Point Registers:");
        for (Map.Entry<String, Register> entry : floatingPointRegisters.entrySet()) {
            System.out.printf("%s -> Tag: %s, Value: %s%n", entry.getKey(), entry.getValue().getTag(), entry.getValue().getValue());
        }
    }

    public static void main(String[] args) {
        // Create a register file with 32 integer and 32 floating-point registers
        // Preload integers with 100 and floating-point registers with 1.5
        RegisterFile registerFile = new RegisterFile(32, 32, 100, 1.5f);

        // Display the register file to verify preloaded values
        registerFile.displayRegisterFile();
    }



    public void updateTagAndValue(String tag, Object value) {
        // Update integer registers
        for (Map.Entry<String, Register> entry : integerRegisters.entrySet()) {
            Register reg = entry.getValue();
            if (reg.getTag() != null && reg.getTag().equals(tag)) {
                reg.setValue(value); // Set the new value
                reg.setTag("0"); // Clear the tag (mark as available)
            }
        }

        // Update floating-point registers
        for (Map.Entry<String, Register> entry : floatingPointRegisters.entrySet()) {
            Register reg = entry.getValue();
            if (reg.getTag() != null && reg.getTag().equals(tag)) {
                reg.setValue(value); // Set the new value
                reg.setTag("0"); // Clear the tag (mark as available)
            }
        }
    }

    
    public  Map<String, Register> getIntegerRegisters(){
    	return integerRegisters;
    }
    
    public Map<String, Register> getFloatingPointRegisters(){
    	return floatingPointRegisters;
    }
    

}
