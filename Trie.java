
public class Trie {
    private static final byte OFFSET = 13; //(Char.hash + OFFSET) % ALPHABET.length = alphabet.indexOf(Char)
    private static final char[] ALPHABET = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                                            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                                            'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static class Entry implements Comparable<Entry> {
        byte element;
        boolean isWordEnd = false;
		final byte prefixLength;
		Entry parent;
		Entry[] children = new Entry[ALPHABET.length];
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
		public int getIndex() {
		    return (element + OFFSET) % ALPHABET.length;
		}
        @Override
        public int compareTo(Entry other) {
            return element - other.element;
        }
        public String toString() {
            if (parent == null) {
                return "";
            } else {
                return String.valueOf(ALPHABET[getIndex()]);
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
        final Entry[] kids = current.children;
        final char element = word.charAt(index);
        Entry next = new Entry(element, current);
        if (kids[next.getIndex()] == null) {
            kids[next.getIndex()] = next;
        } else {
            next = kids[next.getIndex()];
        }
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
		    if (c != null) {
		          display(c, s + e);
		    }
		}
		//System.out.println();
	}
	public void getAvailibleSuffixes(final Entry e, final String s,
	                                  final char[] hand, final MyList<ScrabbleWord> list,
	                                  final int startCol, final int startRow, final char orientation) {
	    if (e.isWordEnd) {
	        list.insert(new ScrabbleWord(s + e, startRow, startCol, orientation));
	    }
	    for (Entry c: e.children) {
	        if (c == null) {
	            continue;
	        } else if (isInHand(c, hand)) {
	            getAvailibleSuffixes(c, s + e, hand, list, startCol, startRow, orientation);
	        }
	    }
	}
    private boolean isInHand(Entry e, char[] hand) {
        for (final Character c : hand) {
            if (c.toString().equals(e.toString())) {
                return true;
            }
        }
        return false;
    }
}
