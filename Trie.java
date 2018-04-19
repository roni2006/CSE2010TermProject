

/*
  Authors (group members):Javier Munoz, Sung-Jun Baek, Thanart Pandey
  Email addresses of group members: jmunoz2014@my.fit.edu, sbaek2015@my.fit.edu, tpandey2017@my.fit.edu
  Group name: 14

  Course: CSE2010
  Section: 2

  Description of the overall algorithm and key data structures:
      The dictionary is stored as a trie.
          Each entry in the trie has a byte element, representing its index in the alphabet
          Each entry has a size 26 array of children
      To find the best Scrabble Word
          Find the word on the board, return as original word
          Find words the start with one of the letters from original word
          Find words that are the original word + a suffix
          Return the word that is worth the most points
      Finding words (availableCharacters, currentEntry, currentResult)
          if current entry is the end of a word
              check to see if its more points than the currentResult
              if it is, currentResult = entry -> scrabble word
          For each available character c, convert c to it's index in the alphabet array
              This uses hashing to make this constant time
              find the entry next at the same index in currentEntry.children
              Remove c from availableCharacters
              if next entry is the end of a word
                  check to see if its more points than the currentResult
                  if it is, currentResult = entry -> scrabble word
              recurse using nextEntry, the new availableCharacters, and the current best result
          return the best result
              
   To find a next best word selection from the dictionary for the Scrabble.
   The program mostly uses hash, binary representation (byte), and trie algorithm
*/
public class Trie {
    /*
     * Description: This stores the dictionary in a trie.
     * Constants:
     *  The following two are used along with Character.hashCode(c) to provide constant time searching
     *  OFFSET
     *  ALPHABET
     *
     *  BOARD_SIZE
     *
     * Methods:
     *      insert(Str) calls insert(Str, 0, root)
     *      insert: uses string and int to determine character to be inserted at Entry's children
     *      getIndex: takes a character and turns it into an index
     *      getBestWord: given a ScrabbleWord and the char in hand, it finds a word to play by calling the other methods
     *          getAvailableSuffixes: given an entry, it finds its descendant with the most points
     *          wordStartsWithLetterOnBoard: finds words perpendicular to the word on board. Assuming they start with a letter on board
     *          resultExtendsWordOnBoard: finds words extending the word on board (extending by adding suffixes, not prefixes)
     *          getEntryWithPrefix: returns the entry with the given prefix
     */
    private static final byte OFFSET = 13; //(Char.hash + OFFSET) % ALPHABET.length = alphabet.indexOf(Char)
    public static final char[] ALPHABET = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                                            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                                            'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final int BOARD_SIZE = 15;
    private static class Entry {
        /*
         * Description:
         * Entry class, is the 'node' of the Trie
         * 
         * Variables:
         *  index: index of the character in the Alphabet
         *  children: array of children, where each child is at the same index they'd be in the alphabet
         *  isWordEnd: is this character the end of a word
         *
         * Methods:
         *  toString: uses the index to retrieve the character from the alphabet
         */
        private final byte index; //we use byte instead of char because byte is 8 bit and char is 16 bit
        boolean isWordEnd = false;
		private final Entry[] children = new Entry[ALPHABET.length];
		Entry(final char theElement) {
		    if (theElement == '!') { //Is root
		        index = -1;
		    } else {
		          //The following gives index the index of theElement in alphabet, assuming it is A-Z
	            index = (byte) ((Character.hashCode(theElement) + OFFSET) % ALPHABET.length);
		    }
		} 
        public String toString() {
            if (index == -1) { //Is root
                return "";
            } else {
                return String.valueOf(ALPHABET[index]);
            }
        }
    }
    private final Entry root;
    Trie() {
        root = new Entry('!'); //Root gets a special non-alphabet character
    }
    public final void insert(final String word) {
        insert(word, 0, root);
    }
    /**
     * Use index and word to find char element = word.charAt(index)
     * Insert the node into current's children
     * If there's already a node there, keep it
     * If index is less than the last index of word
     *  Insert with an incremented index and the new node
     * Else
     *  the node is the end of a word
     * @param word
     * @param index
     * @param current
     */
    private void insert(final String word, final int index, final Entry current) {
        final Entry[] kids = current.children;
        final char element = word.charAt(index);
        final Entry next;
        if (kids[getIndex(element)] == null) {
            next = new Entry(element);
            kids[getIndex(element)] = next;
        } else {
            next = kids[getIndex(element)];
        }
        if (index < word.length() - 1) {
            insert(word, index + 1, next);
        } else {
            next.isWordEnd = true;
        }

    }
    /**
     * Returns the best word to play using two cases:
     *  First: the newWord is perpendicular to the original word and starts with a letter from originalWord
     *  Second: the newWord is the originalWord + a suffix
     * returns the best word from those two cases
     * @param word
     * @param hand
     * @return
     */
	public ScrabbleWord getBestWord(final ScrabbleWord word, final char[] hand) {
	    //System.out.println(word);
	    //System.out.println(hand);
	    ScrabbleWord result = new ScrabbleWord();
	    final int col = word.getStartColumn();
	    final int row = word.getStartRow();
	    final boolean originalIsHorizontal = word.getOrientation() == 'h'; //is the original word horizontal?
	    //Case 1: Best result is perpendicular, starting with a letter already on the board
	    result = startOnBoard(word, hand, root, "", result);
	    //System.out.println(result);
	    if (originalIsHorizontal) {
	        //Case 1: Best result is perpendicular, starting with a letter already on the board
	        final int spaceAbove = row;
	        result = generatePrefixes(hand, spaceAbove, word, "", root, result);
	        //System.out.println(result);
	        //Case 2: Best result is an extension of the word on board (suffix)
	        if (word.getScrabbleWord().length() > 1) { //following only works if word is at least two char long
	            final int spaceRight = BOARD_SIZE - (col + word.getScrabbleWord().length()); //space right of word
	            result = extendsBoard(word, hand, result, col, row, 'h', spaceRight);
	        }
	    } else {
	        //Case 2
	        final int spaceLeft = col;
            result = generatePrefixes(hand, spaceLeft, word, "", root, result);
	        if (word.getScrabbleWord().length() > 1) {
	            final int spaceUnder = BOARD_SIZE - (row + word.getScrabbleWord().length()); //space under of word
	            result = extendsBoard(word, hand, result, col, row, 'v', spaceUnder);
	        }
	    }
	    if (result == null) {
	        return word;
	    }
	    return result;
	}
	/**
	 * Gets the best result extending the word on board
	 * @param word
	 * @param hand
	 * @param result
	 * @param col
	 * @param row
	 * @param orientation
	 * @param spaceLeft
	 * @return
	 */
    private ScrabbleWord extendsBoard(ScrabbleWord word, char[] hand, ScrabbleWord result, int col, int row,
            char orientation, int spaceLeft) {
        Entry letterOnBoard = getEntryWithPrefix(word.getScrabbleWord(), root); //Entry at end of word on board
        if (letterOnBoard != null) {
            ScrabbleWord possibleResult = getSuffixes(letterOnBoard, word.getScrabbleWord(), spaceLeft, hand, result, col, row, orientation);
            if (possibleResult == null || possibleResult.equals(word)) {
                return result;
            } else {
                return possibleResult;
            }
        }
        return result;
    }
    /**
     * Returns the entry with the corresponding prefix
     * @param prefix
     * @param current
     * @return
     */
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
    private ScrabbleWord generatePrefixes(final char[] hand, final int spaceBefore, final ScrabbleWord word, final String str,
                                                final Entry current, ScrabbleWord result) {
        //System.out.println(str);
        if (spaceBefore == 0) {
            result = startOnBoard(word, hand, current, str, result);
        } else {
            for (int i = 0; i < word.getScrabbleWord().length(); i++) {
                final Entry boardLetter = current.children[getIndex(word.getScrabbleWord().charAt(i))];
                if (boardLetter != null) {
                    result = startOnBoard(word, hand, current, str, result);
                }
            }
            for (int i = 0; i < hand.length; i++) {
                if (hand[i] != '!' && hand[i] != '_') {
                    final char[] newHand = modifiedHand(hand, i);
                    final Entry next = current.children[getIndex(hand[i])];
                    if (next != null ) {
                        result = generatePrefixes(newHand, spaceBefore - 1, word, str + next, next, result);
                    }
                }
            }
        }
        return result;
    }
    /**
     * Finds the best word that starts with a letter already on board
     * @param word
     * @param hand
     * @param result
     * @param col
     * @param row
     * @param orientation
     * @param spaceLeft
     * @return
     */
    private ScrabbleWord startOnBoard(final ScrabbleWord word, final char[] hand, final Entry current, final String prefix,
                                      ScrabbleWord result) {
        final String wordOnBoard = word.getScrabbleWord();
        for (int i = 0; i < wordOnBoard.length(); i++) {
            final char c = wordOnBoard.charAt(i);
            final int index = getIndex(c);
            final Entry firstLetter = current.children[index];
            if (current.children[index] != null) {
                if (word.getOrientation() == 'v') {
                    final int spaceLeft = BOARD_SIZE - (word.getStartColumn() + 1);
                    final int newRow = word.getScrabbleWord().indexOf(firstLetter.toString()) + word.getStartRow();
                    final int newCol = word.getStartColumn() - prefix.length();
                    result = getSuffixes(firstLetter, prefix + firstLetter, spaceLeft, hand, result, newCol, newRow, 'h');
                } else {
                    final int spaceLeft = BOARD_SIZE - (word.getStartRow() + 1);
                    final int newCol = word.getScrabbleWord().indexOf(firstLetter.toString()) + word.getStartColumn();
                    final int newRow = word.getStartRow() - prefix.length();
                    result = getSuffixes(firstLetter, prefix + firstLetter, spaceLeft, hand, result, newCol, newRow, 'v');
                }
            }
        }
        return result;
    }
    /**
     * Given a char, it returns it's index in alphabet. Assumes char is A-Z
     * @param c
     * @return
     */
    private int getIndex(final char c) {
        final byte b = (byte) (Character.hashCode(c));
        final int index = (b + OFFSET) % ALPHABET.length;
        return index;
    }
    /**
     * if current entry is the end of a word
              check to see if its more points than the currentResult
              if it is, currentResult = entry -> ScrabbleWord
          For each available character c, convert c to it's index in the alphabet array
              This uses hashing to make this constant time
              find the entry next at the same index in currentEntry.children
              Remove c from availableCharacters
              if next entry is the end of a word
                  check to see if its more points than the currentResult
                  if it is, currentResult = entry -> ScrabbleWord
              recurse using nextEntry, the new availableCharacters, and the current best result
          return the best result
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
    private ScrabbleWord getSuffixes(final Entry currentEntry, final String currentString, final int spaceLeft,
	                                  final char[] hand, final ScrabbleWord currentResult,
	                                  final int startCol, final int startRow, final char orientation) {
	    ScrabbleWord result = currentResult;
        if (currentEntry.isWordEnd) {
	        final ScrabbleWord newWord = new ScrabbleWord(currentString, startRow, startCol, orientation);
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
	                char[] newHand = modifiedHand(hand, i);
	                if (c == '_') {
	                    for (int j = 0; j < currentEntry.children.length; j++) {
	                        final Entry newEntry = currentEntry.children[j];
	                        if (newEntry != null) {
	                            result = getSuffixes(newEntry, currentString + "_", spaceLeft - 1, newHand, result, startCol, startRow, orientation);    
	                        }
	                    }
	                } else {
	                    final Entry newEntry = currentEntry.children[getIndex(c)];
                        if (newEntry != null) {
                            result = getSuffixes(newEntry, currentString + newEntry, spaceLeft - 1, newHand, result, startCol, startRow, orientation);    
                        }
	                }
	            }
	        }
	    }
	    return result;
	}
    /**
     * i: index of character to be 'removed'
     * @param hand
     * @param i
     * @return
     */
    private char[] modifiedHand(final char[] hand, final int index) {
        char[] newHand = new char[hand.length];
        for (int j = 0; j < hand.length; j++) {
            newHand[j] = hand[j];
            if (j == index) {
                newHand[j] = '!';
            }
        }
        return newHand;
    }
}
