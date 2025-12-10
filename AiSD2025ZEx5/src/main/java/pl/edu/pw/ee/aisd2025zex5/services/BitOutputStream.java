package pl.edu.pw.ee.aisd2025zex5.services;

import java.io.FileOutputStream;
import java.io.IOException;

public class BitOutputStream implements AutoCloseable {

    private final FileOutputStream outputStream;
    
    // Bufor na jeden bajt (int wystarczy, będziemy operować na 8 bitach)
    private int currentByte = 0; 
    
    // Licznik, ile bitów mamy już w buforze (od 0 do 8)
    private int numBitsFilled = 0;

    public BitOutputStream(FileOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Dodaje pojedynczy bit ('0' lub '1') do strumienia.
     */
    public void writeBit(char bit) throws IOException {
        // Przesuwamy obecną zawartość bufora o 1 w lewo, robiąc miejsce na nowy bit
        currentByte = currentByte << 1;

        // Jeśli bit to '1', ustawiamy najmłodszy bit na 1 (operacja OR). 
        // Jeśli '0', nic nie robimy (najmłodszy bit jest już 0 po przesunięciu).
        if (bit == '1') {
            currentByte = currentByte | 1;
        }

        numBitsFilled++;

        // Jeśli uzbieraliśmy 8 bitów, zapisujemy bajt do pliku i resetujemy bufor
        if (numBitsFilled == 8) {
            outputStream.write(currentByte);
            currentByte = 0;
            numBitsFilled = 0;
        }
    }

    /**
     * Zapisuje cały ciąg bitów (np. "01101") znak po znaku.
     */
    public void writeBits(String bitString) throws IOException {
        for (int i = 0; i < bitString.length(); i++) {
            writeBit(bitString.charAt(i));
        }
    }

    /**
     * Zamyka strumień. WAŻNE: Obsługuje Padding (dopełnienie).
     * Jeśli na końcu zostało np. 3 bity, musimy dopełnić zerami do 8 i zapisać.
     */
    @Override
    public void close() throws IOException {
        if (numBitsFilled > 0) {
            // Przesuwamy w lewo o tyle, ile brakuje do 8, aby bity były "z lewej strony" bajtu
            currentByte = currentByte << (8 - numBitsFilled);
            outputStream.write(currentByte);
        }
        outputStream.close();
    }
    
    public void writeByte(byte b) throws IOException {
        // Zapisujemy 8 bitów z tego bajtu
        for (int i = 7; i >= 0; i--) {
            // Sprawdzamy, czy i-ty bit jest 1
            // (b >> i) przesuwa bit na pozycję 0
            // & 1 wycina tylko ten jeden bit
            int bit = (b >> i) & 1;
        
            if (bit == 1) {
                writeBit('1');
            } else {
                writeBit('0');
            }
        }
    }
}