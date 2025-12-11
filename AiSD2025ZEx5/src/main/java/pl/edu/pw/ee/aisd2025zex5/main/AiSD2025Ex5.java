package pl.edu.pw.ee.aisd2025zex5.main;

import pl.edu.pw.ee.aisd2025zex5.filereader.FileReader;
import pl.edu.pw.ee.aisd2025zex5.services.priorityqueue.PriorityListQueue;
import pl.edu.pw.ee.aisd2025zex5.services.tree.HuffmanTree;
import pl.edu.pw.ee.aisd2025zex5.services.tree.HuffmanCodeMap;
import pl.edu.pw.ee.aisd2025zex5.compress.HuffmanCompressor;
import pl.edu.pw.ee.aisd2025zex5.decompress.HuffmanDecompressor;

public class AiSD2025Ex5 {

    public static void main(String[] args) {
        FileReader tab = new FileReader();
        tab.readBytesFromFile("test.txt",5);
        System.out.println(tab.getCountingMap().toString());
        PriorityListQueue queue = tab.TransferFromMapToQueue(tab.getCountingMap());
        System.out.println(queue.toString());
        HuffmanTree tree = new HuffmanTree();
        HuffmanCodeMap map;
        tree.createHuffmanTree(queue);
        map = tree.getCodes();
        System.out.println(map.toString());
        HuffmanCompressor compress = new HuffmanCompressor();
        compress.compress("test.txt", "result.comp",tree, map,tab.getCountingMap(), 5);
        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress("result.comp", "result.txt");
    }
}
