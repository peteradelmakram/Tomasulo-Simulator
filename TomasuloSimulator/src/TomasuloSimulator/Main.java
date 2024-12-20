package TomasuloSimulator;

import java.util.Queue;

import ExecutionTable.ExecutionTableEntry;
import Instruction.Instruction;

public class Main {
    public static void main(String[] args) {
    	 TomasuloSimulator simulator = new TomasuloSimulator(
    	            16, // Number of integer registers
    	            16, // Number of floating-point registers
    	            16,  // Cache block size
    	            3,  // Add/Subtract reservation station size
    	            2,  // Multiply/Divide reservation station size
    	            2,  // Add latency
    	            2,  // Subtract latency
    	            4, // Multiply latency
    	            5, // Divide latency
    	            5,  // Cache miss latency
    	            2,  // Load latency
    	            2,  // Store latency
    	            3,  // Load buffer size
    	            3 ,  // Store buffer size
    	            1  // Branch latency
    	        );
    	 	simulator.runSimulator();
    }

//    private static void printState(TomasuloSimulator simulator) {
//        System.out.println("\nReservation Stations:");
//        // You may need a method in ReservationStationManager to print its state
//        simulator.getReservationStations().printState(); 
//
//        System.out.println("\nLoad Buffers:");
//        simulator.getBuffers().getLoadBuffers().printState();
//
//        System.out.println("\nStore Buffers:");
//        simulator.getBuffers().getStoreBuffers().printState();
//
//        System.out.println("\nRegister File:");
//        simulator.getRegisterFile().printState();
//        
//        
//        System.out.println("\nExecution Table:");
//        for (ExecutionTable.ExecutionTableEntry entry : simulator.getExecutionTable()) {
//            System.out.println(entry);
//        }
//    }

}

