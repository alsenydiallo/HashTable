
public interface FrequencyTable<K> {
	
	void click(K key);
	int count(K key);
}
