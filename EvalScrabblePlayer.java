import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;

/*

  Author: Haoran Chang and Philip Chan
  Email: hchang2014@my.fit.edu
  Pseudocode: Philip Chan

  Usage: EvalScrabblePlayer wordFile [seed] [numOfGames]

  Input:
  wordFile has valid words, one on each line
  numOfGames is the number of different games/boards [optional]
  seed is for generating different boards [optional]

  Description:

  The goal is to evaluate ScrabblePlayer
  Validity and points for words are in the assignment.

  The performance of ScrabblePlayer is measured by:

  a.  points: total points of found word
  b.  speed: time in second for finding words
  c.  space consumption: memory consumption
  d.  overall score--(Points^2)/sqrt(time * memory)  


  --------Pseudocode for evaluating ScrabblePlayer---------------

     1.  create scrabble player
     ScrabblePlayer player = new ScrabblePlayer(wordFile) // a list of English words

     2.  play scrabble numOfGames times
     board = randomly generate a board
     availableLetters = 7 random letters from the scrabble word distribution 
     playerWord = player.getScrabbleWord(board, availableLetters)

     3.  report performance
     check validity of player word according to the rules in the assignment
     calculate points as in the assignment
     report performance

 */


public class EvalScrabblePlayer {

    private static ThreadMXBean bean;  // for measuring cpu time
    
    // we deliberately use a worse approach so that we don't give away better ideas
    // constant for the bonus cells on the board and the points for letters
    private static final String[] BONUS_POS =
    {"30","110","62","82","03","73","143","26","66","86","126","37","117", // double letter score
     "28","68","88","128","011","711","1411","612","812","314","1114",     // double letter score
     
     "51","91","15","55","95","135","19","59","99","139","513","913",      // triple letter score
     
     "11","22","33","44","113","212","311","410","131","122","113",        // double word score
     "104","1010","1111","1212","1313",                                    // double word score
     
     "00","70","07","014","140","147","714","1414"};                       // triple word score
     
     private static final String[] BONUS =
     {"2L","2L","2L","2L","2L","2L","2L","2L","2L","2L","2L","2L","2L",
      "2L","2L","2L","2L","2L","2L","2L","2L","2L","2L","2L",
      
      "3L","3L","3L","3L","3L","3L","3L","3L","3L","3L","3L","3L",
      
      "2W","2W","2W","2W","2W","2W","2W","2W","2W","2W","2W",
      "2W","2W","2W","2W","2W",
      
      "3W","3W","3W","3W","3W","3W","3W","3W"};
    
