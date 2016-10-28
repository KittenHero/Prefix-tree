import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class HMbasedPrefixMap implements PrefixMap {
	
	private final HashMap<String, String> keys = new HashMap<>();;
	private final HashMap<String, ArrayList<String>> prefixes  = new HashMap<>();
	private final String validCharsRegex;
	private int keySum = 0;

	public HMbasedPrefixMap(String validChars) { 
		for (int i = 0; i < validChars.length(); i++) {
			char ch = validChars.charAt(i);
			switch (ch) {
				case '\\':
				case ']':
				case '[':
				case '^':
					throw new IllegalArgumentException("no " + ch + " pls");
			}
			if (i != validChars.lastIndexOf(ch))
				throw new IllegalArgumentException("repeated characters");
		}
		validCharsRegex = '[' + validChars + "]*+"; 
	}
	
	public HMbasedPrefixMap() { this("ACTG"); }

	@Override
	public int size() {	return keys.size(); }

	@Override
	public boolean isEmpty() { return keys.isEmpty(); }

	private void verify(String key) {
		if (key == null) throw new IllegalArgumentException("null key");
		if (!key.matches(validCharsRegex)) throw new MalformedKeyException();
	}
	
	@Override
	public String get(String key) {
		verify(key);
		return keys.get(key);
	}

	@Override
	public String put(String key, String value) {
		verify(key);
		if (value == null) throw new IllegalArgumentException("null value");
		
		if (keys.containsKey(key)) 
			return keys.put(key, value);
		
		for (int i = 0; i <= key.length(); i++) {

			String prefix = key.substring(0, i);
			ArrayList<String> keyStartingWith = 
				prefixes.getOrDefault(prefix, new ArrayList<>());

			keyStartingWith.add(key);
			prefixes.putIfAbsent(prefix, keyStartingWith);
		}
		keySum += key.length();
		return keys.put(key, value);
	}

	@Override
	public String remove(String key) {
		
		verify(key);
		if (!keys.containsKey(key))
			return null;
		
		for (int i = 0; i <= key.length(); i++) {
			String prefix = key.substring(0, i);
			
			prefixes.get(prefix).remove(key);
			if (prefixes.get(prefix).isEmpty())
				prefixes.remove(prefix);
		}
		keySum -= key.length();
		return keys.remove(key);
	}

	@Override
	public int countKeysMatchingPrefix(String prefix) {
		verify(prefix);
		return prefixes.getOrDefault(prefix, new ArrayList<>()).size();
	}

	@Override
	public List<String> getKeysMatchingPrefix(String prefix) {
		verify(prefix);
		
		ArrayList<String> keysMPrefix = new ArrayList<>();
		if (prefixes.containsKey(prefix))
			keysMPrefix.addAll(prefixes.get(prefix));
		
		return keysMPrefix;
	}

	@Override
	public int countPrefixes() { return keys.isEmpty() ? 0 : prefixes.size() - 1; }

	@Override
	public int sumKeyLengths() { return keySum; }
}
