package pl.edu.pw.ee.aisd2025zex5.services.priorityqueue;

// Zakładam, że klasa HuffmanNode jest w tym samym pakiecie lub zaimportowana

import pl.edu.pw.ee.aisd2025zex5.services.tree.HuffmanNode;

public class PriorityListQueue {

    private Node head = null;
    private int size = 0;

    // Węzeł listy teraz przechowuje KONKRETNIE HuffmanNode
    private static class Node {
        HuffmanNode value;
        Node next;

        Node(HuffmanNode value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    /**
     * Dodaje węzeł Huffmana, zachowując kolejność rosnącą (Min-Queue).
     * Najmniejsza częstość (frequency) będzie na początku listy (head).
     */
    public void add(HuffmanNode nodeToAdd) {
        // Przypadek 0: Pusta lista
        if (head == null) {
            head = new Node(nodeToAdd, null);
            size++;
            return;
        }

        // Przypadek 1: Nowy element jest mniejszy od głowy (wstawiamy na początek)
        // Używamy compareTo, który zaimplementowałeś w HuffmanNode
        if (nodeToAdd.compareTo(head.value) < 0) {
            head = new Node(nodeToAdd, head);
            size++;
            return;
        }

        // Przypadek 2: Szukamy miejsca w środku lub na końcu
        Node current = head;
        
        // Idziemy tak długo, jak następny element istnieje I jest mniejszy od dodawanego
        // (czyli szukamy momentu, gdzie nodeToAdd będzie mniejszy od następnika)
        while (current.next != null && nodeToAdd.compareTo(current.next.value) >= 0) {
            current = current.next;
        }

        // Wstawiamy ZA elementem current
        current.next = new Node(nodeToAdd, current.next);
        size++;
    }

    /**
     * Pobiera i usuwa element o najmniejszej częstości (głowę listy).
     */
    public HuffmanNode poll() {
        if (head == null) {
            return null;
        }
        
        HuffmanNode result = head.value;
        head = head.next; // Przesuwamy głowę dalej
        size--;
        
        return result;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public String toString() {
        if (head == null) {
            return "[]"; // Pusta kolejka
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        Node current = head;
        while (current != null) {
            // Dodajemy reprezentację tekstową węzła Huffmana
            sb.append(current.value.sequence + ",");
            sb.append(current.value.frequency);

            // Jeśli to nie jest ostatni element, dodajemy strzałkę lub przecinek
            if (current.next != null) {
                sb.append(" -> "); 
            }
            current = current.next;
        }

        sb.append("]");
        return sb.toString();
    }
    public int getSize(){
        return size;
    }
}
