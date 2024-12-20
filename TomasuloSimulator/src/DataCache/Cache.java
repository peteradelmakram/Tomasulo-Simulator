package DataCache;
import java.util.Arrays;
import DataMemory.Memory;

public class Cache {
	
    public static class CacheBlock {
        boolean valid;
        int tag;
        byte[] data;

        public boolean isValid() {
			return valid;
		}

		public void setValid(boolean valid) {
			this.valid = valid;
		}

		public int getTag() {
			return tag;
		}

		public void setTag(int tag) {
			this.tag = tag;
		}

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

		public CacheBlock(int blockSize) {
            this.valid = false;
            this.tag = -1;
            this.data = new byte[blockSize];
        }
    }
    

    private final int blockSize;
    private final CacheBlock[] cache;
    private final Memory memory;
    private final int cacheSize;

    public Cache(int cacheSize, int blockSize, Memory memory) {
        this.cacheSize = cacheSize;
        this.blockSize = blockSize;
        this.memory = memory;

        int numBlocks = cacheSize / blockSize;
        this.cache = new CacheBlock[numBlocks];
        for (int i = 0; i < numBlocks; i++) {
            cache[i] = new CacheBlock(blockSize);
        }
    }

    public byte[] access(int address) {
        int blockNumber = (address / blockSize) % cache.length;
        int tag = address / cacheSize;

        CacheBlock block = cache[blockNumber];

        if (block.valid && block.tag == tag) {
            // Cache hit
            System.out.println("Cache hit!");
        } else {
            // Cache miss: Load the block from memory
            System.out.println("Cache miss!");
            block.valid = true;
            block.tag = tag;
            int blockStartAddress = (address / blockSize) * blockSize;
            for (int i = 0; i < blockSize; i++) {
                block.data[i] = (byte) memory.readByte(blockStartAddress + i);
            }
        }
        return block.data;
    }
    
    public int getBlockSize() {
		return blockSize;
	}

	public CacheBlock[] getCache() {
		return cache;
	}

