
public class Trie {
    private static final byte OFFSET = 13; //(Char.hash + OFFSET) % ALPHABET.length = alphabet.indexOf(Char)
    private static final char[] ALPHABET = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                                            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                                            'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static class Entry implements Comparable<Entry> {
        byte element;
        boolean isWordEnd = false;
		final byte prefixLength;
		Entry parent;
        MyList<Entry> children = new MyList<Entry>();
        Entry(final Character theElement, final Entry theParent) {
            element = (byte) (theElement.hashCode());
			parent = theParent;
			if (parent == null) {
				prefixLength = -1;
			} else {
				prefixLength = (byte) (parent.prefixLength + 1);
			}
        }
		Entry(final char theElement) {
			this(theElement, null);
		}
        @Override
        public int compareTo(Entry other) {
            return element - other.element;
        }
        public String toString() {
            if (parent == null) {
                return "";
            } else {
                return String.valueOf(ALPHABET[(element + OFFSET) % ALPHABET.length]);
            }
        }
    }
    private final Entry root;
    Trie() {
        root = new Entry(' ');
    }
    public final void insert(final String word) {
        insert(word, 0, root);
    }
    private void insert(final String word, final int index, final Entry current) {
        final MyList<Entry> kids = current.children;
        final char element = word.charAt(index);
        final Entry next = kids.insert(new Entry(element, current));
        if (index < word.length() - 1) {
            insert(word, index + 1, next);
        } else {
            next.isWordEnd = true;
        }

    }
    public void display() {
        display(root, "");
    }
	private static void display(final Entry e, final String s) {
		if (e.isWordEnd) {
			System.out.println(s + e);
		}
		for (Entry c : e.children) {
			display(c, s + e);
		}
		//System.out.println();
	}
}
