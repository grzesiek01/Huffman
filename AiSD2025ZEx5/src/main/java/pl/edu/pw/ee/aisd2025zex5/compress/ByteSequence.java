package pl.edu.pw.ee.aisd2025zex5.compress;

public class ByteSequence {
    private final byte[] data;

    public ByteSequence(byte[] data) {
        // Zamiast Arrays.copyOf(data, data.length)
        this.data = copyBytes(data, 0, data.length);
    }
    
    // Dodatkowy konstruktor pomocny przy wycinaniu fragmentów (dla przesuwnego okna)
    public ByteSequence(byte[] source, int start, int length) {
        this.data = copyBytes(source, start, length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ByteSequence that = (ByteSequence) o;
        
        // Ręczne porównanie tablic (zamiast Arrays.equals)
        if (this.data.length != that.data.length) {
            return false;
        }
        for (int i = 0; i < this.data.length; i++) {
            if (this.data[i] != that.data[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        // Ręczne liczenie hasha (algorytm taki sam jak w Arrays.hashCode)
        int result = 1;
        for (byte element : data) {
            result = 31 * result + element;
        }
        return result;
    }

    @Override
    public String toString() {
        // Ręczne budowanie Stringa (zamiast Arrays.toString)
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < data.length; i++) {
            sb.append((char)data[i]);
            if (i < data.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    // --- Metody pomocnicze (Utils) ---

    // Zamiennik Arrays.copyOfRange / Arrays.copyOf
    private static byte[] copyBytes(byte[] source, int start, int length) {
        byte[] copy = new byte[length];
        for (int i = 0; i < length; i++) {
            // Sprawdzenie granic, żeby nie wyjść poza tablicę source
            if (start + i < source.length) {
                copy[i] = source[start + i];
            } else {
                break; 
            }
        }
        return copy;
    }
    
    public byte[] getData() {
        return data; // Zwraca tablicę bajtów
    }
}