package pl.edu.pw.ee.aisd2025zex5.services.decompress;

import java.io.IOException;

import pl.edu.pw.ee.aisd2025zex5.services.tree.HuffmanNode;
import pl.edu.pw.ee.aisd2025zex5.compress.ByteSequence;

public class HeaderReader {

    // Klasa pomocnicza trzymająca wszystko, co wczytaliśmy z nagłówka
    public static class DecodingContext {
        public final HuffmanNode root;
        public final int sequenceLength;
        public final int paddingBits; // Informacja ile bitów na końcu ignorować
        public final int validBytesInLastChunk;

        public DecodingContext(HuffmanNode root, int sequenceLength, int paddingBits, int validBytesInLastChunk) {
            this.root = root;
            this.sequenceLength = sequenceLength;
            this.paddingBits = paddingBits;
            this.validBytesInLastChunk = validBytesInLastChunk;
        }
    }

    // Wewnątrz HeaderReader

    public DecodingContext readHeader(BitInputStream bitIn) throws IOException {
        int paddingBits = bitIn.readPaddingInfo();
        int sequenceLength = bitIn.readByte();
    
    // NOWOŚĆ: Czytamy informację o ostatnim bloku
        int validBytesInLastChunk = bitIn.readByte();

    // Drzewo czytamy ze stałą długością
        HuffmanNode root = readTreeRecursive(bitIn, sequenceLength);

        return new DecodingContext(root, sequenceLength, paddingBits, validBytesInLastChunk);
    }

    private HuffmanNode readTreeRecursive(BitInputStream bitIn, int sequenceLength) throws IOException {
        int bit = bitIn.readBit();
        
        if (bit == 1) {
        // LIŚĆ
        // ZMIANA: Nie czytamy długości z pliku. Używamy globalnego sequenceLength.
            byte[] buffer = new byte[sequenceLength];
            for (int i = 0; i < sequenceLength; i++) {
                buffer[i] = (byte) bitIn.readByte();
            }
        
            return new HuffmanNode(new ByteSequence(buffer), 0);
        } 
        else {
            HuffmanNode left = readTreeRecursive(bitIn, sequenceLength);
            HuffmanNode right = readTreeRecursive(bitIn, sequenceLength);
            return new HuffmanNode(left, right);
        }
    }
}