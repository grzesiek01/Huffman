package pl.edu.pw.ee.aisd2025zex5.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HuffmanCompressor {

    public void compress(String inputPath, String outputPath, HuffmanTree tree, 
                         HuffmanCodeMap codes, int sequenceLength) {
        
        // Używamy try-with-resources dla bezpieczeństwa zamknięcia wszystkich strumieni
        try (
            FileOutputStream fos = new FileOutputStream(outputPath);
            // 1. Otwieramy BitOutputStream na FileOutputStream
            BitOutputStream bitOut = new BitOutputStream(fos); 
            FileInputStream fis = new FileInputStream(inputPath)
        ) {
       
            // --- FAZA 0: ZAPIS DŁUGOŚCI SEKWENCJI (NOWA OPTYMALIZACJA) ---
            // Zapisujemy długość sekwencji jako bajt (zakładamy, że nie przekroczy 255)
            bitOut.writeByte((byte) sequenceLength); 
            //System.out.println("Zapisano długość sekwencji: " + sequenceLength);
            
            // --- FAZA I: ZAPIS NAGŁÓWKA (DRZEWA) ---
            System.out.println("Zapisywanie drzewa (Pre-order)...");
            
            HuffmanNode root = tree.getRoot();
            HeaderService headerService = new HeaderService();
            
            // Nagłówek jest teraz zapisywany BIT PO BICIE
            headerService.writeTree(bitOut, root);
            
            // WAŻNE: Po zapisaniu nagłówka, FIS musi wrócić na początek
            // (zakładając, że plik został już w całości wczytany do zliczenia)
            // Użycie .getChannel().position(0) jest najszybszym sposobem na cofnięcie FIS.
            fis.getChannel().position(0);

            // --- FAZA II: ZAPIS DANYCH SKOMPRESOWANYCH ---
            System.out.println("Rozpoczynam kompresję danych...");
            
            byte[] buffer = new byte[sequenceLength];
            int bytesRead;

            // Czytamy plik takimi samymi blokami, jak przy zliczaniu
            while ((bytesRead = fis.read(buffer)) != -1) {
                
                // Obsługa końcówki pliku (jeśli wczytano mniej niż pełny bufor)
                byte[] dataToProcess;
                if (bytesRead < sequenceLength) {
                    // Własna kopia (bez użycia Arrays)
                    dataToProcess = new byte[bytesRead];
                    for(int i=0; i<bytesRead; i++) {
                         dataToProcess[i] = buffer[i];
                    }
                } else {
                    dataToProcess = buffer;
                }

                ByteSequence sequence = new ByteSequence(dataToProcess);

                // Pobieramy kod ze słownika (który wygenerowaliśmy wcześniej)
                String code = codes.get(sequence);

                if (code == null) {
                    // Błąd krytyczny: Brak kodu dla sekwencji
                    throw new RuntimeException("BŁĄD: Nie znaleziono kodu dla sekwencji: " + sequence);
                }

                // Zapisujemy bity do pliku
                bitOut.writeBits(code);
            }
            
            // Metoda close() w BitOutputStream zadba o zapisanie ostatniego, niepełnego bajtu (padding)
            System.out.println("Kompresja zakończona sukcesem. Plik zapisany z nagłówkiem bitowym.");

        } catch (IOException e) {
            // Zgodnie z wymaganiami zadania (punkt 2 w 1.3), rzucamy Państwa wyjątki
            throw new RuntimeException("Wystąpił błąd I/O podczas kompresji pliku: " + e.getMessage(), e);
        }
    }
}
