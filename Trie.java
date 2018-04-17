
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
	    final boolean originalIsHorizontal = word.getOrientation() == 'h'; //is the original word horizontal?
	    if (originalIsHorizontal) {
	        //Case 1: Best result is perpendicular, starting with a letter already on the board
	        int spaceLeft = BOARD_SIZE - (row + 1); //Space left under the word
	        result = wordStartsWithLetterOnBoard(word, hand, result, col, row, 'v', spaceLeft);
	        //Case 2: Best result is an extension of the word on board (suffix)
	        if (word.getScrabbleWord().length() > 1) { //following only works if word is at least two char long
	            spaceLeft = BOARD_SIZE - (col + word.getScrabbleWord().length()); //space right of word
	            result = resultExtendsWordOnBoard(word, hand, result, col, row, 'h', spaceLeft);
	        }
	    } else {
	        //Case 1
	        int spaceLeft = BOARD_SIZE - (col + 1);//Space right of word
	        result = wordStartsWithLetterOnBoard(word, hand, result, col, row, 'h', spaceLeft);
	        //Case 2
	        if (word.getScrabbleWord().length() > 1) {
	            spaceLeft = BOARD_SIZE - (row + word.getScrabbleWord().length()); //space under of word
	            result = resultExtendsWordOnBoard(word, hand, result, col, row, 'v', spaceLeft);
	        }
	    }
	    if (result == null) {
	        return word;
	    }
	    //System.out.println(result);
	    return result;
	}
    private ScrabbleWord resultExtendsWordOnBoard(ScrabbleWord word, char[] hand, ScrabbleWord result, int col, int row,
            char orientation, int spaceLeft) {
        Entry letterOnBoard = getEntryWithPrefix(word.getScrabbleWord(), root);
        final String prefix = word.getScrabbleWord().substring(0, word.getScrabbleWord().length() - 1);//Everything except the last letter
        ScrabbleWord possibleResult = getAvailibleSuffixes(letterOnBoard, prefix, spaceLeft, hand, result, col, row, orientation);
        if (possibleResult == null || possibleResult.equals(word)) {
            return result;
        } else {
            return possibleResult;
        }
    }
    private Entry getEntryWithPrefix(final String prefix, final Entry current) {
        if (prefix.length() == 0) {
            return current;
        } else {
            final char c = prefix.charAt(0);
            final int index = getIndex(c);
            final Entry newEntry = current.children[index];
            if (newEntry != null) {
                return getEntryWithPrefix(prefix.substring(1), newEntry);
            }
        }
        return null;
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
            final char c = wordOnBoard.charAt(i);
            final int index = getIndex(c);
            final Entry firstLetter = root.children[index];
            if (root.children[index] != null) {
                if (orientation == 'h') {
                    final int newRow = word.getScrabbleWord().indexOf(firstLetter.toString()) + row;
                    result = getAvailibleSuffixes(firstLetter, "", spaceLeft, hand, result, col, newRow, orientation);
                } else {
                    final int newCol = word.getScrabbleWord().indexOf(firstLetter.toString()) + col;
                    result = getAvailibleSuffixes(firstLetter, "", spaceLeft, hand, result, newCol, row, orientation);
                }
            }
        }
        return result;
    }
    /**
     * @param c
     * @return
     */
    private int getIndex(final char c) {
        final byte b = (byte) (Character.hashCode(c));
        final int index = (b + OFFSET) % ALPHABET.length;
        return index;
    }
    /**
     * 
     * @param currentEntry
     * @param currentString
     * @param spaceLeft
     * @param hand
     * @param currentResult
     * @param startCol
     * @param startRow
     * @param orientation
     * @return
     */
    private ScrabbleWord getAvailibleSuffixes(final Entry currentEntry, final String currentString, final int spaceLeft,
	                                  final char[] hand, final ScrabbleWord currentResult,
	                                  final int startCol, final int startRow, final char orientation) {
	    ScrabbleWord result = currentResult;
        if (currentEntry.isWordEnd) {
	        final ScrabbleWord newWord = new ScrabbleWord(currentString + currentEntry, startRow, startCol, orientation);
	        if (result == null) {
	            result = newWord;
	        } else if (newWord.compareTo(result) < 0) { 
	            result = newWord;
	        }
	    }
	    if (spaceLeft > 1) {
	        for (int i = 0; i < hand.length; i++) {
	            final char c = hand[i];
	            if (c != '!' && c != '_') {
	                char[] newHand = new char[hand.length];
	                for (int j = 0; j < hand.length; j++) {
	                    newHand[j] = hand[j];
	                    if (j == i) {
	                        newHand[j] = '!';
	                    }
	                }
	                final Entry newEntry = currentEntry.children[getIndex(c)];
	                if (newEntry != null) {
	                    result = getAvailibleSuffixes(newEntry, currentString + currentEntry, spaceLeft - 1, newHand, result, startCol, startRow, orientation);    
	                }
	            }
	        }
	    }
	    return result;
	}
}
