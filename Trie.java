
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
	public ScrabbleWord getBestWord(final ScrabbleWord word, final char[] hand) {
	    ScrabbleWord result = new ScrabbleWord();
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
	    result = wordStartsWithLetterOnBoard(word, hand, result, col, row, orientation, spaceLeft);
	    if (result == null) {
	        return word;
	    }
	    return result;
	}
    /**
     * @param word
     * @param hand
     * @param result
     * @param col
     * @param row
     * @param orientation
     * @param spaceLeft
     * @return
     */
    private ScrabbleWord wordStartsWithLetterOnBoard(final ScrabbleWord word, final char[] hand,
                                                     ScrabbleWord result, final int col, final int row,
                                                     final char orientation, final int spaceLeft) {
        final String wordOnBoard = word.getScrabbleWord();
        final Entry[] rootChildren = root.children;
        for (int i = 0; i < wordOnBoard.length(); i++) {
            final byte b = (byte) (Character.hashCode(wordOnBoard.charAt(i)));
            final int index = (b + OFFSET) % ALPHABET.length;
            final Entry e = rootChildren[index];
            if (rootChildren[index] != null) {
                if (orientation == 'h') {
                    final int newRow = word.getScrabbleWord().indexOf(e.toString()) + row;
                    result = getAvailibleSuffixes(e, "", spaceLeft, hand, result, col, newRow, orientation);
                } else {
                    final int newCol = word.getScrabbleWord().indexOf(e.toString()) + col;
                    result = getAvailibleSuffixes(e, "", spaceLeft, hand, result, newCol, row, orientation);
                }
            }
        }
        return result;
    }
    private ScrabbleWord getAvailibleSuffixes(final Entry e, final String s, final int spaceLeft,
	                                  final char[] hand, final ScrabbleWord word,
	                                  final int startCol, final int startRow, final char orientation) {
	    ScrabbleWord result = word;
        if (e.isWordEnd) {
	        final ScrabbleWord newWord = new ScrabbleWord(s + e, startRow, startCol, orientation);
	        if (result == null) {
	            result = newWord;
	        } else if (newWord.compareTo(result) < 0) { 
	            result = newWord;
	        }
	    }
	    if (spaceLeft > 1) {
	        for (Entry c: e.children) {
	            char[] newHand = new char[hand.length];
	            for (int i = 0; i < hand.length; i++) {
	                newHand[i] = hand[i];
	            }
	            if (c == null) {
	                continue;
	            } else if (isInHand(c, newHand)) {
	                result = getAvailibleSuffixes(c, s + e, spaceLeft - 1, newHand, result, startCol, startRow, orientation);
	            }
	        }
	    }
	    return result;
	}
    private boolean isInHand(Entry e, char[] hand) {
        for (int i = 0; i < hand.length; i++) {
            if (hand[i] == e.toString().charAt(0)) {
                hand[i] = '!';
                return true;
            }
        }
        /*
        for (final Character c : hand) {
            //System.out.printf("%s %s", e, c);
            if (c.toString().equals(e.toString())) {
                return true;
            }
        }
        */
        return false;
    }
}
