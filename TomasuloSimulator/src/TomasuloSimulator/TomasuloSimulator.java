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
import java.util.ArrayList;

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
    private Boolean isFirstMemAccess;
    

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
        this.buffers = new BufferManager(loadBufferSize, storeBufferSize, cacheMissLatency);
        this.executionTable = new ArrayList<ExecutionTableEntry>();
        this.isFirstMemAccess = true;
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
        Station.setIssueCycle(clockCycle);
        Station.setInstruction(instruction);
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
    	
    	buffer.setInstruction(instruction);
    	buffer.setIssueCycle(clockCycle);
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
    		buffer.setAddress(null);
    	}
    	
    	if(isDirectMapped) {
    		buffer.setAddress(destinationAddr);
    	}else {
    		String dstTag = registerFile.getRegisterTag(destinationAddr);
    		if(dstTag.equals("0")) {
    			buffer.setAddress( registerFile.getRegisterValue(destinationAddr) + "");
    		}else {
    			buffer.setAddress(registerFile.getRegisterTag(destinationAddr));
    		}
    		
    	}
    	buffer.setIssueCycle(clockCycle);
    	buffer.setInstruction(instruction);

    }
    
    // Method to issue instructions from the instruction queue
    public boolean issueInstructions() {
 	   boolean issued = false;
        // Try to issue the next instruction in the queue
        if (!instructionQueue.isEmpty()) {
            Instruction instruction = instructionQueue.peek();
      	   System.out.println("Issuing instruction : " + instruction.getOperation());

            
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

            	}else {
            		issued = false;
            	}
            	
            	

            }

            return issued;
        }
        return false;
    }
    
    public void executeInstructions() {
        // Process reservation stations
        for (ReservationStationEntry station : reservationStations.getAllStations()) {
        	
            if (station.isBusy() && station.getVj() != null && station.getVk() != null && station.getEndExec() < 0) {
                if (!station.isExecuting() && station.getIssueCycle() < clockCycle) {
                	
                	
                	// Start execution one cycle after issuing
                    station.setExecuting(true);
                    station.setExecutionRemainingCycles(getLatency(station.getOp()));

                    ExecutionTableEntry entry = findExecutionTableEntry(station.getInstruction());
                    entry.setStartExecutionCycle(clockCycle);
                    
                } else if (station.isExecuting()) {
                    station.decrementExecutionRemainingCycles();

                    if (station.getExecutionRemainingCycles() == 0) {
                        // Execution completed, prepare for write-back
                        station.setExecuting(false);
                        station.setReadyForWriteBack(true);

                        ExecutionTableEntry entry = findExecutionTableEntry(station.getInstruction());
                        entry.setEndExecutionCycle(clockCycle);
                        station.setEndExec(clockCycle);
                    }
                }
            }
        }

        // Process load buffers
        for (LoadBufferEntry loadBuffer : buffers.getLoadBuffers().getBuffer()) {
        	
            if (loadBuffer.isBusy() && loadBuffer.getAddress() != null && loadBuffer.getAddress().matches("\\d+")) {
                int address = Integer.parseInt(loadBuffer.getAddress());
                ExecutionTableEntry entry = findExecutionTableEntry(loadBuffer.getInstruction());

                if (!loadBuffer.isExecuting() && loadBuffer.getIssueCycle() < clockCycle && !loadBuffer.isReadyForWriteBack()) {
                    // Start load execution one cycle after issuing
                    loadBuffer.setExecuting(true);
                    
                    if(isFirstMemAccess) {
                        loadBuffer.setExecutionRemainingCycles(loadLatency + cacheMissLatency);
                    }else {
                    	loadBuffer.setExecutionRemainingCycles(loadLatency);
                    }
                    
                    loadBuffer.setStallCyclesResolved(false);
                    entry.setStartExecutionCycle(clockCycle);
                }
                if (loadBuffer.isExecuting()) {
                    loadBuffer.decrementExecutionRemainingCycles();

                    if (loadBuffer.getExecutionRemainingCycles() == 0) {
                        // Load from cache
                        String operation = loadBuffer.getInstruction().getOperation();
                        Object loadedValue;

                        switch (operation) {
                            case "LW":
                                loadedValue = dataCache.getWord(address);
                                break;
                            case "LD":
                                loadedValue = dataCache.getLong(address);
                                break;
                            case "L.S":
                                loadedValue = dataCache.getSingleFloat(address);
                                break;
                            case "L.D":
                                loadedValue = dataCache.getDoubleFloat(address);
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported load operation: " + operation);
                        }

    
                        loadBuffer.setLoadedValue(loadedValue);
                        loadBuffer.setReadyForWriteBack(true);
                        loadBuffer.setEndExecutionCycle(clockCycle);
                        entry.setEndExecutionCycle(clockCycle);
                        loadBuffer.setIsExecuting(false);

                        // Reset first memory access flag
                        isFirstMemAccess = false;
                    }
                }
            }
        }
        
        // Process store buffers
        for (StoreBufferEntry storeBuffer : buffers.getStoreBuffers().getBuffer()) {
            if (storeBuffer.isBusy() && storeBuffer.getAddress() != null) {
                ExecutionTableEntry entry = findExecutionTableEntry(storeBuffer.getInstruction());

                if (!storeBuffer.isExecuting() && storeBuffer.getIssueCycle() < clockCycle) {
                    // Start store execution one cycle after issuing
                    storeBuffer.setExecuting(true);
                    
                    if(isFirstMemAccess) {
                        storeBuffer.setExecutionRemainingCycles(loadLatency + cacheMissLatency);
                    }else {
                    	storeBuffer.setExecutionRemainingCycles(loadLatency);
                    }
                    
                    entry.setStartExecutionCycle(clockCycle);
                } else if (storeBuffer.isExecuting() && storeBuffer.getValue() != null) {
                    storeBuffer.decrementExecutionRemainingCycles();

                    if (storeBuffer.getExecutionRemainingCycles() == 0) {
                        // Store the value into the cache
                        int address = Integer.parseInt(storeBuffer.getAddress());
                        Object value = storeBuffer.getValue();

                        if (value instanceof Integer) {
                            dataCache.storeWord(address, (Integer) value);
                        } else if (value instanceof Long) {
                            dataCache.storeDoubleWord(address, (Long) value);
                        } else if (value instanceof Float) {
                            dataCache.storeSingleFloat(address, (Float) value);
                        } else if (value instanceof Double) {
                            dataCache.storeDoubleFloat(address, (Double) value);
                        } else {
                           dataCache.storeDoubleFloat(address, 0.0);
                        }
                        
                        storeBuffer.setExecuting(false);
                        storeBuffer.setEndExecutionCycle(clockCycle);
                        entry.setEndExecutionCycle(clockCycle);
                        entry.setWriteBackCycle(clockCycle);
                        storeBuffer.clear();
                    }
                }
            }
        }
    }

    
    public void writeBack() {
    	for (ReservationStationEntry station : reservationStations.getAllStations()) {
    	    if (station.isBusy() && station.isReadyForWriteBack() && station.getEndExec() < clockCycle) {
    	        // Write-back is allowed only one cycle after execution ends
    	        Object result = calculateResult(station);

    	        // Propagate the result to dependent components
    	        String tag = station.getTag();
    	        updateDependents(tag, result);

    	        // Clear the reservation station
    	        station.clear();

    	        // Update the execution table
    	        ExecutionTableEntry entry = findExecutionTableEntry(station.getInstruction());
    	        entry.setWriteBackCycle(clockCycle);

    	        // Only one instruction can write back per cycle
    	        break;
    	    } else if (station.isBusy() && station.isReadyForWriteBack() && station.getWriteBackCycle() == -1) {
    	        // Set the write-back cycle for the instruction
    	        station.setWriteBackCycle(clockCycle);
    	    }
    	}
    	 // Process load buffers
        for (LoadBufferEntry loadBuffer : buffers.getLoadBuffers().getBuffer()) {
            if (loadBuffer.isReadyForWriteBack() && loadBuffer.getEndExecutionCycle() < clockCycle) {
                // Write back the loaded value to the register file
                String tag = loadBuffer.getTag();
                Object loadedValue = loadBuffer.getLoadedValue();
                updateDependents(tag, loadedValue);


                ExecutionTableEntry entry = findExecutionTableEntry(loadBuffer.getInstruction());
                entry.setWriteBackCycle(clockCycle);
                loadBuffer.setReadyForWriteBack(false);
                loadBuffer.clear();

                // Only one instruction can write back per cycle
                break;
            }
        } 	
    }
    

    private Object calculateResult(ReservationStationEntry station) {
        Object Vj = station.getVj();
        Object Vk = station.getVk();
        String operation = station.getOp();

        // Convert Vj to numeric value
        double numericVj = (Vj instanceof String) ? Double.parseDouble((String) Vj) : ((Number) Vj).doubleValue();
        // Convert Vk to numeric value
        double numericVk = (Vk instanceof String) ? Double.parseDouble((String) Vk) : ((Number) Vk).doubleValue();

        switch (operation) {
            case "ADD":
            case "DADDI":
                return (int) numericVj + (int) numericVk;

            case "ADD.D":
                return numericVj + numericVk;
            case "ADD.S":
                return (float) numericVj + (float) numericVk;

            case "SUB":
            case "DSUBI":
                return (int) numericVj - (int) numericVk;

            case "SUB.D":
                return numericVj - numericVk;
            case "SUB.S":
                return (float) numericVj - (float) numericVk;

            case "MUL.S":
                return (float) numericVj * (float) numericVk;
            case "MUL.D":
                return numericVj * numericVk;

            case "DIV.D":
                return numericVj / numericVk;
            case "DIV.S":
                return (float) numericVj / (float) numericVk;

            default:
                throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }


    private void updateDependents(String tag, Object value) {
        // Update register file
        registerFile.updateTagAndValue(tag, value);

        // Update reservation stations
        reservationStations.updateAfterWriteBack(tag, value);

        // Update load buffers
        buffers.getLoadBuffers().updateAfterWriteBack(tag, value);

        // Update store buffers
        buffers.getStoreBuffers().updateAfterWriteBack(tag, value);
    }

    private  void printStatus() {
    	System.out.println("Clock Cycle : " + clockCycle + "---------------------------------------------------------------------------------");
        System.out.println("\nReservation Stations:");
        // You may need a method in ReservationStationManager to print its state
        getReservationStations().printState(); 

        System.out.println("\nLoad Buffers:");
        getBuffers().getLoadBuffers().printState();

        System.out.println("\nStore Buffers:");
        getBuffers().getStoreBuffers().printState();

        System.out.println("\nRegister File:");
        getRegisterFile().printState();
        
        
        System.out.println("\nExecution Table:");
        for (ExecutionTable.ExecutionTableEntry entry : getExecutionTable()) {
            System.out.println(entry);
        }
        
        System.out.println("----------------------------------------------------------------------------------------");
    }
 
    
    // Simulate Tomasulo's algorithm execution
    public void runSimulator() {
      while(!instructionQueue.isEmpty() || hasPendingWriteBacks()) {
            clockCycle++;

            // Step 1: Issue instructions from the queue to the reservation stations
            issueInstructions();

            // Step 2: Execute instructions in reservation stations and buffers
            executeInstructions();

            // Step 3: Write back results to the register file and clear reservation stations
            writeBack();

            // Optionally, print the current cycle status for debugging
            printStatus();
        }
    }

    // Helper method to check if there are instructions still pending write-back
    private boolean hasPendingWriteBacks() {
        for (ExecutionTableEntry entry : executionTable) {
            if (entry.getWriteBackCycle() < 0) {
                return true; // There is at least one instruction pending write-back
            }
        }
        return false; // All instructions have completed write-back
    }

	public List<ExecutionTableEntry> getExecutionTable() {
		return executionTable;
	}
}

