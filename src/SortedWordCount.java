import java.io.*;
import java.util.*;

public class SortedWordCount {

    private static class WordCount implements Comparable<WordCount> {
        public final String word;
        public final int count;
        public WordCount(String w, int c) {
            word = w;
            count = c;
        }
        public int compareTo(WordCount other) {
            return other.count - count;  // descending order
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: SortedWordCount <file> <N>");
            System.err.println("   file: name of file containint words");
            System.err.println("   N: print top N most frequent words");
            return;
        }
        final int N = Integer.parseInt(args[1]);
        if (N <= 0) { 
            System.err.println("Bogus number of words to output!\n");
            return;
        }
        try {
            HashFrequencyTable<String> table = new HashFrequencyTable<String>(1000, 0.75F);
            FileInputStream stream = new FileInputStream(args[0]);
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader in = new BufferedReader(streamReader);
            String word;
            while ((word = in.readLine()) != null) {
                table.click(word);
            }
            in.close();
            
            ArrayList<WordCount> words = new ArrayList<WordCount>();
            for (String w : table) {
                int count = table.count(w);
                words.add(new WordCount(w,count));
            }

            Collections.sort(words);

            int i = 0;
            for (WordCount wc : words) {
                System.out.println(wc.count + " " + wc.word);
                if (++i >= N) break;
            }

        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }

    }

}
