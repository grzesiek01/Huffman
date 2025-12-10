package pl.edu.pw.ee.aisd2025zex5.services;

import java.util.List;
import java.util.ArrayList;
/**
 * Specjalistyczna mapa do zliczania wystąpień obiektów.
 * Kluczem jest obiekt typu K, wartością jest zawsze int (licznik).
 * @param <K> Typ klucza (np. ByteSequence, String, Character)
 */
public class CountingMap<K> {

    // Możesz zwiększyć ten rozmiar w zależności od potrzeb.
    // Dla grup bajtów (n-gramów) warto dać np. 4096 lub więcej.
    private static final int DEFAULT_CAPACITY = 256;

    private Node<K>[] buckets;
    private int size; // Ilość unikalnych kluczy

    // Węzeł przechowujący klucz oraz licznik (int)
    private static class Node<K> {
        final K key;
        int count;      // Tutaj trzymamy liczbę wystąpień (typ prosty int)
        Node<K> next;   // Wskaźnik na kolejny element w przypadku kolizji

        Node(K key, int count, Node<K> next) {
            this.key = key;
            this.count = count;
            this.next = next;
        }
    }
    @SuppressWarnings("unchecked")
    public CountingMap() {
        this.buckets = (Node<K>[]) new Node[DEFAULT_CAPACITY];
        this.size = 0;
    }
    @SuppressWarnings("unchecked")
    public CountingMap(int capacity) {
        this.buckets = (Node<K>[]) new Node[capacity];
        this.size = 0;
    }

    /**
     * Zwiększa licznik dla danego klucza o 1.
     * Jeśli klucz nie istnieje, dodaje go z wartością 1.
     */
    public void increment(K key) {
        add(key, 1);
    }

    /**
     * Zwiększa licznik dla danego klucza o podaną wartość (amount).
     */
    public void add(K key, int amount) {
        if (key == null) {
            return; // Lub rzuć wyjątek, zależnie od założeń
        }

        int bucketIndex = getBucketIndex(key);
        Node<K> current = buckets[bucketIndex];

        // 1. Przeszukujemy listę w poszukiwaniu klucza
        while (current != null) {
            if (current.key.equals(key)) {
                // Klucz znaleziony -> ZWIĘKSZAMY licznik (nie nadpisujemy!)
                current.count += amount;
                return;
            }
            current = current.next;
        }

        // 2. Jeśli pętla się skończyła, klucza nie ma w mapie.
        // Dodajemy nowy węzeł na początek listy w kubełku.
        Node<K> newNode = new Node<>(key, amount, buckets[bucketIndex]);
        buckets[bucketIndex] = newNode;
        size++;
    }

    /**
     * Zwraca aktualny licznik dla klucza.
     * Jeśli klucz nie istnieje, zwraca 0.
     */
    public int get(K key) {
        if (key == null) return 0;

        int bucketIndex = getBucketIndex(key);
        Node<K> current = buckets[bucketIndex];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.count;
            }
            current = current.next;
        }

        return 0; // Klucz nie występuje
    }

    public int size() {
        return size;
    }

    // Pomocnicza funkcja haszująca
    private int getBucketIndex(K key) {
        // Math.abs jest konieczny, bo hashCode może być ujemny
        return Math.abs(key.hashCode()) % buckets.length;
    }
    
    @Override
    public String toString() {
        // Używamy StringBuilder dla wydajności przy łączeniu napisów w pętli
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean isFirst = true; // Flaga, aby nie dodać przecinka na początku

        // 1. Iterujemy przez wszystkie kubełki w tablicy
        for (Node<K> bucket : buckets) {
            Node<K> current = bucket;

            // 2. Iterujemy przez listę połączoną w danym kubełku (jeśli nie jest pusty)
            while (current != null) {
                if (!isFirst) {
                    sb.append(", "); // Separator między elementami
                }
                
                // Dodajemy klucz i wartość
                sb.append(current.key);
                sb.append("=");
                sb.append(current.count);

                isFirst = false;
                current = current.next; // Przejdź do kolejnego węzła w łańcuchu
            }
        }

        sb.append("}");
        return sb.toString();
    }
    
    // Publiczna klasa, dzięki której zwrócimy dane na zewnątrz
    public static class Entry<K> {
        private final K key;
        private final int count;

        public Entry(K key, int count) {
            this.key = key;
            this.count = count;
        }

        public K getKey() { return key; }
        public int getCount() { return count; }
    
        @Override
        public String toString() { return key + "=" + count; }
    }
    
    public List<Entry<K>> entrySet() {
        List<Entry<K>> allEntries = new ArrayList<>();

        // 1. Iterujemy po tablicy kubełków
        for (Node<K> bucket : buckets) {
            Node<K> current = bucket;

            // 2. Iterujemy po łańcuchu wewnątrz kubełka
            while (current != null) {
                // Pakujemy klucz i licznik do publicznego obiektu Entry
                allEntries.add(new Entry<>(current.key, current.count));
            
                current = current.next;
            }
        }
    
        return allEntries;
    }
}