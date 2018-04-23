/**
  *<p><b>You may add additional methods, but may not change existing methods</b></p>
  *
  * The ScrabbleWord class has four components:
  *
  *<ol>
  *  <li>the word (String)--UPPERCASE letters, underscore '_' represents blank (wildcard)</li>
  *  <li>starting row and column (int)</li>
  *  <li>orientation:  v (vertical) or h (horizontal)  (char)
  *</ol>
  *
  *@author Philip Chan
  */

public class ScrabbleWord implements Comparable<ScrabbleWord> {
    /*
     * Description of modifications:
     * Added OFFSET for use in constant-time access of Letters array
     * Made scrabble word comparable by implementing
     *  getPoints()
     * Added toString for debugging
     * Added equals() to check that we can't return the word already on the board
     */
    //Used in determining index of the letters A-Z
    private static final byte OFFSET = 13;    //Use: (Char.hash + OFFSET) % (LETTERS.length - 1) + 1 = LETTERS.indexOf(Char)
    
    private static final char[] LETTERS =
    {'_', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
     'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    
    private static final int[] LETTERS_SCORE =
    {0, 1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3,
     1, 1, 3, 10,1, 1, 1, 1, 4, 4, 8, 4, 10};

    private static final byte[][] BONUSES = {{4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4}, //Holds the bonus for position [row][col]
                                             {0, 3, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 3, 0},
                                             {0, 0, 3, 0, 0, 0, 1, 0, 1, 0, 0, 0, 3, 0, 0},
                                             {1, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 1},
                                             {0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0},
                                             {0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0},
                                             {0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0},
                                             {4, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 4}, //EDIT MIDDLE TO 1 if he doesn't fix eval
                                             {0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0},
                                             {0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0},
                                             {0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0},
                                             {1, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 1},
                                             {0, 0, 3, 0, 0, 0, 1, 0, 1, 0, 0, 0, 3, 0, 0},
                                             {0, 3, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 3, 0},
                                             {4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4}};
    private String  word;  // word
    private int     startRow, startCol;   // starting row and column of the word
    private char    orientation;  // v for vertical, h for horizontal
    private int     points = -1; 

    /** 
     * default constructor with empty string at (0,0) horizontally
     */
    public ScrabbleWord()
    {
    word = "";
    startRow = 0;
    startCol = 0;
    orientation = 'h';
    }

    /** 
     * constructor with user supplied values
     * @param myWord the corresponding word
     * @param myStartRow starting row
     * @param myStartCol starting column
     * @param myOrientation orientation of the word: v or h
     */
    public ScrabbleWord(String myWord, int myStartRow, int myStartCol, char myOrientation)
    {
    word = myWord;
    startRow = myStartRow;
    startCol = myStartCol;
    orientation = myOrientation;
    }

    /**
     * return the word
     * @return the corresponding word
     */
    public String getScrabbleWord()
    {
    return word;
    }

    /**
     * get the starting row
     * @return the starting row
     */
    public int getStartRow()
    {
    return startRow;
    }

    /**
     * get the starting column
     * @return the starting column
     */
    public int getStartColumn()
    {
    return startCol;
    }

    /**
     * get the orientation
     * @return the orientation
     */
    public int getOrientation()
    {
    return orientation;
    }

    @Override
    /**
     * Used to compare ScrabbleWords to determine the best word
     * Words worth more points are better
     */
    public int compareTo(final ScrabbleWord other) {
        return other.getPoints() - this.getPoints();
    }
    /**
     * Determines the number of points a word is worth.
     * Much of the code is taken from EvalScrabblePlayer
     *
     * Saves the points to save time later
     * @return
     */
    public int getPoints() {
        if (points != -1) { //If points is known
            return points;
        } else { //Calc points, then save points
            int totalScore = 0, bonusForWord = 1;
            final int startColumn = getStartColumn();
            final int startRow = getStartRow();

            for (int i = 0; i < word.length(); i++) {
                final int index;
                if (word.charAt(i) == '_') {
                    index = 0;
                } else {
                    final byte element = (byte) Character.hashCode(word.charAt(i));
                    index = (element + OFFSET) % (LETTERS.length - 1) + 1;
                }

                // find the score for this letter
                int letterPoints = LETTERS_SCORE[index];

                final byte bonus;
                if (orientation == 'h') {
                    bonus = BONUSES[startRow][startColumn + i]; //Find bonus for this position
                } else {
                    bonus = BONUSES[startRow + i][startColumn];
                }
                if (bonus == 1) {
                    letterPoints *= 2;
                } else if (bonus == 2) {
                    letterPoints *= 3;
                } else if (bonus == 3) {
                    bonusForWord *= 2;
                } else if (bonus == 4) {
                    bonusForWord *= 3;
                } 
                // sum them up
                totalScore += letterPoints;
            }
            // final score must multiply the bonus
            totalScore = totalScore * bonusForWord;
            points = totalScore;
            return totalScore;
        }
    }
    /**
     * For debugging
     */
    public String toString() {
        return String.format("%s r: %d c: %d. o:%s p: %d", word, startRow, startCol, orientation, getPoints());
    }
    /**
     * Used in a modified EvalScrabblePlayer to make sure we can't just return the word already on the board
     * @param other
     * @return
     */
    public boolean equals(ScrabbleWord other) {
        if (this.word.equals(other.word)) {
            if (this.getOrientation() == other.getOrientation()) {
                if (this.startCol == other.startCol) {
                    if (this.startRow == other.startRow) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}