    // points for letters
    private static final char[] LETTERS =
    {'_', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
     'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final int[] LETTERS_SCORE =
    {0, 1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3,
     1, 1, 3, 10,1, 1, 1, 1, 4, 4, 8, 4, 10 };

    public static void main(String[] args) throws IOException {

        if (args.length < 1 || args.length > 3) {
            System.err.println("Usage: EvalScrabblePlayer wordFile [seed] [numOfGames]");
            System.exit(-1);
        }

        //Default seed if second argument is not passed
        long seed = 123456789;
        if ((args.length == 2) || (args.length == 3)) {
            seed = Long.parseLong(args[1]);
        }

        //Default number of games if third argument is not passed
        int numOfGames = 1;
        if (args.length == 3) {
            numOfGames = Integer.parseInt(args[2]);
        }

        // create a bean object for getting cpu time
        bean = ManagementFactory.getThreadMXBean();
        if (!bean.isCurrentThreadCpuTimeSupported()) {
            System.err.println("cpu time not supported, use wall-clock time:");
            System.err.println("Use System.nanoTime() instead of bean.getCurrentThreadCpuTime()");
            System.exit(-1);
        }

        // create our dictionary of words
        Scanner           dictFile = new Scanner(new File(args[0]));
        ArrayList<String> dictionary = new ArrayList<>();
        while (dictFile.hasNext()) {
            dictionary.add(dictFile.nextLine().toUpperCase());
        }
        // create the scrabble player and play scrabble
        ScrabblePlayer player = createScrabblePlayer(args[0]);
        playScrabble(player, dictionary, numOfGames, seed);

        ScrabblePlayer player2 = player;  // keep player used to avoid garbage collection of player
    }


    /*
     *  create the scrabble player and return the player object
     *  report time and space consumption
     */
    private static ScrabblePlayer createScrabblePlayer(String dictFile) 
    {
        //Preprocessing in ScrabblePlayer
        System.out.println("Preprocessing in ScrabblePlayer...");

        long startPreProcTime = bean.getCurrentThreadCpuTime();
        ScrabblePlayer player = new ScrabblePlayer(dictFile);
        long endPreProcTime = bean.getCurrentThreadCpuTime();

        //Stop if pre-processing runs for more than 5 minutes.
        double processingTimeInSec = (endPreProcTime - startPreProcTime) / 1.0E9;
        if (processingTimeInSec > 300) {
            System.err.println("Preprocessing time \"" + processingTimeInSec + " sec\" is too long...");
            System.exit(-1);
        }

        // report time and memory spent on preprocessing
        DecimalFormat df = new DecimalFormat("0.####E0");
        System.out.println("Pre-processing in seconds (not part of performance): " + df.format(processingTimeInSec));
        System.out.println("Used memory after pre-processing in bytes (not part of performance): " + peakMemoryUsage());

    return player;
    }


    /*
     *  use the player object to play scrabble with a random seed
     *  measure time, space, points
     */
    private static void playScrabble(ScrabblePlayer player, 
                 ArrayList<String> dictionary,
                 int numOfGames, long seed)
    {
        System.out.println("Playing Scrabble...");

    int       totalPoints = 0;
    long      totalElapsedTime = 0;
    char[][]  board = new char[15][15];
    char[]    availableLetters = new char[7]; 
    Random    rand = new Random(seed);
    
    for (int game = 0; game < numOfGames; game++)
        {
        //to do: initialize the board with spaces
        //       add a random word of at most length 7 from the dictionary
        ScrabbleWord initialWord = generateBoard(board, dictionary, rand);
        
        //to do: Randomly pick 7 letters according to the distribution of letters in
        //       the wiki page in the assignment
        generateAvailableLetters(availableLetters, rand);
        //System.out.printf("available Letters: %s%n", new String(availableLetters));
                
        // the player might change board and/or availableLetters, give the player a clone
        char[][] boardClone = board.clone();
        char[]   availableLettersClone = availableLetters.clone();

        //Calculate the time taken to find the words on the board
        long startTime = bean.getCurrentThreadCpuTime();
        //Play the game of Scrabble and find the words
        ScrabbleWord playerWord = player.getScrabbleWord(boardClone, availableLettersClone);
        
        long endTime = bean.getCurrentThreadCpuTime();

        //System.out.println(endTime - startTime);
        if ((endTime - startTime)/1.0E9 > 100)  // longer than 1 second
            {
            System.err.println("player.getScrabbleWord() exceeded 1 second");
            System.exit(-1);
            }
        totalElapsedTime += (endTime - startTime);

        //Calculate points for the words found
        totalPoints += calculatePoints(playerWord, initialWord, board, availableLetters, dictionary);
        //System.out.println("Total: " + totalPoints);
        }

        reportPerformance(totalPoints, totalElapsedTime, peakMemoryUsage(), 
                          numOfGames);
    }


    /**
     * Setup the board
     */
    private static ScrabbleWord generateBoard(char[][] board, ArrayList<String> dictionary, Random rand)
    {
        // initialize the board
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                board[i][j] = ' ';
        
        // randomly choose a word
        int randomIndex = rand.nextInt(dictionary.size());
        String initialWord = dictionary.get(randomIndex);
        while (initialWord.length() > 7)
        {
            randomIndex = rand.nextInt(dictionary.size());
            initialWord = dictionary.get(randomIndex);
        }
        
        // choose the orientation and position, put the initial word onto the board
        boolean flipCoin = rand.nextBoolean();
        char orientation = 'h';
        int rowPos, colPos, startRow, startCol;
        // flipCoin == true, then horizontal
        if (flipCoin)
        {
            orientation = 'h';
            // randomly choose the rowPos
            rowPos = rand.nextInt(board.length);
            // since it is horizontal, rowPos will not change
            // but colPos cannot not be 15
            // for example, if word.length = 6, then Max(colPos) = 9 = 15 - 6
            colPos = rand.nextInt(board[0].length - initialWord.length());
            // store the starting position
            startRow = rowPos;
            startCol = colPos;
            // horizontal, so each letter has the same rowID
            for (int i = 0; i < initialWord.length(); i++)
            {
                board[rowPos][colPos] = initialWord.charAt(i);
                colPos++;
            }
        }
        // otherwise, vertical
        else
        {
            orientation = 'v';
            // similarly, colPos = 0 ~ 14, rowPos = 0 ~ (15 - word.length)
            colPos = rand.nextInt(board[0].length);
            rowPos = rand.nextInt(board.length - initialWord.length());
            // store the starting position
            startRow = rowPos;
            startCol = colPos;
            // vertical, so each letter has the same colID
            for (int i = 0; i < initialWord.length(); i++)
            {
                board[rowPos][colPos] = initialWord.charAt(i);
                rowPos++;
            }
        }
        
        ScrabbleWord wordOnBoard = new ScrabbleWord(initialWord, startRow, startCol, orientation);
        return wordOnBoard;
    }
    
    
    /**
    * Haoran: randomly pick up 7 letters to be the available letters
    *         according to the distribution on wiki page
    */
    private static void generateAvailableLetters(char[] availableLetters, Random rand)
    {
        // initial distribution (total 100 tiles):
        // blank:2 A:9 B:2 C:2 D:4 E:12 F:2 G:3 H:2 I:9 J:1 K:1 L:4 M:2
        //     N:6 O:8 P:2 Q:1 R:6 S:4  T:6 U:4 V:2 W:2 X:1 Y:2 Z:1
        int[] distribution = {2, 9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2,
                              6, 8, 2, 1, 6, 4,  6, 4, 2, 2, 1, 2, 1};
        
        char[] allCharacters = {'_', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        
        int tilesNum = 100;
        for (int i = 0; i < 7; i++)
        {
            int randomNum = rand.nextInt(tilesNum) + 1;
            tilesNum = tilesNum - 1;
            
            // according to the randomNum, find the character
            int cumulative = 0;
            int counter = 0;
            while (randomNum > cumulative)
            {
                cumulative += distribution[counter];
                counter++;
            }
            
            // counter - 1 is the actual index of distribution
            // which letter is chosen
            availableLetters[i] = allCharacters[counter - 1];
            
            // distribution[counter - 1] should minus 1 because one tile has been picked
            distribution[counter - 1]--;
        }
    }
    

    /*
     * return peak memory usage in bytes
     *
     * adapted from

     * https://stackoverflow.com/questions/34624892/how-to-measure-peak-heap-memory-usage-in-java 
     */
    private static long peakMemoryUsage() 
    {

    List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
    long total = 0;
    for (MemoryPoolMXBean memoryPoolMXBean : pools)
        {
        if (memoryPoolMXBean.getType() == MemoryType.HEAP)
        {
            long peakUsage = memoryPoolMXBean.getPeakUsage().getUsed();
            // System.out.println("Peak used for: " + memoryPoolMXBean.getName() + " is: " + peakUsage);
            total = total + peakUsage;
        }
        }

    return total;
    }

    /*
     * report performance of the scrabble player
     * based on time, space, and points
     */
    private static void reportPerformance(int totalPoints, long totalElapsedTime, long memory, 
                  int numOfGames)
    {
        // avoid divided by zero
        if (totalElapsedTime <= 0)
            totalElapsedTime = 1;
        if (memory <= 0)
            memory = 1;
        
        double avgPoints = totalPoints / numOfGames;
        System.out.printf("Average Points: %.4f\n", avgPoints);

        System.out.println("totalElapsedTime " + totalElapsedTime);

        //Convert elapsed time into seconds, and calculate the Average time
        double avgTime = totalElapsedTime / 1.0E9 / numOfGames;
        //To format the Average time upto 4 decimal places.
        DecimalFormat df = new DecimalFormat("0.####E0"); 
        System.out.println("Time in seconds: " + df.format(avgTime));

        System.out.println("Used memory in bytes: " + memory);

        //Overall Performance
        double pointsSquared = avgPoints * avgPoints;
        if (avgPoints < 0)
            pointsSquared = - pointsSquared;

        System.out.printf("Overall Performance: %.4f\n",  pointsSquared / Math.sqrt(avgTime * memory));

    }


    /**
     * to do:
     *
     * Checks if the word is valid and assigns positive points for valid word and negative points for invalid word
     * 
     * @param playerWord word returned by the player
     * @param board The exisiting board before the adding the playerWord
     * @param dictionary dictionary for validity check
     * @return Positive or negative points for the word
     */
    private static int calculatePoints(ScrabbleWord playerWord, ScrabbleWord initialWord, char[][] board,
                                       char[] availableLetters, ArrayList<String> dictionary) 
    {
        // check if it is a valid word
        ScrabbleWord returnWord = validPlayWord(playerWord, initialWord, board, availableLetters, dictionary);
        if (returnWord == null)
            return 0;
        
        // calculate the points based on wiki
        int totalPoints = singleWordPoints(playerWord);
        
        // if a new word is created
        String playerW = playerWord.getScrabbleWord();
        if (!returnWord.getScrabbleWord().equals(playerW))
            totalPoints += singleWordPoints(returnWord);
        return totalPoints;
    }
    
    
    // compute the points for a single word
    private static int singleWordPoints(ScrabbleWord playerWord)
    {        
        int totalScore = 0, bonusForWord = 1;
        
        String playerW = playerWord.getScrabbleWord();
        int rowID = playerWord.getStartRow();
        int colID = playerWord.getStartColumn();
        
        for (int i = 0; i < playerW.length(); i++)
        {
            char letterInWord = playerW.charAt(i);
            
            // find the score for this letter
            int letterPoints = 0;
            for (int k = 0; k < LETTERS.length; k++)
            {
                if (LETTERS[k] == letterInWord)
                    letterPoints = LETTERS_SCORE[k];
            }
            
            //System.out.printf("The %d th letter of %s is %c: %d points, ", i, playerW, letterInWord, letterPoints);
            
            //System.out.printf("pos (row, col): (%d, %d)%n", rowID, colID);
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
            
            // sum them up
            totalScore = totalScore + letterPoints;
        }
        // final score must multiply the bonus
        totalScore = totalScore * bonusForWord;
        
        // return
        return totalScore;
    }

    /**
    * function to check if the play word is valid or not
    * @param playWord: a ScrabbleWord that the player wants to add
    * @param board: The initial board before the adding the playerWord
    * @param dictionary: dictionary to check if the word is in the dictionary
    * @return ScrabbleWord: if invalid, return empty string;
    *                       if valid, return same playerWord if no new word created
    *                                 or return the new Word
    */
    private static ScrabbleWord validPlayWord(ScrabbleWord playWord, ScrabbleWord initialWord, char[][] board,
                                 char[] availableLetters, ArrayList<String> dictionary)
    {
        // default return value
        ScrabbleWord returnWord = playWord;
        
        // Invalid case 0: empty string
        if (playWord.getScrabbleWord().equals(""))
            return null;
        
        // Invalid case 1: the playWord is not in the dictionary
        if (!isInDictionary(playWord.getScrabbleWord(), dictionary))
        {
            //System.out.println(playWord.getScrabbleWord() + " not in the dictionary.");
            //return false;
            return null;
        }
        
        //////////////////////////////////////////////////////////////////////////
        // Invalid case 2: out of boundary
        int maxIndex = 0;
        if (playWord.getOrientation() == 'h')
            maxIndex = playWord.getStartColumn() + playWord.getScrabbleWord().length() - 1;
        else
            maxIndex = playWord.getStartRow() + playWord.getScrabbleWord().length() - 1;
        if (maxIndex > board.length)
        {
            //System.out.println("out of boundary");
            //return false;
            return null;
        }
        // startRow/startColumn less than zero
        else if (playWord.getStartRow() < 0 || playWord.getStartColumn() < 0)
        {
            //System.out.println("out of boundary");
            //return false;
            return null;
        }
        
        ///////////////////////////////////////////////////////////////////////////////
        // Invalid case 3: the playWord use letters that are not in availableLetters
        // Before checking, must remove the intersection part of the playWord and the starting word        
        // remove the intersection part
        String additionLetters = findAdditionLetters(initialWord, playWord);
        
        // use arraylist to store the letters
        ArrayList<String> playerLetters = new ArrayList<String>();        
        for (char i: additionLetters.toCharArray())
            playerLetters.add(Character.toString(i));
        
        ArrayList<String> validLetters = new ArrayList<String>();
        for (char i: availableLetters)
            validLetters.add(Character.toString(i));
        
        // check every letter in additionLetters
        for (int i = 0; i < additionLetters.length(); i++)
        {
            String tempChar = Character.toString(additionLetters.charAt(i));
            // if it is an available letter, remove it from both lists
            if (validLetters.contains(tempChar))
            {
                playerLetters.remove(tempChar);
                validLetters.remove(tempChar);
            }
        }
        // not empty means some letters that are not in availableLetters are used
        if (!playerLetters.isEmpty()){
            return null;
        }
        
        ////////////////////////////////////////////////////////////////////////////////
        // Invalid case 4: connection
        String initialW = initialWord.getScrabbleWord();
        String playW = playWord.getScrabbleWord();
        // same orientation, playWord is valid only when it contains initialWord
        if (playWord.getOrientation() == initialWord.getOrientation())
        {
            if (!playW.contains(initialW))
                return null;
        }
        // different orientation, two valid cases: 1. they are intersecting
        //                                         2. one letter in play word extends the
        //                                            initialWord at the end or in the front
        else
        {
            // if two words are not intersecting
            if (!isIntersecting(playWord, initialWord))
            {
                // check if extending (valid case 2)
                returnWord = isExtending(playWord, initialWord, dictionary);
                return returnWord;
            }
            // intersection is true
            else
                returnWord = playWord;
        }
        
        // all the cases have been checked, return
        //System.out.println("returnWord "+ returnWord.getScrabbleWord());
        return returnWord;
    }

    
    // check if a word is in the dictionary
    private static boolean isInDictionary(String word, ArrayList<String> dictionary)
    {
        // if not blank tile
        if (!word.contains("_"))
        {
            // if the word doesn't contain blank tile, directly check
            if (!dictionary.contains(word))
            {
                //System.out.println(word + " not in the dictionary.");
                return false;
            }
            else
                return true;
        }
        else
        {
            int indexOfBlank = word.indexOf("_");
            // try different letters (a-z)
            for (int i = 0; i <= 26; i++)
            {
                char trial = (char)(i + 65); // (int)'A' = 65
                // change blank to trial
                String testingWord = word.substring(0, indexOfBlank) + trial + 
                                     word.substring(indexOfBlank + 1, word.length());
                
                // check if this testingWord in dictionary
                // recursive call because it may have multiple blanks
                if (isInDictionary(testingWord, dictionary))
                    return true;
            }
            
            return false;
        }
    }
    
    
    /**
    * function to find the letters in the playWord that are added by the player
    * @param initialWord: a ScrabbleWord in the board at the beginning
    * @param playWord: a ScrabbleWord that the player wants to add
    * @return additionLetters: kind of playWord - initialWord
    */
    private static String findAdditionLetters(ScrabbleWord initialWord, ScrabbleWord playWord)
    {
        String additionLetters;
        String playerW = playWord.getScrabbleWord();
        String initialW = initialWord.getScrabbleWord();
        
        // find the intersection according to the orientation
        // same orientation, remove the whole initialWord from playWord
        if (playWord.getOrientation() == initialWord.getOrientation())
        {
            additionLetters = playerW.replace(initialW, "");
        }
        // different orientation
        else
        {
            int intersect = 0;
            if (playWord.getOrientation() == 'h')
                intersect = initialWord.getStartColumn() - playWord.getStartColumn();
            else
                intersect = initialWord.getStartRow() - playWord.getStartRow();
            // remove intersect letter
            additionLetters = playerW.substring(0, intersect) + 
                              playerW.substring(intersect + 1, playerW.length());
        }
        
        return additionLetters;
    }
    
    
    /**
    * function to check if two words are intersecting
    * @param playWord: a ScrabbleWord that the player wants to add
    * @param initialWord: a ScrabbleWord in the board at the beginning
    * @return boolean: true when intersecting
    */
    private static boolean isIntersecting(ScrabbleWord playWord, ScrabbleWord initialWord)
    {
        // initialize the return value
        boolean intersection = false;
        // string of the two ScrabbleWord objects
        String playW = playWord.getScrabbleWord();
        String initialW = initialWord.getScrabbleWord();
        
        // start row and column for the playWord
        int pStartRow = playWord.getStartRow(), pStartCol = playWord.getStartColumn();
        
        // start row and column for the initialWord
        int iStartRow = initialWord.getStartRow(), iStartCol = initialWord.getStartColumn();
        
        // end row and column for both playWord and initialWord
        // different according to the orientation
        int pEndRow, pEndCol, iEndRow, iEndCol;
        
        // when playWord is horizontal, then initialWord is vertical
        if (playWord.getOrientation() == 'h')
        {
            // for play word,
            // endRow = startRow, endCol = startCol + playWord.length - 1
            pEndRow = pStartRow;
            pEndCol = pStartCol + playW.length() - 1;
            
            // for initial word, it is vertical
            // endCol = startCol, endRow = StartCol + initialWord.length - 1
            iEndRow = iStartRow + initialW.length() - 1;
            iEndCol = iStartCol;
            
            // if they are intersecting, then (pStartCol <= iStartCol <= pEndCol) AND
            //                                (iStartRow <= pStartRow <= iEndRow)
            if ((pStartCol <= iStartCol && iStartCol <= pEndCol) && 
                (iStartRow <= pStartRow && pStartRow <= iEndRow))
            {
                // check if the letters are then same
                if (playW.charAt(iStartCol - pStartCol) == initialW.charAt(pStartRow - iStartRow))
                    intersection = true;
                else
                    intersection = false;
            }
            // not intersecting
            else
                intersection = false;
        }
        // when playWord is vertical, then initialWord is horizontal
        else
        {
            // since playWord is vertical, then:
            // endRow = startRow + playWord.length - 1, endCol = startCol
            pEndRow = pStartRow + playW.length() - 1;
            pEndCol = pStartCol;
            
            // for initial word, it is horizontal
            // endCol = startCol + initialWord.length - 1, endRow = StartCol
            iEndRow = iStartRow;
            iEndCol = iStartCol + initialW.length() - 1;
            
            // if they are intersecting, then (pStartRow <= iStartRow <= pEndRow) AND
            //                                (iStartCol <= pStartCol <= iEndCol)
            if ((pStartRow <= iStartRow) && (iStartRow <= pEndRow) &&
                (iStartCol <= pStartCol) && (pStartCol <= iEndCol))
            {
                // check if the letters are then same 
                if (playW.charAt(iStartRow - pStartRow) == initialW.charAt(pStartCol - iStartCol))
                    intersection = true;
                else
                    intersection = false;
            }
            // not intersecting
            else
                intersection = false;
        }
        return intersection;
    }
    
    /**
    * function to check if playWord extends initialWord
    * @param playWord: a ScrabbleWord that the player wants to add
    * @param initialWord: a ScrabbleWord in the board at the beginning
    * @param dictionary
    * @return a ScrabbleWord can be a newWord or playWord
    */
    private static ScrabbleWord isExtending(ScrabbleWord playWord, ScrabbleWord initialWord,
                                            ArrayList<String> dictionary)
    {
        ScrabbleWord newWord;
        String playW = playWord.getScrabbleWord();
        String initialW = initialWord.getScrabbleWord();
        
        // start row and column for the playWord
        int pStartRow = playWord.getStartRow(), pStartCol = playWord.getStartColumn();
        
        // start row and column for the initialWord
        int iStartRow = initialWord.getStartRow(), iStartCol = initialWord.getStartColumn();
        
        // end row and column for both playWord and initialWord
        // different according to the orientation
        int pEndRow, pEndCol, iEndRow, iEndCol;
        
        // when playWord is horizontal, then initialWord is vertical
        if (playWord.getOrientation() == 'h')
        {
            // for play word,
            // endRow = startRow, endCol = startCol + playWord.length - 1
            pEndRow = pStartRow;
            pEndCol = pStartCol + playW.length() - 1;
            
            // for initial word, it is vertical
            // endCol = startCol, endRow = StartCol + initialWord.length - 1
            iEndRow = iStartRow + initialW.length() - 1;
            iEndCol = iStartCol;
            
            // if playWord extends intialWord, then (pStartCol <= iStartCol <= pEndCol) AND
            //                                      (pStartRow == iStartRow - 1 || pStartRow == iEndRow + 1)
            if ((pStartCol <= iStartCol && iStartCol <= pEndCol) && 
                (pStartRow == iStartRow - 1 || pStartRow == iEndRow + 1))
            {
                // find the letter in playWord that extends the initialWord
                String extraLetter = Character.toString(playW.charAt(iStartCol - pStartCol));
                
                // this letter can be the head or the tail of the new word
                String newWordString;
                if (pStartRow == iStartRow - 1) // head
                    newWordString = extraLetter + initialW;
                else
                    newWordString = initialW + extraLetter;
                // in the dictionary?
                if (!isInDictionary(playWord.getScrabbleWord(), dictionary))
                    newWord = null;
                else
                    newWord = new ScrabbleWord(newWordString, iStartRow - 1, iStartRow, 'v');
            }
            // invalid
            else
                newWord = null;
        }
        // when playWord is vertical, then initialWord is horizontal
        else
        {
            // since playWord is vertical, then:
            // endRow = startRow + playWord.length - 1, endCol = startCol
            pEndRow = pStartRow + playW.length() - 1;
            pEndCol = pStartCol;
            
            // for initial word, it is horizontal
            // endCol = startCol + initialWord.length - 1, endRow = StartCol
            iEndRow = iStartRow;
            iEndCol = iStartCol + initialW.length() - 1;
            
            // if they are intersecting, then (pStartRow <= iStartRow <= pEndRow) AND
            //                                (iStartCol <= pStartCol <= iEndRow)
            if ((pStartRow <= iStartRow) && (iStartRow <= pEndRow) &&
                (pStartCol == iStartCol - 1) || (pStartCol == iEndCol + 1))
            {
                // find the letter in playWord that extends the initialWord
                String extraLetter = Character.toString(playW.charAt(iStartRow - pStartRow));
                
                // this letter can be the head or the tail of the new word
                String newWordString;
                if (pStartCol == iStartCol - 1) // head
                    newWordString = extraLetter + initialW;
                else
                    newWordString = initialW + extraLetter;
                // in the dictionary?
                if (!isInDictionary(playWord.getScrabbleWord(), dictionary))
                    newWord = null;
                else
                    newWord = new ScrabbleWord(newWordString, iStartCol - 1, iStartCol, 'h');
            }
            // invalid
            else
                newWord = null;
        }
        
        // finally, return the word
        return newWord;
    }

}