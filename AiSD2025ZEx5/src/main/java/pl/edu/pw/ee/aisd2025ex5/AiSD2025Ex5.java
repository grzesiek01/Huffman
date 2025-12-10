package pl.edu.pw.ee.aisd2025ex5;

import pl.edu.pw.ee.aisd2025zex5.services.PriorityListQueue;
import pl.edu.pw.ee.aisd2025zex5.services.HuffmanTree;
import pl.edu.pw.ee.aisd2025zex5.services.HuffmanCodeMap;
import pl.edu.pw.ee.aisd2025zex5.services.HuffmanCompressor;

public class AiSD2025Ex5 {

    public static void main(String[] args) {
        FileReader tab = new FileReader();
        System.out.println(tab.readBytesFromFile("test.txt",1).toString());
        PriorityListQueue queue = tab.TransferFromMapToQueue(tab.readBytesFromFile("test.txt",1));
        System.out.println(queue.toString());
        HuffmanTree tree = new HuffmanTree();
        HuffmanCodeMap map;
        tree.createHuffmanTree(queue);
        map = tree.getCodes();
        System.out.println(map.toString());
        HuffmanCompressor compress = new HuffmanCompressor();
        compress.compress("test.txt", "result.comp",tree, map, 1);
        
    }
}
