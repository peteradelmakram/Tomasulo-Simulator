package ReservationStations;

import Instruction.Instruction;

public class BranchReservationStation {
    private ReservationStationEntry[] stations;
    private int currentTag;

    public BranchReservationStation(int size) {
        stations = new ReservationStationEntry[size];
        currentTag = 0;
        for (int i = 0; i < size; i++) {
            stations[i] = new ReservationStationEntry(generateTag(), "");
        }
    }

    // Generate a new unique tag for the instruction
    private String generateTag() {
        return "B" + currentTag++;
    }

    public ReservationStationEntry getStation(String tag) {
        for (ReservationStationEntry entry : stations) {
            if (entry.getTag().equals(tag)) {
                return entry;
            }
        }
        return null;
    }

    // Dispatch instruction to the reservation station
    public String dispatchInstruction(Instruction instruction) {
        for (ReservationStationEntry entry : stations) {
            if (!entry.isBusy()) {
                entry.setInstruction(instruction);
                entry.setBusy(true);
                entry.setOp(instruction.getOperation());
                return entry.getTag();
            }
        }
        return null;
    }

    public boolean hasAvailableSlot() {
        for (ReservationStationEntry entry : stations) {
            if (!entry.isBusy()) {
                return true;
            }
        }
        return false;
    }

    // Get the reservation stations
    public ReservationStationEntry[] getStations() {
        return stations;
    }

    public void printState() {
        System.out.println("Branch Reservation Station:");
        for (int i = 0; i < stations.length; i++) {
            ReservationStationEntry entry = stations[i];
            System.out.printf("Entry %d -> Tag: %s, Op: %s, Vj: %s, Vk: %s, Qj: %s, Qk: %s, Busy: %b%n",
                              i, entry.getTag(), entry.getOp(), entry.getVj(), entry.getVk(),
                              entry.getQj(), entry.getQk(), entry.isBusy());
        }
    }
}
