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
import java.nio.Buffer;
import java.util.LinkedList;
import java.util.Scanner;

import Buffer.BufferManager;
import Buffer.LoadBuffer;
import Buffer.LoadBufferEntry;
import Buffer.StoreBuffer;
import Buffer.StoreBufferEntry;

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

    // Constructor to initialize all components of the simulator
    public TomasuloSimulator(int numIntegerRegisters, int numFloatingPointRegisters, int blockSize, 
                              int addSubtractSize, int multiplyDivideSize, int addLatency, 
                              int subLatency, int mulLatency, int divLatency, 
                              int cacheMissLatency, int loadLatency, int storeLatency,
                              int loadBufferSize, int storeBufferSize) {
        
        try {
			this.parser = new InstructionParser("src/instructions.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

        // Set Program Counter (PC) to 0 initially (first instruction)
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
            	}
                issued = true;

            }

            return issued;
        }
        return false;
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

