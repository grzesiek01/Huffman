package pl.edu.pw.ee.aisd2025zex5.services;

import java.util.List;
import java.util.ArrayList;

public class HuffmanCodeMap {

    private static final int DEFAULT_CAPACITY = 256; // Rozmiar tablicy
    private Node[] buckets;

    // Węzeł przechowujący parę: Sekwencja -> Kod (String)
    private static class Node {
        final ByteSequence key;
        String code;        // Zmiana względem CountingMap: String zamiast int
        Node next;

        Node(ByteSequence key, String code, Node next) {
            this.key = key;
            this.code = code;
            this.next = next;
        }
    }

    public HuffmanCodeMap() {
        this.buckets = new Node[DEFAULT_CAPACITY];
    }
    
    public HuffmanCodeMap(int capacity) {
        this.buckets = new Node[capacity];
    }

    /**
     * Dodaje kod dla danej sekwencji.
     */
    public void put(ByteSequence key, String code) {
        int bucketIndex = getBucketIndex(key);
        Node current = buckets[bucketIndex];

        // 1. Sprawdzamy, czy klucz już jest (nadpisanie)
        while (current != null) {
            if (current.key.equals(key)) {
                current.code = code;
                return;
            }
            current = current.next;
        }

        // 2. Dodajemy nowy węzeł na początek listy
        buckets[bucketIndex] = new Node(key, code, buckets[bucketIndex]);
    }

    /**
     * Pobiera kod dla danej sekwencji.
     * Zwraca null, jeśli sekwencji nie ma w mapie.
     */
    public String get(ByteSequence key) {
        int bucketIndex = getBucketIndex(key);
        Node current = buckets[bucketIndex];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.code;
            }
            current = current.next;
        }
        return null;
    }

    // Funkcja haszująca (taka sama jak w CountingMap)
    private int getBucketIndex(ByteSequence key) {
        // Zabezpieczenie przed ujemnym hashem
        return (key.hashCode() & 0x7FFFFFFF) % buckets.length;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n"); // Otwieramy nawias, dodajemy nową linię dla czytelności

        boolean isFirst = true;

        // 1. Iterujemy przez wszystkie kubełki (buckets)
        for (int i = 0; i < buckets.length; i++) {
            Node current = buckets[i];

            // 2. Iterujemy przez listę wewnątrz kubełka
            while (current != null) {
                if (!isFirst) {
                    sb.append(",\n"); // Separator: przecinek i nowa linia
                }
                
                // Format: [Sekwencja] = KodBinarny
                sb.append("  "); // Wcięcie dla estetyki
                sb.append(current.key.toString()); 
                sb.append(" = ");
                sb.append(current.code);

                isFirst = false;
                current = current.next;
            }
        }

        sb.append("\n}"); // Zamykamy nawias
        return sb.toString();
    }
    
    // Wewnątrz klasy HuffmanCodeMap

    // Prosta klasa do transportu danych (klucz + kod)
    public static class Entry {
        public final ByteSequence key;
        public final String code; // np. "010"

        public Entry(ByteSequence key, String code) {
            this.key = key;
            this.code = code;
        }
    }

    // Metoda zwracająca listę wszystkich wpisów (potrzebna java.util.ArrayList i List)
    public List<Entry> getAllEntries() {
        List<Entry> list = new ArrayList<>();
        for (Node bucket : buckets) {
            Node current = bucket;
            while (current != null) {
                list.add(new Entry(current.key, current.code));
                current = current.next;
            }
        }
        return list;
    }
}