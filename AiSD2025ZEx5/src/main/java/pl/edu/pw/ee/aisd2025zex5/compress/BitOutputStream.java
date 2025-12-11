package pl.edu.pw.ee.aisd2025zex5.compress;

import java.io.FileOutputStream;
import java.io.IOException;

public class BitOutputStream implements AutoCloseable {

    private final FileOutputStream outputStream;
    
    // Bufor na jeden bajt (gromadzimy tu bity zanim wyślemy je do pliku)
    private int currentByte = 0; 
    
    // Licznik, ile bitów mamy już w buforze (od 0 do 8)
    private int numBitsFilled = 0;

    public BitOutputStream(FileOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * NOWA METODA: Zapisuje 3 bity informujące o wielkości paddingu (dopełnienia).
     * Służy do zapisania liczby od 0 do 7 na samym początku pliku.
     * @param paddingBits Liczba bitów dopełnienia (np. obliczona wcześniej matematycznie).
     */
    public void writePaddingInfo(int paddingBits) throws IOException {
        // Zabezpieczenie: interesują nas tylko 3 ostatnie bity (zakres 0-7)
        // 0x07 to binarnie 00000111
        int cleanPadding = paddingBits & 0x07;

        // Zapisujemy 3 bity: od najstarszego (pozycja 2) do najmłodszego (pozycja 0)
        // Np. dla liczby 5 (101):
        // i=2 -> bit 1
        // i=1 -> bit 0
        // i=0 -> bit 1
        for (int i = 2; i >= 0; i--) {
            int bit = (cleanPadding >> i) & 1;
            if (bit == 1) {
                writeBit('1');
            } else {
                writeBit('0');
            }
        }
    }

    /**
     * Dodaje pojedynczy bit ('0' lub '1') do strumienia.
     */
    public void writeBit(char bit) throws IOException {
        // Przesuwamy obecną zawartość bufora o 1 w lewo
        currentByte = currentByte << 1;

        // Jeśli bit to '1', wstawiamy go na najmłodszą pozycję.
        // Jeśli '0', samo przesunięcie w lewo załatwiło sprawę (weszło 0).
        if (bit == '1') {
            currentByte = currentByte | 1;
        }

        numBitsFilled++;

        // Jeśli bufor jest pełny (8 bitów), wysyłamy go do pliku
        if (numBitsFilled == 8) {
            outputStream.write(currentByte);
            currentByte = 0;
            numBitsFilled = 0;
        }
    }

    /**
     * Pomocnicza metoda do zapisania całego bajtu (8 bitów) bit-po-bicie.
     * Używana przy zapisywaniu nagłówka (długość sekwencji, bajty klucza).
     */
    public void writeByte(byte b) throws IOException {
        for (int i = 7; i >= 0; i--) {
            // Wyciągamy i-ty bit z bajtu
            int bit = (b >> i) & 1;
            
            if (bit == 1) {
                writeBit('1');
            } else {
                writeBit('0');
            }
        }
    }

    /**
     * Zapisuje ciąg bitów podany jako String (np. kod Huffmana "010").
     */
    public void writeBits(String bitString) throws IOException {
        for (int i = 0; i < bitString.length(); i++) {
            writeBit(bitString.charAt(i));
        }
    }

    /**
     * Zamyka strumień i zapisuje ostatni, niepełny bajt (jeśli istnieje).
     * Brakujące bity są uzupełniane zerami (to jest właśnie ten padding).
     */
    @Override
    public void close() throws IOException {
        if (numBitsFilled > 0) {
            // Obliczamy ile zer trzeba dopisać
            int paddingToAdd = 8 - numBitsFilled;
            
            // Przesuwamy bity w lewo, co automatycznie dopisuje zera na końcu
            currentByte = currentByte << paddingToAdd;
            
            // Zapisujemy ostatni bajt
            outputStream.write(currentByte);
        }
        
        // Zamykamy fizyczny strumień pliku
        outputStream.close();
    }
}