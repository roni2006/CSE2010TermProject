
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
    private static final char[] LETTERS =
    {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
     'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    
    private static final int[] LETTERS_SCORE =
    {0, 1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3,
     1, 1, 3, 10,1, 1, 1, 1, 4, 4, 8, 4, 10 };
    
    private String  word;  // word
    private int     startRow, startCol;   // starting row and column of the word
    private char    orientation;  // v for vertical, h for horizontal

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
    public int compareTo(ScrabbleWord other) {
        final int pointDifference = getPoints(other.word) - other.getPoints(other.word);
        if (pointDifference == 0) {
            //TODO determine how to order in this case
            return other.word.length() - this.word.length();
        } else {
            return pointDifference;
        }
    }
    
    private int getPoints(String str) {

        int totalScore = 0, bonusForWord = 1;
        
        for (int i = 0; i < str.length(); i++)
        {
            char letterInWord = str.charAt(i);
            
            // find the score for this letter
            int letterPoints = 0;
            for (char tempChar: LETTERS)
            {
                if (tempChar == letterInWord)
                    letterPoints = LETTERS_SCORE[tempChar];
            }
            
            //System.out.printf("The %d th letter of %s is %c: %d points, ", i, str, letterInWord, letterPoints);
            
            /*
            //System.out.printf("pos (row, col): (%d, %d), ", rowID, colID);
            // find the score on board
            String position = Integer.toString(rowID) + Integer.toString(colID);
            String bonusFromBoard = "";
            
            // find the score on board if exist
            for (int j = 0; j < BONUS_POS.length; j++)
            {
                if (position.equals(BONUS_POS[j]))
                    bonusFromBoard = BONUS[j];
            }
            
            // double/triple letter score
            if (bonusFromBoard.equals("2L"))
                letterPoints = letterPoints * 2;
            else if (bonusFromBoard.equals("3L"))
                letterPoints = letterPoints * 3;
            // double/triple word score
            else if (bonusFromBoard.equals("2W"))
                bonusForWord = bonusForWord * 2;
            else if (bonusFromBoard.equals("3W"))
                bonusForWord = bonusForWord * 3;
            else
                bonusForWord = bonusForWord * 1;
            
            // move to the next cell
            if (playerWord.getOrientation() == 'h')
                colID++;
            else
                rowID++;
            */
            // sum them up
            totalScore = totalScore + letterPoints;
                
        }
        // final score must multiply the bonus
        totalScore = totalScore * bonusForWord;
        return totalScore;
    }
    public String toString() {
        return String.format("%s r: %d c: %d. o:%s", word, startRow, startCol, orientation);
    }
}
