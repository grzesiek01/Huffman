package pl.edu.pw.ee.aisd2025zex5.services.decompress;

import java.io.FileInputStream;
import java.io.IOException;

public class BitInputStream implements AutoCloseable {

    private final FileInputStream inputStream;
    
    private int currentByte;
    private int bitsRemaining;
    
    // NOWE POLE: Licznik wszystkich przeczytanych bitów
    private long totalBitsRead = 0; 

    public BitInputStream(FileInputStream inputStream) {
        this.inputStream = inputStream;
        this.currentByte = 0;
        this.bitsRemaining = 0;
    }

    public int readBit() throws IOException {
        if (bitsRemaining == 0) {
            currentByte = inputStream.read();
            if (currentByte == -1) {
                return -1;
            }
            bitsRemaining = 8;
        }

        bitsRemaining--;
        int bit = (currentByte >> bitsRemaining) & 1;
        
        // Zwiększamy licznik przy każdym udanym odczycie
        totalBitsRead++; 
        
        return bit;
    }
    
    // Getter do licznika
    public long getTotalBitsRead() {
        return totalBitsRead;
    }

    // ... (reszta metod bez zmian, pamiętaj żeby readByte i inne korzystały z readBit!) ...
    
    // WAŻNE: readPaddingInfo, readByte itp. muszą wewnątrz wołać readBit(), 
    // aby licznik totalBitsRead się aktualizował!
    
    public int readPaddingInfo() throws IOException {
        int result = 0;
        for (int i = 0; i < 3; i++) {
            int bit = readBit(); // To zwiększy licznik!
            if (bit == -1) throw new IOException("EOF in padding");
            result = (result << 1) | bit;
        }
        return result;
    }

    public int readByte() throws IOException {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            int bit = readBit(); // To zwiększy licznik!
            if (bit == -1) throw new IOException("EOF in byte");
            result = (result << 1) | bit;
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}