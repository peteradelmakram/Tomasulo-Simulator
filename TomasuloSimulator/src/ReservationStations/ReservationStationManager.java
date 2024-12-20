package ReservationStations;

import java.util.List;
import java.util.ArrayList;

import Instruction.Instruction;


public class ReservationStationManager {
    private AdditionReservationStation addSubtractRS;
    private MultiplicationReservationStation multiplyDivideRS;
    private BranchReservationStation branchRS;

    public ReservationStationManager(int addSubtractSize, int multiplyDivideSize, int branchStationSize) {
        this.addSubtractRS = new AdditionReservationStation(addSubtractSize);
        this.multiplyDivideRS = new MultiplicationReservationStation(multiplyDivideSize);
        this.branchRS = new BranchReservationStation(branchStationSize);
    }
    
    // Dispatch instructions to the appropriate reservation station
    public boolean hasAvailableSlotFor(Instruction instruction) {
        if (isAddSubOperation(instruction)) {
            return addSubtractRS.hasAvailableSlot();
        } else if (isMulDivOperation(instruction)) {
            return multiplyDivideRS.hasAvailableSlot();
        }else if(isBranchOperation(instruction)) {
        	return branchRS.hasAvailableSlot();
        }
        return false;
    }
    
    
    public boolean isAddSubOperation(Instruction instruction) {
        return instruction.getOperation().equals("ADD.D") || instruction.getOperation().equals("SUB.D") ||
                instruction.getOperation().equals("DADDI") || instruction.getOperation().equals("DSUBI") ||
                instruction.getOperation().equals("ADD.S") || instruction.getOperation().equals("SUB.S");
    }

    public boolean isMulDivOperation(Instruction instruction) {
        return instruction.getOperation().equals("MUL.D") || instruction.getOperation().equals("DIV.D") ||
                instruction.getOperation().equals("MUL.S") || instruction.getOperation().equals("DIV.S");
    }

    
    public boolean isBranchOperation(Instruction instruction) {
        return instruction.getOperation().startsWith("B"); // Covers BEQ, BNE, etc.
    }
    
    // Dispatch instructions to the appropriate reservation station
    public String issueInstruction(Instruction instruction) {
    	String tag = "";
    	if (isAddSubOperation(instruction)) {
            if (addSubtractRS.hasAvailableSlot()) {
                 tag = addSubtractRS.dispatchInstruction(instruction);
            } else {
                System.out.println("No available slot in Addition/Subtraction reservation station.");
            }
        } else if (isMulDivOperation(instruction)) {
            if (multiplyDivideRS.hasAvailableSlot()) {
                 tag = multiplyDivideRS.dispatchInstruction(instruction);
            } else {
                System.out.println("No available slot in Multiplication/Division reservation station.");
            }
        } else if (isBranchOperation(instruction)) {
            if (branchRS.hasAvailableSlot()) {
                tag = branchRS.dispatchInstruction(instruction);
            } else {
                System.out.println("No available slot in Branch reservation station.");
            }
        }else{
            System.out.println("Unknown instruction type: " + instruction.getOperation());
        }
    	return tag;
    }
    
    public ReservationStationEntry getStation(String tag) {
        if (addSubtractRS.getStation(tag) != null) {
            return addSubtractRS.getStation(tag);
        } else if (multiplyDivideRS.getStation(tag) != null) {
            return multiplyDivideRS.getStation(tag);
        } else {
            return branchRS.getStation(tag);
        }
    }

//
//    public void executeInstructions() {
//        addSubtractRS.checkExecution();
//        multiplyDivideRS.checkExecution();
//        
//    }
//    
    public void printState() {
        addSubtractRS.printState();
        multiplyDivideRS.printState();
        branchRS.printState();
    }
    
    public List<ReservationStationEntry> getAllStations() {
        List<ReservationStationEntry> allStations = new ArrayList<>();
        for (ReservationStationEntry entry : addSubtractRS.getStations()) {
            allStations.add(entry);
        }
        for (ReservationStationEntry entry : multiplyDivideRS.getStations()) {
            allStations.add(entry);
        }
     
        return allStations;
    }
    
    public List<ReservationStationEntry> getBranchStations(){
        List<ReservationStationEntry> allStations = new ArrayList<>();
        for (ReservationStationEntry entry : branchRS.getStations()) {
            allStations.add(entry);
        }
        return allStations;
    }
    
    public List<ReservationStationEntry> getStationsForDisplay(){
        List<ReservationStationEntry> allStations = new ArrayList<>();
        for (ReservationStationEntry entry : addSubtractRS.getStations()) {
            allStations.add(entry);
        }
        for (ReservationStationEntry entry : multiplyDivideRS.getStations()) {
            allStations.add(entry);
        }
        for (ReservationStationEntry entry : branchRS.getStations()) {
            allStations.add(entry);
        }
        return allStations;
    }
    
    

    public void updateAfterWriteBack(String tag, Object value) {
        // Update Addition/Subtraction Reservation Station
        for (ReservationStationEntry entry : addSubtractRS.getStations()) {
            if (entry.getQj() != null && entry.getQj().equals(tag)) {
                entry.setVj(value);
                entry.setQj(null); // Clear dependency
            }
            if (entry.getQk() != null && entry.getQk().equals(tag)) {
                entry.setVk(value);
                entry.setQk(null); // Clear dependency
            }
        }

        // Update Multiplication/Division Reservation Station
        for (ReservationStationEntry entry : multiplyDivideRS.getStations()) {
            if (entry.getQj() != null && entry.getQj().equals(tag)) {
                entry.setVj(value);
                entry.setQj(null); // Clear dependency
            }
            if (entry.getQk() != null && entry.getQk().equals(tag)) {
                entry.setVk(value);
                entry.setQk(null); // Clear dependency
            }
        }
        
        
        // Update Branch Reservation Station
        for (ReservationStationEntry entry : branchRS.getStations()) {
            if (entry.getQj() != null && entry.getQj().equals(tag)) {
                entry.setVj(value);
                entry.setQj(null); // Clear dependency
            }
            if (entry.getQk() != null && entry.getQk().equals(tag)) {
                entry.setVk(value);
                entry.setQk(null); // Clear dependency
            }
        }
    }
}



