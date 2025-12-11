package pl.edu.pw.ee.aisd2025zex5.filereader;

import java.io.FileInputStream;
import java.io.IOException;
import pl.edu.pw.ee.aisd2025zex5.services.map.CountingMap;
import pl.edu.pw.ee.aisd2025zex5.compress.ByteSequence;
import pl.edu.pw.ee.aisd2025zex5.services.priorityqueue.PriorityListQueue;
import pl.edu.pw.ee.aisd2025zex5.services.tree.HuffmanNode;
import java.util.List;

public class FileReader {
    
    // Dodano typ generyczny <ByteSequence> dla czystości kodu
    private CountingMap<ByteSequence> bytesRead;
        
    public void readBytesFromFile(String filePath, int lengthOfByteSequence) {
        bytesRead = new CountingMap<>(); // Dodano <>
        
        try (FileInputStream fis = new FileInputStream(filePath)) {
            
            byte[] buffer = new byte[lengthOfByteSequence];
            int bytesReadCount;

            while ((bytesReadCount = fis.read(buffer)) != -1) {

                // POPRAWKA: Obsługa końcówki pliku przez DOPYCHANIE ZERAMI (Padding)
                // Zamiast tworzyć mniejszą tablicę, zerujemy końcówkę obecnego bufora.
                if (bytesReadCount < lengthOfByteSequence) {
                    for (int i = bytesReadCount; i < lengthOfByteSequence; i++) {
                        buffer[i] = 0; // Wypełniamy resztę zerami (pusty znak)
                    }
                }

                // Tworzymy obiekt klucza.
                // WAŻNE: Konstruktor ByteSequence MUSI robić kopię tablicy (new byte[]...),
                // ponieważ tablica 'buffer' jest tutaj używana wielokrotnie!
                ByteSequence sequence = new ByteSequence(buffer);
    
                // Dodajemy do mapy. 
                // Teraz klucz zawsze ma długość 'lengthOfByteSequence' (np. [A, 0]).
                bytesRead.increment(sequence);
            }

        } catch (IOException e) {
            // Zgodnie z wymaganiami zadania (punkt 4), wyjątki powinny być rzucane dalej lub jawnie obsługiwane.
            // Tutaj wypisujesz na konsolę, co jest OK dla debugowania, 
            // ale w finalnym projekcie lepiej rzucić RuntimeException.
            throw new RuntimeException("Błąd podczas czytania pliku: " + filePath, e);
        }
    }
    
    public PriorityListQueue TransferFromMapToQueue(CountingMap<ByteSequence> map){
        PriorityListQueue queue = new PriorityListQueue();
        
        // Dodano typy generyczne do listy
        List<CountingMap.Entry<ByteSequence>> entries = map.entrySet();
        
        for (CountingMap.Entry<ByteSequence> entry : entries) {
            HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getCount());
            queue.add(node);
        }
        return queue;
    }
    
    // Poprawiony typ zwracany (dodano generyk)
    public CountingMap<ByteSequence> getCountingMap(){
        return this.bytesRead;
    }
}