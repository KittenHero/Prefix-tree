import java.util.List;
import java.util.ArrayList;

public class Assignment implements PrefixMap {
	
	protected class Node {
		/**********************************************
		 *               ^
		 *       ________|________ __________________
		 * Node:|        |        |                  |
		 *      |                 |                  |
		 *      |  parent: Node   |  value: String   |
		 *      |                 |                  |
		 *      |_________________|__________________|
		 *      |        |        |        |         |
		 *  <--------   children : Node array    -------->
		 *      |________|___|____|___|____|_________|
		 *                   |        |
		 *                   V        V
		 **********************************************/
		String value = null;
		final Node parent; // for efficient removal
		final Node[] children = new Node[NUCLEOBASES.length]; // you never know
		
		Node(Node parent) { this.parent = parent; }
		
		String getValue() { return value; }
		Node getChild(char key) { return children[index(key)]; }
		
		public Node getOrCreateChild(char key) {
			
			int index = index(key);
			if (children[index] == null) {
				children[index] =  new Node(this);
				Assignment.this.updateNodeCount(1); // because encapsulation
			}
			return children[index];
		}
		
		int index(char key) {
			
			for (int i = 0; i < NUCLEOBASES.length; i++)
				if (key == NUCLEOBASES[i])
					return i;
			
			throw new MalformedKeyException();
		}
		
		String setValue(String v) {
			String prev = value;
			value = v;
			return prev;
		}
		/*****************************************************************
		 * Nodes should manage their own children (because encapsulation)
		 *****************************************************************/
		String removeValue() {
			String prev = value;
			value = null;
			if (parent != null)
				parent.removeIfDead(this);
			
			return prev;
		}
		/*********************************************************
		 * Recursively deletes empty/dead nodes from the parent
		 *
		 * DO NOT CALL FROM OUTSIDE
		 *(I'm sorry Java doesn't have nested functions
		 * and declaring anonymous/local classes is ugly
		 * and lambdas are ugly as f**k
		 * and inner classes can't hide from outer classes)
		 * (also non-static inner classes can't have static methods
		 *********************************************************/
		void removeIfDead(Node child) {
			
			if (!child.isDead()) return;
			
			children[index(child)] = null;
			Assignment.this.updateNodeCount(-1);
			
			if (parent != null)
				parent.removeIfDead(this);
		}
		/**********************************************************
		 * return whether or the node is useless
		 *********************************************************/
		boolean isDead() {
			
			for (int i = 0; i < children.length; i++)
				if (children[i] != null)
					return false;
			
			return value == null;
		}
		/********************************************************
		 * Also don't call this from outside
		 ********************************************************/
		int index(Node child) {
			if (child == null) throw new IllegalArgumentException("undefined behaviour");
			
			for (int i = 0; i < children.length; i++)
				if (children[i] == child)
					return i;
			
			throw new IllegalArgumentException("Invalid child");
		}
	}
	/***************************************************************************
	 * Shared:
	 *  ____________
	 * |Nulcleobases|
	 * |     :      |
	 * | A, C, G, T |
	 * |____________|
	 * 
	 * Instance:
	 *  __________________________ ________________
	 * |                          |                |
	 * |        size : int        |                |
	 * |     (number of keys)     |  root : Node   |
	 * |--------------------------|                |
	 * |       keySum : int       |                |
	 * |  (Combined key lengths)  |                |
	 * |--------------------------|        -------------------->
	 * |    numNodes : int        |                |
	 * |(maintained by Node class)|                |
	 * |__________________________|________________|
	 * 
	 *************************************************************************/
	private static final char[] NUCLEOBASES = new char[] {'A', 'C', 'G', 'T'};
	private final Node root = new Node(null);
	private int size = 0, // obviously there are no more than 2^31 - 1 DNA sequences in existence
				keySum = 0, // and the length sum of those sequences are also below 2^31 - 1
				numNodes = 1; // the number of unique prefixes is also below 2^31 - 1
	
