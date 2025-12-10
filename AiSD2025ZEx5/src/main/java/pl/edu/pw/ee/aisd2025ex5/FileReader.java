package pl.edu.pw.ee.aisd2025ex5;

import java.io.FileInputStream;
import java.io.IOException;
import pl.edu.pw.ee.aisd2025zex5.services.CountingMap;
import pl.edu.pw.ee.aisd2025zex5.services.ByteSequence;
import pl.edu.pw.ee.aisd2025zex5.services.PriorityListQueue;
import pl.edu.pw.ee.aisd2025zex5.services.HuffmanNode;
import java.util.List;

public class FileReader {
        
    public CountingMap readBytesFromFile(String filePath, int lengthOfByteSequence) {
        CountingMap<ByteSequence> bytesRead = new CountingMap();
        // Użycie try-with-resources automatycznie zamknie FileInputStream
        try (FileInputStream fis = new FileInputStream(filePath)) {
            
            // Bufor na naszą sekwencję (np. 3 bajty)
            byte[] buffer = new byte[lengthOfByteSequence];
            int bytesReadCount;

            // Metoda read(buffer) próbuje wczytać tyle bajtów, ile mieści się w tablicy
            // Zwraca liczbę faktycznie wczytanych bajtów lub -1 na końcu pliku
            while ((bytesReadCount = fis.read(buffer)) != -1) {

                // WAŻNE: Musimy obsłużyć sytuację na końcu pliku, 
                // gdzie wczytano mniej bajtów niż wynosi długość bufora.
                if (bytesReadCount < lengthOfByteSequence) {
                // Opcja A: Ignorujemy niepełną końcówkę (zależy od treści zadania)
                // Opcja B: Traktujemy to jako krótszą sekwencję (wymaga aby ByteSequence to obsługiwał)
        
                // Przykład dla Opcji B (stworzenie mniejszej tablicy dla końcówki):
                   byte[] partialBuffer = new byte[bytesReadCount];
                   for(int i=0;i<bytesReadCount;i++){
                       partialBuffer[i] = buffer[i];
                   }
                   ByteSequence seq = new ByteSequence(partialBuffer);
                   bytesRead.increment(seq);
        
                continue; // lub break, zależy jak chcesz traktować "ogon" pliku
                }

                // Tworzymy obiekt klucza (korzystając z Twojej klasy i konstruktora kopiującego)
                ByteSequence sequence = new ByteSequence(buffer);
    
                // Dodajemy do mapy
                bytesRead.increment(sequence);
            }

            //System.out.println("\n--- Wczytywanie zakończone. Wczytano " + byteCount + " bajtów. ---");

        } catch (IOException e) {
            // Obsługa błędów wejścia/wyjścia (np. plik nie został znaleziony)
            System.err.println("Wystąpił błąd podczas wczytywania pliku: " + e.getMessage());
        }
        return bytesRead;
    }
    
    public PriorityListQueue TransferFromMapToQueue(CountingMap map){
        PriorityListQueue queue = new PriorityListQueue();
        List<CountingMap.Entry<ByteSequence>> entries = map.entrySet();
        for (CountingMap.Entry<ByteSequence> entry : entries) {
            HuffmanNode node = new HuffmanNode(entry.getKey(),entry.getCount());
            queue.add(node);
        }
        return queue;
    }
}
