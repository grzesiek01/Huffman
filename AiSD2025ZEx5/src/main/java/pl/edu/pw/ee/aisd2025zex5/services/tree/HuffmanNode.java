package pl.edu.pw.ee.aisd2025zex5.services.tree;

import pl.edu.pw.ee.aisd2025zex5.compress.ByteSequence;

public class HuffmanNode implements Comparable<HuffmanNode> {
    public ByteSequence sequence; // Twoja sekwencja bajtów (dla liści)
    public int frequency;         // Liczba wystąpień (priorytet)
    public HuffmanNode left;
    public HuffmanNode right;

    // Konstruktor dla liścia
    public HuffmanNode(ByteSequence seq, int freq) {
        this.sequence = seq;
        this.frequency = freq;
        this.left = null;
        this.right = null;
    }

    // Konstruktor dla węzła wewnętrznego (połączenie dwóch innych)
    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;
    }

    // To jest kluczowe! Mówi kolejce, jak układać elementy.
    @Override
    public int compareTo(HuffmanNode other) {
        // Chcemy Min-Heap (najmniejsze częstości na górze), więc:
        return Integer.compare(this.frequency, other.frequency);
        
        // Jeśli nie możesz użyć Integer.compare:
        // return this.frequency - other.frequency;
    }
    
    @Override
    public String toString() {
        // Jeśli węzeł ma sekwencję, to jest liściem (Leaf)
        if (sequence != null) {
            return "{Seq:" + sequence + ", Freq:" + frequency + "}";
        } 
        // W przeciwnym razie jest węzłem wewnętrznym (połączeniem)
        else {
            return "{Node: " + frequency + "}";
        }
    }
    
    public HuffmanNode getLeft(){
        return left;
    }
    
    public HuffmanNode getRight(){
        return right;
    }
}