	public Memory getMemory() {
		return memory;
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public void displayCache() {
        System.out.println("Cache Contents:");
        for (int i = 0; i < cache.length; i++) {
            CacheBlock block = cache[i];
            System.out.println("Block " + i + ":");
            System.out.println("  Valid: " + block.valid);
            System.out.println("  Tag: " + block.tag);
            System.out.println("  Data: " + Arrays.toString(block.data));
        }
    }
    
	
	public int getWord(int address) {
	    // Get the cache block number
	    int blockNumber = getBlockNumber(address);
	    CacheBlock block = cache[blockNumber];

	    // Get the offset within the block (assuming word size is 4 bytes)
	    int offset = address % blockSize; // This works because the word is 4 bytes and the block is 64 bytes

	    // Ensure the block is valid
	        // Combine bytes into the 4-byte word
	        int word = (block.getData()[offset] & 0xFF)      // First byte (least significant)
	                   | ((block.getData()[offset + 1] & 0xFF) << 8)  // Second byte
	                   | ((block.getData()[offset + 2] & 0xFF) << 16) // Third byte
	                   | ((block.getData()[offset + 3] & 0xFF) << 24); // Fourth byte
	        return word;
	   
	}
	
	public void loadBlockFromMemory(int address) {
		  int blockNumber = getBlockNumber(address);
		   CacheBlock block = cache[blockNumber];
	    for (int i = 0; i < blockSize; i++) {
	        block.data[i] = (byte) memory.readByte(address + i);
	    }

	    block.valid = true;
	    block.tag = address / blockSize;
	}

	
	public long getLong(int address) {
	    // Get the cache block number
	    int blockNumber = getBlockNumber(address);
	    CacheBlock block = cache[blockNumber];

	    // Get the offset within the block (assuming word size is 8 bytes for a long)
	    int offset = address % blockSize;

	    // Ensure the block is valid
	    
	        // Combine bytes into the 8-byte long
	        long value = ((long) (block.getData()[offset] & 0xFF)) |
	                     ((long) (block.getData()[offset + 1] & 0xFF) << 8) |
	                     ((long) (block.getData()[offset + 2] & 0xFF) << 16) |
	                     ((long) (block.getData()[offset + 3] & 0xFF) << 24) |
	                     ((long) (block.getData()[offset + 4] & 0xFF) << 32) |
	                     ((long) (block.getData()[offset + 5] & 0xFF) << 40) |
	                     ((long) (block.getData()[offset + 6] & 0xFF) << 48) |
	                     ((long) (block.getData()[offset + 7] & 0xFF) << 56);

	        return value;
	    
	}
	
	
	public float getSingleFloat(int address) {
	    // Get the cache block number
	    int blockNumber = getBlockNumber(address);
	    CacheBlock block = cache[blockNumber];

	    // Get the offset within the block (assuming word size is 4 bytes)
	    int offset = address % blockSize;

	   
	  
	        // Combine bytes into the 4-byte float (in IEEE 754 format)
	        int bits = (block.getData()[offset] & 0xFF) |
	                   ((block.getData()[offset + 1] & 0xFF) << 8) |
	                   ((block.getData()[offset + 2] & 0xFF) << 16) |
	                   ((block.getData()[offset + 3] & 0xFF) << 24);

	        // Convert the combined bits into a float
	        return Float.intBitsToFloat(bits);
	}

	
	public double getDoubleFloat(int address) {
	    // Get the cache block number
	    int blockNumber = getBlockNumber(address);
	    CacheBlock block = cache[blockNumber];

	    // Get the offset within the block (assuming word size is 8 bytes for a double)
	    int offset = address % blockSize;

	    // Ensure the block is valid
	        // Combine bytes into the 8-byte double (in IEEE 754 format)
	        long bits = ((long) (block.getData()[offset] & 0xFF)) |
	                    ((long) (block.getData()[offset + 1] & 0xFF) << 8) |
	                    ((long) (block.getData()[offset + 2] & 0xFF) << 16) |
	                    ((long) (block.getData()[offset + 3] & 0xFF) << 24) |
	                    ((long) (block.getData()[offset + 4] & 0xFF) << 32) |
	                    ((long) (block.getData()[offset + 5] & 0xFF) << 40) |
	                    ((long) (block.getData()[offset + 6] & 0xFF) << 48) |
	                    ((long) (block.getData()[offset + 7] & 0xFF) << 56);

	        // Convert the combined bits into a double
	        return Double.longBitsToDouble(bits);
	  
	}


    public int getBlockNumber(int address) {
        return (address / blockSize) % (cacheSize / blockSize);
    }

    public int getOffset(int address) {
        return address % blockSize;
    }
    
    public void loadWord(int address) {
        int blockNumber = (address / blockSize) % (cacheSize / blockSize);
        int tag = address / blockSize; // Fix tag calculation

        CacheBlock block = cache[blockNumber];

        if (block.valid && block.tag == tag) {
            System.out.println("Cache hit for word!");
        } else {
            System.out.println("Cache miss for word!");
            block.valid = true;
            block.tag = tag;
            int offset = getOffset(address);

            // Load 4 bytes (for a word) from memory into the cache block starting from the offset
            for (int i = 0; i < 4; i++) {
                block.data[offset + i] = (byte) memory.readByte(address + i);
            }
        }
    }

    public void loadDoubleWord(int address) {
        int blockNumber = (address / blockSize) % (cacheSize / blockSize);
        int tag = address / blockSize; // Fix tag calculation

        CacheBlock block = cache[blockNumber];

        if (block.valid && block.tag == tag) {
            System.out.println("Cache hit for double word!");
        } else {
            System.out.println("Cache miss for double word!");
            block.valid = true;
            block.tag = tag;
            int offset = getOffset(address);

            // Load 8 bytes (for a double word) from memory into the cache block starting from the offset
            for (int i = 0; i < 8; i++) {
                block.data[offset + i] = (byte) memory.readByte(address + i);
            }
        }
    }


    public void loadSingleFloat(int address) {
        int blockNumber = (address / blockSize) % (cacheSize / blockSize);
        int tag = address / blockSize; // Fix tag calculation

        CacheBlock block = cache[blockNumber];

        if (block.valid && block.tag == tag) {
            System.out.println("Cache hit for single float!");
        } else {
            System.out.println("Cache miss for single float!");
            block.valid = true;
            block.tag = tag;
            int offset = getOffset(address);

            // Load 4 bytes (for a single float) from memory into the cache block starting from the offset
            for (int i = 0; i < 4; i++) {
                block.data[offset + i] = (byte) memory.readByte(address + i);
            }
        }
    }


    public void loadDoubleFloat(int address) {
        int blockNumber = (address / blockSize) % (cacheSize / blockSize);
        int tag = address / blockSize; // Fix tag calculation

        CacheBlock block = cache[blockNumber];

        if (block.valid && block.tag == tag) {
            System.out.println("Cache hit for double float!");
        } else {
            System.out.println("Cache miss for double float!");
            block.valid = true;
            block.tag = tag;
            int offset = getOffset(address);
            for (int i = 0; i < 8; i++) {
                block.data[offset + i] = (byte) memory.readByte(address + i);
            }
        }
    }


    
    
    
    
    public static void main(String[] args) {
        // Initialize memory (1 MB)
    	  Memory memory = new Memory(1 << 10); // 1 KB of memory (1024 bytes)
    	    Cache cache = new Cache(1 << 10, 64, memory); // Cache size 1 KB, block size 64 bytes

    	    // Write a double precision value into memory
    	    memory.writeDouble(8, 3123);

    	    // Load the value into the cache
    	    cache.loadWord(8);
    	    


    	    
    	    

    	    // Print the result
    	    System.out.println("The value from the cache is: " );
        
    }
    
    private byte[] intToBytes(int value) {
        return new byte[] {
            (byte) (value),
            (byte) (value >> 8),
            (byte) (value >> 16),
            (byte) (value >> 24)
        };
    }

    private byte[] longToBytes(long value) {
        return new byte[] {
            (byte) (value),
            (byte) (value >> 8),
            (byte) (value >> 16),
            (byte) (value >> 24),
            (byte) (value >> 32),
            (byte) (value >> 40),
            (byte) (value >> 48),
            (byte) (value >> 56)
        };
    }

    
    public boolean isCacheHit(int address) {
        int blockNumber = (address / blockSize) % cache.length; // Calculate block number
        int tag = address / blockSize;                         // Calculate tag for the address

        CacheBlock block = cache[blockNumber];

        // A cache hit occurs if the block is valid and the tag matches
        return block.valid;
    }
    
    public void storeWord(int address, Integer value) {
        int blockNumber = (address / blockSize) % cache.length;
        int tag = address / blockSize;

        CacheBlock block = cache[blockNumber];

       

        // Write the word into the cache
        int offset = getOffset(address);
        byte[] valueBytes = intToBytes(value);

        for (int i = 0; i < 4; i++) {
            block.data[offset + i] = valueBytes[i];
        }

        // Write-through: Update main memory
        for (int i = 0; i < 4; i++) {
            memory.writeByte(address + i, valueBytes[i]);
        }
    }

    public void storeDoubleWord(int address, Long value) {
        int blockNumber = (address / blockSize) % cache.length;
        int tag = address / blockSize;

        CacheBlock block = cache[blockNumber];

        // Write the double word into the cache
        int offset = getOffset(address);
        byte[] valueBytes = longToBytes(value);

        for (int i = 0; i < 8; i++) {
            block.data[offset + i] = valueBytes[i];
        }

        // Write-through: Update main memory
        for (int i = 0; i < 8; i++) {
            memory.writeByte(address + i, valueBytes[i]);
        }
    }

    public void storeSingleFloat(int address, Float value) {
        int blockNumber = (address / blockSize) % cache.length;
        int tag = address / blockSize;

        CacheBlock block = cache[blockNumber];

      

        // Write the single float into the cache
        int offset = getOffset(address);
        byte[] valueBytes = intToBytes(Float.floatToIntBits(value));

        for (int i = 0; i < 4; i++) {
            block.data[offset + i] = valueBytes[i];
        }

        // Write-through: Update main memory
        for (int i = 0; i < 4; i++) {
            memory.writeByte(address + i, valueBytes[i]);
        }
    }

    public void storeDoubleFloat(int address, Double value) {
        int blockNumber = (address / blockSize) % cache.length;
        int tag = address / blockSize;

        CacheBlock block = cache[blockNumber];

     
        // Write the double float into the cache
        int offset = getOffset(address);
        byte[] valueBytes = longToBytes(Double.doubleToLongBits(value));

        for (int i = 0; i < 8; i++) {
            block.data[offset + i] = valueBytes[i];
        }

        // Write-through: Update main memory
        for (int i = 0; i < 8; i++) {
            memory.writeByte(address + i, valueBytes[i]);
        }
    }
    


}
