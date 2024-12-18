package DataMemory;

public class Memory {
	  private final byte[] memory;

	    public Memory(int size) {
	        memory = new byte[size];
	    }

	    public void writeWord(int address, int value) {
	        for (int i = 0; i < 4; i++) {
	            memory[address + i] = (byte) ((value >> (i * 8)) & 0xFF);
	        }
	    }

	    public void writeDouble(int address, long value) {
	        for (int i = 0; i < 8; i++) {
	            memory[address + i] = (byte) ((value >> (i * 8)) & 0xFF);
	        }
	    }
	    
	    public void writeFloat(int address, float value) {
	        int bits = Float.floatToIntBits(value);
	        for (int i = 0; i < 4; i++) {
	            memory[address + i] = (byte) ((bits >> (i * 8)) & 0xFF);
	        }
	    }

	    public void writeDoubleFloat(int address, double value) {
	        long bits = Double.doubleToLongBits(value);
	        for (int i = 0; i < 8; i++) {
	            memory[address + i] = (byte) ((bits >> (i * 8)) & 0xFF);
	        }
	    }
	    
	    
	    public int readByte(int address) {
	        int value = 0;
	        for (int i = 0; i < 4; i++) {
	            value |= (memory[address + i] & 0xFF) << (i * 8);
	        }
	        return value;
	    }

		    public void writeByte(int address, byte value) {
        if (address < 0 || address >= memory.length) {
            throw new IndexOutOfBoundsException("Invalid memory address: " + address);
        }
        memory[address] = value;
    }

}
