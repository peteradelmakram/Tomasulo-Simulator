package ReservationStations;

import Instruction.Instruction;

public class MultiplicationReservationStation {
    private ReservationStationEntry[] stations;
    private int currentTag;

    public MultiplicationReservationStation(int size) {
        stations = new ReservationStationEntry[size];
        currentTag = 0;
        for (int i = 0; i < size; i++) {
            stations[i] = new ReservationStationEntry(generateTag(), "");
        }
    }

    // Generate a new unique tag for the instruction
    private String generateTag() {
        return "M" + currentTag++;
    }
    
    public ReservationStationEntry getStation(String tag) {
    	for (ReservationStationEntry entry : stations) {
    		if(entry.getTag().equals(tag)) {
    			return entry;
    		}
    	}
    	return null;
    }

    // Dispatch instruction to the reservation station
    public String dispatchInstruction(Instruction instruction) {
        for (ReservationStationEntry entry : stations) {
            if (!entry.isBusy()) {
                entry.setBusy(true);
                
                return entry.getTag();
            }
        }
        return null; // No available reservation station
    }

    // Method to check if any instruction in the reservation station is ready to execute
    public void checkExecution() {
        for (ReservationStationEntry entry : stations) {
            if (entry.isBusy() && entry.getVj() != null && entry.getVk() != null) {
                // The instruction can now execute, simulate the operation
                System.out.println("Executing " + entry.getOp() + " with operands " + entry.getVj() + " and " + entry.getVk());
                entry.clear(); // Instruction execution is complete
            }
        }
    }
    
    public boolean hasAvailableSlot() {
        for (ReservationStationEntry entry : stations) {
            if (!entry.isBusy()) {
                return true; // Found an empty slot
            }
        }
        return false; // No available slot
    }

    // Get the reservation stations
    public ReservationStationEntry[] getStations() {
        return stations;
    }
    
    public void printState() {
        System.out.println("Multiplication/Division Reservation Station:");
        for (int i = 0; i < stations.length; i++) {
            ReservationStationEntry entry = stations[i];
            System.out.printf("Entry %d -> Tag: %s, Op: %s, Vj: %s, Vk: %s, Qj: %s, Qk: %s, Busy: %b%n",
                              i, entry.getTag(), entry.getOp(), entry.getVj(), entry.getVk(),
                              entry.getQj(), entry.getQk(), entry.isBusy());
        }
    }
}