	public Assignment() {} //:P bad code right here
	/***********************************************************************************
	 *  helper methods are static so you know without looking inside 
	 *  that they won't modify instance variables
	 ***********************************************************************************/
	private static void verify(String key) {
		if (key == null) throw new IllegalArgumentException("null key");
		if (!key.matches("[ACTG]*+")) throw new MalformedKeyException(); // possessive, since it fails faster
	}
	@Override
	public boolean isEmpty() { return size == 0; }
	@Override
	public int size() { return size; }
	@Override
	public int countPrefixes() { return numNodes - 1; }// No. nodes = No. prefixes + root 
	@Override
	public int sumKeyLengths() { return keySum; }

	private void updateNodeCount(int amount) { numNodes += amount; }
	@Override
	public String get(String key) {
		
		Node pos = traverse(root, key);
		if (pos == null)
			return null;
		else
			return pos.getValue();
	}
	@Override
	public String put(String key, String value) {
		
		verify(key);
		if (value == null) throw new IllegalArgumentException();
		
		Node pos = root;
		for (int i = 0; i < key.length(); i++)
			 pos = pos.getOrCreateChild(key.charAt(i));
		
		if (pos.getValue() == null) {
			keySum += key.length(); // no overflow plz
			size++;
		}
		return pos.setValue(value);
	}
	@Override
	public String remove(String key) {
		
		Node pos = traverse(root, key);
		if (pos == null || pos.getValue() == null)
			return null;
		
		keySum -= key.length();
		size--;
		return pos.removeValue();
	}
	
	private static Node traverse(Node from, String path) {
		
		verify(path);
		Node cur = from;
		
		for (int i = 0; i < path.length() && cur != null; i++)
			cur = cur.getChild(path.charAt(i));
		
		return cur;
	}
	
	@Override
	public int countKeysMatchingPrefix(String prefix) { return countValues(traverse(root, prefix)); }
	
	private static int countValues(Node subtrie) {
		
		if (subtrie == null)
			return 0;
		
		int count = (subtrie.getValue() != null) ? 1 : 0;
		for (char base : NUCLEOBASES)
			count += countValues(subtrie.getChild(base));
		
		return count;
	}
	@Override
	public List<String> getKeysMatchingPrefix(String prefix) {
		
		List<String> keys = new ArrayList<>();
		preOrderKeys(traverse(root, prefix), new StringBuilder(prefix), keys);		
		return keys;
	}
	/**************************************************************************************
	 * Populates the list with keys in the subtrie given in the parameter
	 **************************************************************************************/
	private static void preOrderKeys(Node subtrie, StringBuilder curKey, List<String> keys) {
		
		if (subtrie == null)
			return;
		if (subtrie.getValue() != null)
			keys.add(curKey.toString());
		
		for (char base : NUCLEOBASES) {
			curKey.append(base);
			preOrderKeys(subtrie.getChild(base), curKey, keys);
			curKey.deleteCharAt(curKey.length() - 1);
		}
	}
}
/****************************************************************************************************************
-\-
\-- \-
 \  - -\
  \      \\
   \       \
    \       \\
     \        \\
     \          \\
     \            \\
      \            \\
       \            \\ 
       \. .          \\
        \    .       \\
         \      .    \\
          \       .  \\
          \         . \\
          \            <=)
          \            <==)
          \            <=)
           \           .\\                                           _-
           \         .   \\                                        _-//
           \       .     \\                                     _-_/ /
           \ . . .        \\                                 _--_/ _/
            \              \\                              _- _/ _/
            \               \\                      ___-(O) _/ _/
            \                \\                 __--  __   /_ /      *******************************************
            \                 \\          ____--__----  /    \_       Class ended
             \                  \\       -------       /   \_  \_     
              \                   \                  //   // \__ \_   from: http://theoatmeal.com/ (source code)
               \                   \\              //   //      \_ \_*******************************************
                \                   \\          ///   //          \__-
                \                -   \\/////////    //
                \            -         \_         //
                /        -                      //
               /     -                       ///
              /   -                       //
         __--/                         ///
__________/                            // |
//-_________      ___                ////  |
    ____\__--/                /////    |
-----______    -/---________////        |
 _______/  --/    \                   |
/_________-/       \                   |
//                  \                   /
                   \.                 /
                   \     .            /
                    \       .        /
                   \\           .    /
                    \                /
                    \              __|
                    \              ==/
                    /              //
                    /          .  //
                    /   .  .     //
                   /.           //
                  /            //
                  /           /
                 /          //
                /         //
             --/         /
            /          //
        ////         //
     ///_________////
****************************************************************************************************************/
