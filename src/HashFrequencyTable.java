import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

public class HashFrequencyTable<K> implements FrequencyTable<K>, Iterable<K> {

	private class Entry {
		public K key;
		public int count;
		public Entry(K k) {key = k; count = 1;}
		public String toString(){
			return "\t"+ key + "\t" + count;
		}
	}

	private float maxLoadFactor;
	private int numEntries;
	private ArrayList<Entry> table;

	private class TableIterator implements Iterator<K> {
		private int i;
		public TableIterator() {i = 0;}
		public boolean hasNext() {
			while (i < table.size() && table.get(i) == null)
				i++;
			return i < table.size();
		}
		public K next() {
			return table.get(i++).key;
		}
		public void remove() {
			throw new UnsupportedOperationException("Remove not supported");
		}
	}

	@Override
	public Iterator<K> iterator() {
		return new TableIterator();
	}

	public int size() {return numEntries;}
	public boolean isEmpty() {return numEntries == 0;}   
	private float loadFactor() {return (float) numEntries / table.size();}

	@Override
	public void click(K key) {
		int h = search(key);
		Entry e = table.get(h);
		if(e == null){
			insert(key, h);
		}
		else{
			if(key.equals(e.key)){
				int i = e.count;
				e.count = ++i;
			}	
		}
	}

	@Override
	public int count(K key) {
		int index = search(key);
		Entry e = table.get(index);
		if(e != null){
			return e.count;
		}
		else
			return 0;
	}

	public HashFrequencyTable(int initialCapacity, float maxLoadFactor) {
		this.maxLoadFactor = maxLoadFactor;
		int sz = nextPowerOfTwo(initialCapacity);
		table = new ArrayList<Entry>(sz);
		for (int i = 0; i < sz; i++)
			table.add(null);
		numEntries = 0;
	}

	private static int nextPowerOfTwo(int n) {
		int e = 1;
		while ((1 << e) < n)
			e++;
		return 1 << e;
	}

	private int search(K key) {
		int probe=0;
		int h = key.hashCode() & (table.size()-1);
		int k= (h + probe * (probe + 1) / 2) & (table.size()-1);;

		Entry e;
		while ((e = table.get(k)) != null) {
			if (key.equals(e.key))
				return k;  // hit
			probe++;
			k = (h + probe * (probe + 1) / 2) & (table.size()-1); // Quadratic probing 
		}
		return k; // miss
	}

	private void insert(K key, int index){
		Entry e = new Entry(key);
		table.set(index, e);
		numEntries++;
		if (loadFactor() >= maxLoadFactor)
			doubleSize();
	}

	private void  doubleSize() {
		ArrayList<Entry> holdTable = table;
		int sz = nextPowerOfTwo(table.size()*2);
		this.table = new ArrayList<>(sz);
		for (int i = 0; i < sz; i++)
			table.add(null);
		numEntries = 0;
		rehash(holdTable, 0);
	}

	private void reHashAux(K key, int count) {
		int index = search(key);
		Entry e = table.get(index);
		if (e == null) {
			e = new Entry(key);
			e.count = count;
			table.set(index, e);
			numEntries++;
		}
	}

	private void rehash(ArrayList<Entry> oldTable, int startPosition){ 
		for (int i = startPosition; i < oldTable.size(); i++) {
			Entry e = oldTable.get(i);
			if (e != null) {
				reHashAux(e.key, e.count);
			}
		}
	}

	public void dump(PrintStream str){
		ArrayList<Entry> entryTable = this.table;

		for(int i=0; i < entryTable.size(); i++){
			Entry e = entryTable.get(i);

			str.print(i + ":");
			if(e != null){
				str.print(" key='" + e.key.toString() +"',");
				str.println(" count=" + e.count);
			}
			else
				str.println(" null");
		}
	}
	public static void main(String[] args) {
		String hamlet =
				"To be or not to be that is the question " +
						"Whether 'tis nobler in the mind to suffer " +
						"The slings and arrows of outrageous fortune ";
		String words[] = hamlet.split("\\s+");
		HashFrequencyTable<String> table = new HashFrequencyTable<String>(10, 0.95F);
		for (int i = 0; i < words.length; i++)
			if (words[i].length() > 0)
				table.click(words[i]);
		table.dump(System.out);
	}
}
