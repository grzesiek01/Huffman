package pl.edu.pw.ee.aisd2025zex5.services;

import java.io.IOException;
import java.io.OutputStream;

public class HeaderService {

    public void writeHeader(OutputStream os, HuffmanNode root) throws IOException {
        // Używamy BitOutputStream, bo nagłówek też jest teraz bitowy!
        // WAŻNE: Nie zamykaj strumienia wewnątrz tej metody (użyjemy go dalej do danych)
        // Dlatego nie używamy try-with-resources tutaj dla BitOutputStream, jeśli planujesz 
        // używać tego samego obiektu bitOut dalej.
        // ALE: Jeśli w main tworzysz nowy BitOutputStream dla danych, to tutaj stwórz tymczasowy.
        
        // Załóżmy, że przekazujemy tu już otwarty BitOutputStream, żeby nie psuć buforowania,
        // ALBO (prościej): tworzymy wrapper tylko na chwilę, ale musimy uważać na flush.
        
        // WERSJA BEZPIECZNA: Przyjmijmy BitOutputStream jako argument
        // (wymaga zmiany w wywołaniu w kompresorze)
    }

    // Nowa sygnatura metody - przyjmuje BitOutputStream
    public void writeTree(BitOutputStream bitOut, HuffmanNode node) throws IOException {
        if (node == null) return;

        if (node.left == null && node.right == null) {
            // LIŚĆ: Zapis '1'
            bitOut.writeBit('1');
        
            byte[] data = node.sequence.getData();
        
            // --- USUNIĘTO: bitOut.writeByte((byte) data.length); ---
            // Ponieważ ta informacja jest teraz na początku pliku!
        
            // Zapisz bajty sekwencji (tylko dane)
            for (byte b : data) {
                bitOut.writeByte(b);
            }
        } 
        else {
            // WĘZEŁ WEWNĘTRZNY: Zapis '0'
            bitOut.writeBit('0');
        
            // Rekurencja
            writeTree(bitOut, node.left);
            writeTree(bitOut, node.right);
        }
    }
}
