
public class Trie {
    private static final byte OFFSET = 13; //(Char.hash + OFFSET) % ALPHABET.length = alphabet.indexOf(Char)
    private static final char[] ALPHABET = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                                            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                                            'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final int BOARD_SIZE = 15;
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
	public ScrabbleWord getWords(final ScrabbleWord word, final char[] hand) {
	    MyList<ScrabbleWord> list = new MyList<ScrabbleWord>();
	    final int col = word.getStartColumn();
	    final int row = word.getStartRow();
	    final char orientation;
	    final int spaceLeft;
	    final int vertSpace = BOARD_SIZE - (row + 1);
	    final int horizSpace = BOARD_SIZE - (col + 1);
	    if (word.getOrientation() == 'v') {
	        orientation = 'h';
	        spaceLeft = horizSpace;
	    } else if (word.getOrientation() == 'h') {
	        orientation = 'v';
            spaceLeft = vertSpace;
	    } else {
	        if (vertSpace > horizSpace) {
	            spaceLeft = vertSpace;
	            orientation = 'v';
	        } else {
	            orientation = 'h';
	            spaceLeft = horizSpace;
	        }
	    }
	    for (final Entry e : root.children) {
            if (e == null) {
                continue;
            }
            if (isInHand(e, hand)) {
                //System.out.println("TEST");
                getAvailibleSuffixes(e, "", spaceLeft, hand, list, col, row, orientation);
            }
	    }
	    return list.removeFirst();
	}
	private void getAvailibleSuffixes(final Entry e, final String s, final int spaceLeft,
	                                  final char[] hand, final MyList<ScrabbleWord> list,
	                                  final int startCol, final int startRow, final char orientation) {
	    if (e.isWordEnd) {
	        list.insert(new ScrabbleWord(s + e, startRow, startCol, orientation));
	    }
	    if (spaceLeft > 1) {
	        for (Entry c: e.children) {
	            if (c == null) {
	                continue;
	            } else if (isInHand(c, hand)) {
	                getAvailibleSuffixes(c, s + e, spaceLeft - 1, hand, list, startCol, startRow, orientation);
	            }
	        }
	    }
	}
    private boolean isInHand(Entry e, char[] hand) {
        for (final Character c : hand) {
            //System.out.printf("%s %s", e, c);
            if (c.toString().equals(e.toString())) {
                return true;
            }
        }
        return false;
    }
}
