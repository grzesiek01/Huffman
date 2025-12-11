package pl.edu.pw.ee.aisd2025zex5.compress;

import pl.edu.pw.ee.aisd2025zex5.services.tree.HuffmanCodeMap;
import pl.edu.pw.ee.aisd2025zex5.services.tree.HuffmanTree;
import pl.edu.pw.ee.aisd2025zex5.services.tree.HuffmanNode;
import pl.edu.pw.ee.aisd2025zex5.services.map.CountingMap;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HuffmanCompressor {

    /**
     * Główna metoda kompresująca.
     * Wykonuje kompresję w jednym przebiegu zapisu, dzięki wcześniejszemu obliczeniu paddingu.
     * * @param inputPath Ścieżka do pliku wejściowego.
     * @param outputPath Ścieżka do pliku wynikowego.
     * @param tree Zbudowane drzewo Huffmana.
     * @param codes Mapa kodów (sekwencja -> string bitowy).
     * @param countingMap Mapa częstości wystąpień (potrzebna do matematyki).
     * @param sequenceLength Długość sekwencji (-l).
     */
    public void compress(String inputPath, String outputPath, HuffmanTree tree, 
                         HuffmanCodeMap codes, CountingMap<ByteSequence> countingMap, int sequenceLength) {
        
        try (
            // Otwieramy strumienie - teraz wystarczy zwykły FileOutputStream!
            FileOutputStream fos = new FileOutputStream(outputPath);
            BitOutputStream bitOut = new BitOutputStream(fos);
            FileInputStream fis = new FileInputStream(inputPath)
        ) {
            
            // --- KROK 1: MATEMATYKA (Pre-kalkulacja rozmiaru) ---
            System.out.println("Obliczanie rozmiaru pliku...");
            
            // A. Ile bitów zajmą skompresowane dane?
            long dataBits = calculateDataBits(countingMap, codes);
            
            // B. Ile bitów zajmie drzewo (nagłówek)?
            long treeBits = calculateTreeBits(tree.getRoot());
            
            // C. Stałe narzuty: 
            // 3 bity (padding info) + 8 bitów (długość sekwencji) = 11 bitów
            long staticOverheadBits = 3 + 8 + 8;
            
            // Całkowita liczba bitów w pliku (bez paddingu końcowego)
            long totalBits = staticOverheadBits + treeBits + dataBits;
            
            // D. Obliczamy padding (ile zer brakuje do pełnego bajtu)
            // Wzór: (8 - (reszta z dzielenia przez 8)) modulo 8
            int paddingBits = (int) ((8 - (totalBits % 8)) % 8);
            
            long fileSize = new java.io.File(inputPath).length();
            int validBytesInLastChunk = (int) (fileSize % sequenceLength);
            if (validBytesInLastChunk == 0) {
                validBytesInLastChunk = sequenceLength;
            }
            
            System.out.println("Statystyki:");
            System.out.println(" - Bity danych: " + dataBits);
            System.out.println(" - Bity drzewa: " + treeBits);
            System.out.println(" - Całkowite bity: " + totalBits);
            System.out.println(" -> Wymagany padding: " + paddingBits);


            // --- KROK 2: ZAPIS NAGŁÓWKA ---
            
            // A. Zapisujemy obliczony padding (3 bity)
            // (Metoda writePaddingInfo musi być dostępna w BitOutputStream)
            bitOut.writePaddingInfo(paddingBits); 
            
            // B. Zapisujemy długość sekwencji (8 bitów)
            bitOut.writeByte((byte) sequenceLength);
            
            // C. NOWOŚĆ: Ważne bajty w ostatnim bloku (8 bitów)
            bitOut.writeByte((byte) validBytesInLastChunk);
            
            // D. Zapisujemy drzewo (Pre-order)
            HeaderService headerService = new HeaderService();
            headerService.writeTree(bitOut, tree.getRoot());

            
            // --- KROK 3: ZAPIS DANYCH ---
            // Ponieważ countingMap powstał w pierwszym przebiegu, a teraz robimy drugi,
            // musimy upewnić się, że czytamy plik od początku.
            // Jeśli fis jest nowo otwarty, jest na początku.
            
            System.out.println("Zapisywanie danych...");
            
            byte[] buffer = new byte[sequenceLength];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                // Obsługa niepełnego bufora na końcu pliku
                //byte[] dataToProcess;
                if (bytesRead < sequenceLength) {
                    for (int i = bytesRead; i < sequenceLength; i++) {
                        buffer[i] = 0; // Dopełniamy pustym znakiem
                    }
                }

                ByteSequence sequence = new ByteSequence(buffer); // Zawsze pełny rozmiar
                String code = codes.get(sequence);
                
                if (code == null) {
                    throw new RuntimeException("BŁĄD: Brak kodu dla sekwencji: " + sequence);
                }
                
                // Zapisujemy bity
                bitOut.writeBits(code);
            }
            
            // --- KROK 4: KONIEC ---
            // Metoda close() w BitOutputStream (wywołana automatycznie) dopisze zera.
            // Ponieważ obliczyliśmy je idealnie, liczba zer będzie zgodna z 'paddingBits'.
            
            System.out.println("Kompresja zakończona sukcesem.");

        } catch (IOException e) {
            throw new RuntimeException("Błąd kompresji: " + e.getMessage(), e);
        }
    }

    // --- METODY POMOCNICZE (MATEMATYKA) ---

    /**
     * Oblicza ile bitów zajmą same skompresowane dane (suma iloczynów częstość * długość kodu).
     */
    private long calculateDataBits(CountingMap<ByteSequence> map, HuffmanCodeMap codes) {
        long totalBits = 0;
        
        // Iterujemy po wszystkich wpisach z mapy zliczeń
        for (CountingMap.Entry<ByteSequence> entry : map.entrySet()) {
            ByteSequence seq = entry.getKey();
            int count = entry.getCount(); // Ile razy wystąpiła sekwencja
            
            String code = codes.get(seq);
            
            if (code != null) {
                totalBits += (long) count * code.length();
            }
        }
        return totalBits;
    }

    /**
     * Oblicza ile bitów zajmie zapisanie drzewa w nagłówku (metoda Pre-order).
     */
    private long calculateTreeBits(HuffmanNode node) {
        if (node == null) return 0;

        // Jeśli liść
        if (node.sequence != null) { // lub node.left == null && node.right == null
            // 1 bit (flaga '1' oznaczająca liść) 
            // + 8 bitów (długość sekwencji w bajtach - zapisywana przez writeByte) 
            // + (8 bitów * liczba bajtów w sekwencji)
            byte[] data = node.sequence.getData();
            
            // WAŻNE: W Twoim HeaderService usunęliśmy zapis długości sekwencji dla każdego liścia,
            // bo jest ona stała i zapisana na początku pliku!
            // Więc liczymy: 1 bit flagi + (8 * długość danych)
            return 1 + (data.length * 8);
        } 
        // Jeśli węzeł wewnętrzny
        else {
            // 1 bit (flaga '0') + bity lewego dziecka + bity prawego dziecka
            return 1 + calculateTreeBits(node.left) + calculateTreeBits(node.right);
        }
    }
}