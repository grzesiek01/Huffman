package pl.edu.pw.ee.aisd2025zex5.services.tree;

import pl.edu.pw.ee.aisd2025zex5.services.tree.HuffmanNode;
import pl.edu.pw.ee.aisd2025zex5.services.priorityqueue.PriorityListQueue;

public class HuffmanTree {
    private HuffmanNode root;
    public void createHuffmanTree(PriorityListQueue queue){
        while(queue.getSize() > 1){
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();
            HuffmanNode merge = new HuffmanNode(left,right);
            queue.add(merge);
        }
       
        root = queue.poll();
    }
    public HuffmanNode getRoot(){
        return root;
    }
    
    public HuffmanCodeMap getCodes() {
        HuffmanCodeMap codes = new HuffmanCodeMap();
        generateCodes(root, "", codes);
        return codes;
    }

    // Rekurencyjne generowanie kodów
    private void generateCodes(HuffmanNode node, String currentCode, HuffmanCodeMap map) {
        if (node == null) return;
        
        // Jeśli to liść (ma sekwencję bajtów)
        if (node.left == null && node.right == null) {
            // Przypadek specjalny: plik z tylko 1 rodzajem znaku
            if (currentCode.length() == 0) {
                currentCode = "0";
            }
            // Zapisujemy do Twojej mapy
            map.put(node.sequence, currentCode);
            return;
        }

        // Idziemy w lewo (dodajemy '0')
        generateCodes(node.left, currentCode + "0", map);
        
        // Idziemy w prawo (dodajemy '1')
        generateCodes(node.right, currentCode + "1", map);
    }
    
    @Override
    public String toString() {
        if (root == null) {
            return "Puste Drzewo Huffmana";
        }
        StringBuilder sb = new StringBuilder();
        // Rozpoczynamy rekurencję od korzenia, bez wcięcia, bez etykiety bitu
        printRecursive(sb, root, "", "");
        return sb.toString();
    }

    // Metoda pomocnicza do rysowania drzewa
    private void printRecursive(StringBuilder sb, HuffmanNode node, String prefix, String bitLabel) {
        if (node == null) {
            return;
        }

        // 1. Dodajemy wcięcie (prefix)
        sb.append(prefix);

        // 2. Jeśli to nie korzeń, dodajemy etykietę bitu (0 lub 1)
        if (!bitLabel.isEmpty()) {
            sb.append("(").append(bitLabel).append(") -> ");
        } else {
            sb.append("ROOT -> ");
        }

        // 3. Wypisujemy informacje o węźle (korzystając z toString węzła)
        sb.append(node.toString());
        sb.append("\n");

        // 4. Rekurencyjne wywołanie dla dzieci
        // Zwiększamy wcięcie (dodajemy spacje lub kreski)
        // Lewe dziecko to bit '0', Prawe dziecko to bit '1'
        printRecursive(sb, node.left, prefix + "    |", "0");
        printRecursive(sb, node.right, prefix + "    |", "1");
    }
}
