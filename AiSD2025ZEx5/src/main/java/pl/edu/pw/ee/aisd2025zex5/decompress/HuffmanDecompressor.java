package pl.edu.pw.ee.aisd2025zex5.decompress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import pl.edu.pw.ee.aisd2025zex5.services.decompress.HeaderReader;
import pl.edu.pw.ee.aisd2025zex5.services.decompress.BitInputStream;
import pl.edu.pw.ee.aisd2025zex5.services.tree.HuffmanNode;

public class HuffmanDecompressor {

    public void decompress(String inputPath, String outputPath) {
        
        // 1. Sprawdzamy fizyczny rozmiar pliku
        File file = new File(inputPath);
        long fileSizeInBytes = file.length();
        long totalBitsInFile = fileSizeInBytes * 8; // Całkowita pojemność w bitach

        try (
            FileInputStream fis = new FileInputStream(file);
            BitInputStream bitIn = new BitInputStream(fis);
            FileOutputStream fos = new FileOutputStream(outputPath)
        ) {
            System.out.println("Rozpoczynam dekompresję...");

            // 2. Wczytujemy nagłówek
            HeaderReader reader = new HeaderReader();
            
            // Context musi teraz zawierać validBytesInLastChunk (dodany w poprzednim kroku)
            HeaderReader.DecodingContext context = reader.readHeader(bitIn);

            HuffmanNode root = context.root;
            int paddingBits = context.paddingBits;
            int validBytesInLastChunk = context.validBytesInLastChunk; // <--- NOWOŚĆ
            
            // 3. Obliczamy limit bitów (Gdzie kończą się skompresowane dane)
            long stopAtBit = totalBitsInFile - paddingBits;
            
            System.out.println("Stopujemy na bicie nr: " + stopAtBit);
            System.out.println("Ważne bajty w ostatnim bloku: " + validBytesInLastChunk);

            
            // 4. Główna pętla
            HuffmanNode currentNode = root;
            
            // Czytamy dopóki licznik przeczytanych bitów jest mniejszy od limitu
            while (bitIn.getTotalBitsRead() < stopAtBit) {
                
                int bit = bitIn.readBit();
                if (bit == -1) break; 

                // Idziemy po drzewie
                if (bit == 0) {
                    currentNode = currentNode.left;
                } else {
                    currentNode = currentNode.right;
                }

                // Czy to liść?
                if (currentNode.left == null && currentNode.right == null) {
                    
                    byte[] data = currentNode.sequence.getData();

                    // --- KLUCZOWA ZMIANA: OBSŁUGA OSTATNIEGO BLOKU ---
                    
                    // Sprawdzamy, czy właśnie przeczytaliśmy ostatni bit danych
                    // (czyli licznik bitów dotarł do granicy stopAtBit)
                    if (bitIn.getTotalBitsRead() >= stopAtBit) {
                        
                        // TO JEST OSTATNI BLOK DANYCH W PLIKU!
                        // Zapisujemy tylko tyle bajtów, ile jest ważnych (ucięcie zer)
                        if (validBytesInLastChunk > 0) {
                            fos.write(data, 0, validBytesInLastChunk);
                        }
                        
                        // Przerywamy pętlę, bo to był koniec danych
                        break; 
                        
                    } else {
                        // TO JEST NORMALNY BLOK (ŚRODEK PLIKU)
                        // Zapisujemy całość (pełny sequenceLength)
                        fos.write(data);
                    }
                    
                    // Reset na korzeń dla następnego znaku
                    currentNode = root;
                }
            }
            
            System.out.println("Dekompresja zakończona.");

        } catch (IOException e) {
            throw new RuntimeException("Błąd dekompresji: " + e.getMessage(), e);
        }
    }
}