package TomasuloSimulator;

import Instruction.Instruction;
import InstructionParser.InstructionParser;
import InstructionMemory.InstructionMemory;
import RegisterFile.RegisterFile;
import ReservationStations.ReservationStationEntry;
import ReservationStations.ReservationStationManager;
import DataCache.Cache;
import DataMemory.Memory;
import java.util.Queue;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import Buffer.BufferManager;
import Buffer.LoadBufferEntry;
import Buffer.StoreBufferEntry;
import ExecutionTable.ExecutionTableEntry;

public class TomasuloSimulator {
	
    private Queue<Instruction> instructionQueue;
    private InstructionMemory instructionMemory;
    private RegisterFile registerFile;
    private ReservationStationManager reservationStations;
    private Cache dataCache;
    private Memory dataMemory;
    private int PC; // Program Counter
    private int clockCycle = 0;
    private int addLatency;
    private int subLatency;
    private int mulLatency;
    private int divLatency;
    private int cacheMissLatency;
    private int loadLatency;
    private int storeLatency;
    private InstructionParser parser;
    private BufferManager buffers;
    private List<ExecutionTableEntry> executionTable;

    

    // Constructor to initialize all components of the simulator
    public TomasuloSimulator(int numIntegerRegisters, int numFloatingPointRegisters, int blockSize, 
                              int addSubtractSize, int multiplyDivideSize, int addLatency, 
                              int subLatency, int mulLatency, int divLatency, 
                              int cacheMissLatency, int loadLatency, int storeLatency,
                              int loadBufferSize, int storeBufferSize) {
        
    	// Parse the instructions in the file.
        try {
			this.parser = new InstructionParser("src/instructions.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
  

        // Initialize the instruction queue and memory from the parser
        this.instructionQueue = parser.getInstructionQueue();
        this.instructionMemory = parser.getInstructionMemory();

        // Initialize the RegisterFile, ReservationStations, Memory, and Cache
        this.registerFile = new RegisterFile(numIntegerRegisters, numFloatingPointRegisters, 0, 0.0f);
        this.reservationStations = new ReservationStationManager(addSubtractSize, multiplyDivideSize);
        this.dataMemory = new Memory(1024); // Initialize memory with 1024 bytes
        this.dataCache = new Cache(1024, blockSize, dataMemory); // Initialize cache with 1024 bytes and block size

        // Set PC to last instruction (assuming all instructions are intially loaded in the queue, the only case we'll
        // need to use this is on branches. On Branches, we must fetch the instruction at the address and add it to the queue.
        this.PC = this.instructionMemory.getSize() - 1;

        // Set latencies for various operations
        this.addLatency = addLatency;
        this.subLatency = subLatency;
        this.mulLatency = mulLatency;
        this.divLatency = divLatency;
        this.cacheMissLatency = cacheMissLatency;
        this.loadLatency = loadLatency;
        this.storeLatency = storeLatency;
        this.buffers = new BufferManager(loadBufferSize, storeBufferSize);
    }

    public RegisterFile getRegisterFile() {
		return registerFile;
	}

	public void setRegisterFile(RegisterFile registerFile) {
		this.registerFile = registerFile;
	}

	public ReservationStationManager getReservationStations() {
		return reservationStations;
	}

	public void setReservationStations(ReservationStationManager reservationStations) {
		this.reservationStations = reservationStations;
	}

	public BufferManager getBuffers() {
		return buffers;
	}

	public void setBuffers(BufferManager buffers) {
		this.buffers = buffers;
	}

	// Getter for instruction queue
    public Queue<Instruction> getInstructionQueue() {
        return instructionQueue;
    }

    // Getter for instruction memory
    public InstructionMemory getInstructionMemory() {
        return instructionMemory;
    }

    public int getLatency(String opcode) {
        switch (opcode) {
            case "ADD.D":
            case "ADD.S":
            case "DADDI":
            case "DSUBI":
                return addLatency;

            case "SUB.D":
            case "SUB.S":
                return subLatency;

            case "MUL.D":
            case "MUL.S":
                return mulLatency;

            case "DIV.D":
            case "DIV.S":
                return divLatency;

            case "L.D":
            case "L.S":
                return loadLatency;

            case "S.D":
            case "S.S":
                return storeLatency;

            default:
                throw new IllegalArgumentException("Unknown operation: " + opcode);
        }
    }
    
    public ExecutionTableEntry findExecutionTableEntry(Instruction instruction) {
        for (ExecutionTableEntry entry : executionTable) {
            if (entry.getInstruction() == instruction) {
                return entry;
            }
        }
        // If no entry found for the instruction, you can either throw an exception or return null
        throw new IllegalArgumentException("Execution entry not found for instruction: " + instruction);
    }

    public void MapInstructionToStation(ReservationStationEntry Station, Instruction instruction) {
    	Boolean isDirect = false;
        String operand1 = instruction.getSource1();
        String operand2 = instruction.getSource2();
        String destination = instruction.getDestination();
        
	   	// Check the register file for the tags of the registers.
	 	String TagJ = registerFile.getRegisterTag(operand1);
	 	
	 	if(operand2.matches("\\d+")) {
	 		isDirect = true;
	 	}
	 	
	 	String TagK = "";
	 	if(!isDirect) {
		 	TagK = registerFile.getRegisterTag(operand2);
	 	}
        
        if (TagJ.equals("0") && (TagK.equals("0") || isDirect)) {
     	   Object Vj = registerFile.getRegisterValue(operand1);
     	   
     	   if(!isDirect) {
         	  Object Vk = registerFile.getRegisterValue(operand2);
        	   Station.setVk(Vk);
     	   }else {
     		   Station.setVk(operand2);
     	   }
     	   
     	   Station.setBusy(true);
     	   Station.setOp(instruction.getOperation());
     	   Station.setVj(Vj);
     	   
     	   Station.setQj(null);
     	   Station.setQk(null);
     	   
        } else if(TagJ.equals("0") && !TagK.equals("0")) {
     	   Object Vj = registerFile.getRegisterValue(operand1);
     	   
     	   Station.setBusy(true);
     	   Station.setOp(instruction.getOperation());
     	   Station.setVj(Vj);
     	   Station.setVk(null);
     	   Station.setQj(null);
     	   Station.setQk(TagK);
        }
        else if (!TagJ.equals("0") && (TagK.equals("0") || isDirect)) {
        	  if(!isDirect) {
             	  Object Vk = registerFile.getRegisterValue(operand2);
            	   Station.setVk(Vk);
        	  }else {
        		  Station.setVk(operand2);
        	  }
        	
     	   Station.setBusy(true);
     	   Station.setOp(instruction.getOperation());
     	   Station.setVj(null);
     	   Station.setQj(TagJ);
     	   Station.setQk(null);
        } else {
     	   Station.setBusy(true);
     	   Station.setOp(instruction.getOperation());
     	   Station.setVj(null);
     	   Station.setVk(null);
     	   Station.setQj(TagJ);
     	   Station.setQk(TagK);
     	   
        }
        
        registerFile.setRegisterTag(destination, Station.getTag());
    }
    
    public void MapInstructionToLoadBuffer(LoadBufferEntry buffer, Instruction instruction) {
    	
    	boolean isDirectMapped = false;
    	String source = instruction.getSource1();
    	String destination = instruction.getDestination();
    	
    	// Check if source is numeric or not. If numeric, then direct.
    	if(source.matches("\\d+")) {
    		isDirectMapped = true;
    	}else {
    		isDirectMapped = false;
    	}
    	
    	// If direct mapped, no need to check the register file.
    	if(isDirectMapped) {
    		buffer.setBusy(true);
    		buffer.setAddress(source);
    		
    	// Otherwise, we must check the register file.	
    	}else {
    		buffer.setBusy(true);
    		String tag = registerFile.getRegisterTag(source);
    		
    		if(tag.equals("0")) {
    			String address = (String) registerFile.getRegisterValue(source);
    			buffer.setAddress(address);
    		}else {
    			buffer.setAddress(tag);
    		}
    	}
    	
    	registerFile.setRegisterTag(destination, buffer.getTag());
    	
    	
    }
    
    public void MapInstructionToStoreBuffer(StoreBufferEntry buffer, Instruction instruction) {
    	boolean isDirectMapped = false;
    	
    	// First register is the register we store in memory.
    	String source = instruction.getDestination();
    	
    	// Second register (source 1) is the direct memory address/ or register with memory address. (Direct/Indirect)
    	String destinationAddr = instruction.getSource1();
    	
    	if(destinationAddr.matches("\\d+")) {
    		isDirectMapped = true;
    	}else {
    		isDirectMapped = false;
    	}
    	
    	String Tag = registerFile.getRegisterTag(source);
    	
    	if(Tag.equals("0")) {
    		buffer.setBusy(true);
    		buffer.setValue(registerFile.getRegisterValue(source));
    	}else {
    		buffer.setBusy(true);
    		buffer.setQ(Tag);
    	}
    	
    	if(isDirectMapped) {
    		buffer.setAddress(destinationAddr);
    	}else {
    		String dstTag = registerFile.getRegisterTag(destinationAddr);
    		if(dstTag.equals("0")) {
    			buffer.setAddress((String) registerFile.getRegisterValue(destinationAddr));
    		}else {
    			buffer.setAddress(registerFile.getRegisterTag(destinationAddr));
    		}
    		
    	}
    }
    
    // Method to issue instructions from the instruction queue
    public boolean issueInstructions() {
 	   boolean issued = false;

        // Try to issue the next instruction in the queue
        if (!instructionQueue.isEmpty()) {
            Instruction instruction = instructionQueue.peek();
            
            System.out.println(instruction.getOperation());
            
            // Either an addition or multiplication station to store in reservation station.
            if(reservationStations.isAddSubOperation(instruction) || reservationStations.isMulDivOperation(instruction)) {

                   // Check if there's an available reservation station for this instruction
                   if (reservationStations.hasAvailableSlotFor(instruction)) {
                       // Issue the instruction to the reservation station
                       String instructionTag = reservationStations.issueInstruction(instruction);
                       instructionQueue.poll(); 
                       
                       // Get the station the instruction was issued to.
                	   ReservationStationEntry Station = reservationStations.getStation(instructionTag);
                	   
                	   MapInstructionToStation(Station, instruction);
                	   
                	   ExecutionTableEntry entry = new ExecutionTableEntry(instruction);
                       entry.setIssueCycle(clockCycle);
                       executionTable.add(entry);

                       issued = true;
                   }else {
                	   System.out.print("Error.");
                   }
                   
            }else {
            	if(buffers.hasAvailableSlots(instruction)) {
            		String instructionTag = buffers.dispatchInstruction(instruction);
                    instructionQueue.poll(); 

            		if(instructionTag.startsWith("L")) {
            			LoadBufferEntry lBuffer = buffers.getLoadBuffer(instructionTag);
            			MapInstructionToLoadBuffer(lBuffer, instruction);
            		}else {
            			StoreBufferEntry sBuffer = buffers.getStoreBuffer(instructionTag);
            			MapInstructionToStoreBuffer(sBuffer, instruction);
            		}
            		
            		ExecutionTableEntry entry = new ExecutionTableEntry(instruction);
                    entry.setIssueCycle(clockCycle);
                    executionTable.add(entry);
                    issued = true;

            	}
            	
            	

            }

            return issued;
        }
        return false;
    }
    
    
   
    
    public void executeInstructions() {
    	// Biko , this how the execution should work, you should iterate on the reservation stations, 
    	// if you find a reservation station with both valid Vj & Vk, and busy bits, then you should start
    	// execution. Same should happen for the load buffers, and the store buffers. 
    	// Then, we need another method to handle the specific execution (Functional unit)
    	// so, we execute the method ExecuteInstructions() , it sets the setExecuting boolean to true, 
    	// then we need to call another method FunctionalUnitExecution(), this one should look for reservation,
    	// stations with executing == true. If executing == true, then using the opcode in the reservation station,
    	// you should get the Vj, and Vk, and calculate the result. However, even though you calculate the result, 
    	// The result should technically be invalid until currentClockCycle == (startClockCycle + Latency), 
    	// After which we should start implementing the Write Back stage. 
    	// We'll write back in FIFO order, but we'll check if we have two instructions with ready output, 
    	// then we have to check the FIFO order, then check if one of the results they want to write back is needed by
    	// either any reservation station, load buffer, store buffer, or the register file. If one of them is needed and another isn't, we should write back the one that's needed.
    	
    	
//        for (ReservationStationEntry station : reservationStations.getAllStations()) {
//            if (station.isBusy() && station.getVj() != null && station.getVk() != null) {
//                if (!station.isExecuting()) {
//                    // Start execution
//                    station.setExecuting(true);
//                    station.setExecutionRemainingCycles(getLatency(station.getOp()));
//
//                    // Update execution table
//                    ExecutionTableEntry entry = findExecutionTableEntry(station.getInstruction());
//                    entry.setStartExecutionCycle(clockCycle);
//                } else {
//                    station.decrementExecutionRemainingCycles();
//                    if (station.getExecutionRemainingCycles() == 0) {
//                        // Execution complete
//                        station.clear();
//
//                        // Update execution table
//                        ExecutionTableEntry entry = findExecutionTableEntry(station.getInstruction());
//                        entry.setEndExecutionCycle(clockCycle);
//                    }
//                }
//            }
//        }
        
        
        
    }
    


    
    // Simulate Tomasulo's algorithm execution
    public void runSimulator() {
        while (true) {
            clockCycle++;
            boolean issued = false;

            // Step 1: Issue instructions from the queue to the reservation stations
            issued = issueInstructions();

         

            // Optionally, you can print the current cycle status here for debugging
            // printStatus();
        }
    }
    
  
}

